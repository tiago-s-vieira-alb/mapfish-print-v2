package e2etests.org.mapfish.print;

import org.junit.Test;

/**
 * Test printing maps using WMS.
 * Created by Jesse on 1/9/14.
 */
public class PrintOSMInteractiveTest extends AbstractPrintTest {

    @Test
    public void testPrintWmts_Tiger_NY_EPSG_900913_1_0_0_Scale_Full_KVP() throws Exception {
        interactiveComparePdfVsExpectedImage(
                "/printwmts_osm_new_york_EPSG_900913/config-onlyMap.yaml",
                "/printwmts_osm_new_york_EPSG_900913/scale-osm.json",
                "/printwmts_osm_new_york_EPSG_900913/expected.png");
    }

}
