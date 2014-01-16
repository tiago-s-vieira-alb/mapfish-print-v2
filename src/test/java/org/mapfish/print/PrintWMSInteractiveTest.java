package org.mapfish.print;

import org.junit.Test;

/**
 * Test printing maps using WMS.
 * Created by Jesse on 1/9/14.
 */
public class PrintWMSInteractiveTest extends AbstractPrintTest {

    @Test
    public void testPrintWms_UsaPopulation_EPSG_4326_1_3_0() throws Exception {
        interactiveComparePdfVsExpectedImage(
                "/printwms_UsaPopulation_EPSG_4326/config-onlyMap.yaml",
                "/printwms_UsaPopulation_EPSG_4326/wms1_3_0.json",
                "/printwms_UsaPopulation_EPSG_4326/expected.png");
    }

    @Test
    public void testPrintWms_SpearfishStreams_EPSG_26713_1_3_0() throws Exception {
        interactiveComparePdfVsExpectedImage(
                "/printwms_SpearfishStreams_EPSG_26713/config-onlyMap.yaml",
                "/printwms_SpearfishStreams_EPSG_26713/wms1_3_0.json",
                "/printwms_SpearfishStreams_EPSG_26713/expected.png");
    }
    @Test
    public void testPrintWms_UsaPopulation_EPSG_4326_1_1_1() throws Exception {
        interactiveComparePdfVsExpectedImage(
                "/printwms_UsaPopulation_EPSG_4326/config-onlyMap.yaml",
                "/printwms_UsaPopulation_EPSG_4326/wms1_1_1.json",
                "/printwms_UsaPopulation_EPSG_4326/expected.png");
    }

    @Test
    public void testPrintWms_SpearfishStreams_EPSG_26713_1_1_1() throws Exception {
        interactiveComparePdfVsExpectedImage(
                "/printwms_SpearfishStreams_EPSG_26713/config-onlyMap.yaml",
                "/printwms_SpearfishStreams_EPSG_26713/wms1_1_1.json",
                "/printwms_SpearfishStreams_EPSG_26713/expected.png");
    }
}
