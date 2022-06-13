package e2etests.org.mapfish.print;

import org.junit.Test;

/**
 * Test printing maps using WMS.
 * Created by Jesse on 1/9/14.
 */
public class PrintVectorInteractiveTest extends AbstractPrintTest {

    @Test
    public void testPrint_vector_new_york_EPSG_900913() throws Exception {
        interactiveComparePdfVsExpectedImage(
                "/print_vector_new_york_EPSG_900913/config-onlyMap.yaml",
                "/print_vector_new_york_EPSG_900913/scale-vector.json",
                "/print_vector_new_york_EPSG_900913/expected.png");
    }

}
