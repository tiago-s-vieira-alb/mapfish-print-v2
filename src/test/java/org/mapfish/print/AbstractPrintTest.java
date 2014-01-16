package org.mapfish.print;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.mapfish.print.config.layout.Layout;
import org.mapfish.print.utils.PJsonObject;
import org.pvalsecc.misc.FileUtilities;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

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

        addImage(pane, expected, "Expected", false);
        addImage(pane, actual, "Actual", true);


        frame.setSize(pane.getPreferredSize());
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
    }

    private void addImage(Container pane, BufferedImage image, String name, boolean okButton) {
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

            return pages.get(0).convertToImage(BufferedImage.TYPE_4BYTE_ABGR, 90);
        } finally {
            pdf.close();
        }
    }


    private String loadResource(String configName) throws IOException {
        final InputStream configResource = AbstractPrintTest.class.getResourceAsStream(configName);
        return FileUtilities.readWholeTextStream(configResource, "UTF-8")
                .replace("@@port@@", PORT)
                .replace("@@host@@", HOST);
    }
}
