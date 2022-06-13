package e2etests.org.mapfish.print;

import org.junit.Test;

/**
 * Test printing maps using WMS.
 * Created by Jesse on 1/9/14.
 */
public class PrintOSMInteractiveTest extends AbstractPrintTest {

    @Test
    public void testPrint_osm_new_york_EPSG_900913() throws Exception {
        interactiveComparePdfVsExpectedImage(
                "/print_osm_new_york_EPSG_900913/config-onlyMap.yaml",
                "/print_osm_new_york_EPSG_900913/scale-osm.json",
                "/print_osm_new_york_EPSG_900913/expected.png");
    }

}
