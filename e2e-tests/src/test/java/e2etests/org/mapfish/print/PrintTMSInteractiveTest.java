package e2etests.org.mapfish.print;

import org.junit.Test;

/**
 * Test printing maps using WMS.
 * Created by Jesse on 1/9/14.
 */
public class PrintTMSInteractiveTest extends AbstractPrintTest {

    @Test
    public void testPrint_TMS_Tiger_NY_EPSG_900913_1_0_0_Scale_Full_KVP() throws Exception {
        interactiveComparePdfVsExpectedImage(
                "/print_tms_tyger-ny_EPSG_900913/config-onlyMap.yaml",
                "/print_tms_tyger-ny_EPSG_900913/scale-tms1_0_0.json",
                "/print_tms_tyger-ny_EPSG_900913/expected.png");
    }

}
