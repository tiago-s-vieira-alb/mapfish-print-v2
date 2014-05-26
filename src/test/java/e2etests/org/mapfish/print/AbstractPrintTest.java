package e2etests.org.mapfish.print;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdfviewer.PageDrawer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.mapfish.print.Constants;
import org.mapfish.print.MapPrinter;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.ShellMapPrinter;
import org.mapfish.print.config.layout.Layout;
import org.mapfish.print.utils.PJsonObject;
import org.pvalsecc.misc.FileUtilities;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static org.junit.Assert.assertTrue;

/**
 * Common useful methods for issuing print requests and validating the results.
 * <p/>
 * Created by Jesse on 1/9/14.
 */
public abstract class AbstractPrintTest {

    private static final String HOST = System.getProperty("host", "localhost");
    private static final String PORT = System.getProperty("port", "9876");

    protected final void interactiveComparePdfVsExpectedImage(String configFile, String jsonFile, String expectedImage) throws Exception {
        final BufferedImage expected = ImageIO.read(AbstractPrintTest.class.getResourceAsStream(expectedImage));
        BufferedImage actual = getMapImage(configFile, jsonFile);


        final ArrayBlockingQueue<Boolean> testResult = new ArrayBlockingQueue<Boolean>(2);

        JFrame frame = new JFrame();

        final Container pane = frame.getContentPane();

        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        final Button okButton = new Button("Test Pass");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testResult.add(true);
            }
        });
        buttonPanel.add(okButton);

        final Button nokButton = new Button("Test Failure");
        nokButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testResult.add(false);
            }
        });
        buttonPanel.add(nokButton);
        buttonPanel.setSize(100, 30);
        buttonPanel.setPreferredSize(new Dimension(100, 30));
        pane.add(buttonPanel);

        addImage(pane, expected, "Expected");
        addImage(pane, actual, "Actual");


        final Dimension preferredSize = pane.getPreferredSize();
        frame.setSize((int) (preferredSize.width * 1.5), (int)(preferredSize.height + 30 * 1.5));
        frame.setAlwaysOnTop(true);

        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                testResult.add(false);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                windowClosed(e);
            }

        });

        assertTrue(testResult.take().booleanValue());
        frame.setVisible(false);
    }

    private void addImage(Container pane, BufferedImage image, String name) {
        JPanel panel = new JPanel(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel(name));
        panel.add(new JLabel(new ImageIcon(image)));

        pane.add(panel);
    }

    protected final BufferedImage getMapImage(String configFile, String jsonFile) throws Exception {
        AbstractXmlApplicationContext context = new ClassPathXmlApplicationContext(ShellMapPrinter.DEFAULT_SPRING_CONTEXT);
        final String config = loadResource(configFile);
        final MapPrinter printer = context.getBean(MapPrinter.class);
        printer.setConfig(config);

        final PJsonObject jsonSpec = MapPrinter.parseSpec(loadResource(jsonFile));

        final String layoutName = jsonSpec.getString(Constants.JSON_LAYOUT_KEY);
        Layout layout = printer.getConfig().getLayout(layoutName);
        if (layout == null) {
            throw new RuntimeException("Unknown layout '" + layoutName + "'");
        }

        Document doc = new Document(layout.getFirstPageSize(null, jsonSpec));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(doc, outputStream);

        RenderingContext renderingContext = new RenderingContext(doc, writer, printer.getConfig(), jsonSpec, ".", layout,
                Collections.<String, String>emptyMap());

        layout.render(jsonSpec, renderingContext);

        doc.close();
        writer.close();

        PDDocument pdf = PDDocument.load(new ByteArrayInputStream(outputStream.toByteArray()));

        try {
            @SuppressWarnings("unchecked")
            List<PDPage> pages = pdf.getDocumentCatalog().getAllPages();

            return convertToImage(pages.get(0));//.convertToImage(BufferedImage.TYPE_3BYTE_BGR, 72);
        } finally {
            pdf.close();
        }
    }

    protected BufferedImage convertToImage(PDPage pdPage) throws IOException {
        PDRectangle mBox = pdPage.findMediaBox();
        float widthPt = mBox.getWidth();
        float heightPt = mBox.getHeight();
        float scaling = 1;
        int widthPx = Math.round(widthPt * scaling);
        int heightPx = Math.round(heightPt * scaling);
        //TODO The following reduces accuracy. It should really be a Dimension2D.Float.
        Dimension pageDimension = new Dimension( (int)widthPt, (int)heightPt );

        BufferedImage retval = new BufferedImage( widthPx, heightPx, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D)retval.getGraphics();
        Map<Object, Object> hints = new HashMap<Object, Object>();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.addRenderingHints(hints);
        graphics.setBackground( Color.WHITE );
        graphics.clearRect(0, 0, retval.getWidth(), retval.getHeight());
        PageDrawer drawer = new PageDrawer();
        drawer.drawPage( graphics, pdPage, pageDimension );

        graphics.dispose();
        return retval;
    }


    private String loadResource(String configName) throws IOException {
        final InputStream configResource = AbstractPrintTest.class.getResourceAsStream(configName);
        return FileUtilities.readWholeTextStream(configResource, "UTF-8")
                .replace("@@port@@", PORT)
                .replace("@@host@@", HOST);
    }
}
