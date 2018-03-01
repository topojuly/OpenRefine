package org.openrefine.wikidata.exporters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.openrefine.wikidata.schema.entityvalues.ReconEntityIdValue;
import org.openrefine.wikidata.testing.TestingDataGenerator;
import org.testng.annotations.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;

public class QSValuePrinterTest {
    
    private QSValuePrinter printer;
    
    public QSValuePrinterTest() {
        printer = new QSValuePrinter();
    }
    
    void assertPrints(String expectedFormat, Value datavalue) {
        assertEquals(expectedFormat, datavalue.accept(printer));
    }
    
    // Entity id values
    
    @Test
    public void printItemId() {
        assertPrints("Q42", Datamodel.makeWikidataItemIdValue("Q42"));
    }
    
    @Test
    public void printPropertyId() {
        assertPrints("P42", Datamodel.makeWikidataPropertyIdValue("P42"));
    }
    
    @Test
    public void printNewItemId() {
        ReconEntityIdValue id = TestingDataGenerator.makeNewItemIdValue(12345L, "my new item");
        assertNull(id.accept(printer));
        // because no entity was previously created

        QSValuePrinter printerAfterCreate = new QSValuePrinter(id);
        ReconEntityIdValue equalId = TestingDataGenerator.makeNewItemIdValue(12345L, "my other new item");
        assertEquals("LAST", printerAfterCreate.visit(equalId));
        
        ReconEntityIdValue differentId = TestingDataGenerator.makeNewItemIdValue(34567L, "my new item");
        assertNull(printerAfterCreate.visit(differentId));
    }
    
    // Globe coordinates
    
    @Test
    public void printGlobeCoordinate() {
        // I don't see how to avoid the trailing zeros - in any case it's not a big deal because
        // the precision is governed by a different parameter that QuickStatements does not support.
        assertPrints("@43.261930/10.927080", Datamodel.makeGlobeCoordinatesValue(43.26193, 10.92708,
                GlobeCoordinatesValue.PREC_DEGREE, GlobeCoordinatesValue.GLOBE_EARTH));
    }
    
    // Monolingual text values
    
    @Test
    public void printMonolingualTextValue() {
        assertPrints("pl:\"Krzyżacy\"", Datamodel.makeMonolingualTextValue("Krzyżacy", "pl"));
    }
    
    // Quantity values
    
    @Test
    public void printSimpleQuantityValue() {
        assertPrints("10.00", Datamodel.makeQuantityValue(new BigDecimal("10.00"),
                null, null, "1"));
    }
    
    @Test
    public void printQuantityValueWithUnit() {
        assertPrints("10.00U11573", Datamodel.makeQuantityValue(new BigDecimal("10.00"),
                null, null, "http://www.wikidata.org/entity/Q11573"));
    }
    
    @Test
    public void printQuantityValueWithBounds() {
        assertPrints("10.00[9.0,11.05]", Datamodel.makeQuantityValue(new BigDecimal("10.00"),
                new BigDecimal("9.0"), new BigDecimal("11.05"), "1"));
    }
    
    @Test
    public void printFullQuantity() {
        assertPrints("10.00[9.0,11.05]U11573", Datamodel.makeQuantityValue(new BigDecimal("10.00"),
                new BigDecimal("9.0"), new BigDecimal("11.05"), "http://www.wikidata.org/entity/Q11573"));
    }
    
    // String values
    
    @Test
    public void printString() {
        assertPrints("\"hello\"", Datamodel.makeStringValue("hello"));
    }
    
    // Time values
    
    @Test
    public void printYear() {
        assertPrints("+1586-00-00T00:00:00Z/9", Datamodel.makeTimeValue(1586L, (byte)0, (byte)0, (byte)0,
                (byte)0, (byte)0, (byte)9, 0, 0, 0, TimeValue.CM_GREGORIAN_PRO));
    }
    
    @Test
    public void printDay() {
        assertPrints("+1586-03-09T00:00:00Z/11", Datamodel.makeTimeValue(1586L, (byte)3, (byte)9, (byte)0,
                (byte)0, (byte)0, (byte)11, 0, 0, 0, TimeValue.CM_GREGORIAN_PRO));
    }
}
