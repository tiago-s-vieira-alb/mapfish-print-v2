package org.mapfish.print;

import org.junit.Test;

/**
 * Test printing maps using WMS
 * Created by Jesse on 1/9/14.
 */
public class PrintWMSInteractiveTest extends AbstractPrintTest {

    @Test
    public void testPrintWMS1_3_0() throws Exception {
        interactiveComparePdfVsExpectedImage("/printwms/wms1_3_0.json", "/printwms/expected.gif");
    }
}
