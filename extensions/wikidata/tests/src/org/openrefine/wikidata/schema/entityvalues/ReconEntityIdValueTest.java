package org.openrefine.wikidata.schema.entityvalues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.openrefine.wikidata.testing.TestingDataGenerator;
import org.testng.annotations.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;

import com.google.refine.model.Recon;

public class ReconEntityIdValueTest {
    
    private ReconEntityIdValue newItem = TestingDataGenerator.makeNewItemIdValue(1234L, "new item");
    private ReconEntityIdValue sameNewItem = TestingDataGenerator.makeNewItemIdValue(1234L, "different text");
    private ReconEntityIdValue differentNewItem = TestingDataGenerator.makeNewItemIdValue(7890L, "new item");
    private ReconEntityIdValue newProp = TestingDataGenerator.makeNewPropertyIdValue(1234L, "new prop");
    private ReconEntityIdValue existingProp = TestingDataGenerator.makeMatchedPropertyIdValue("P53", "new prop");
    private ReconEntityIdValue existingItem = TestingDataGenerator.makeMatchedItemIdValue("Q42", "existing item");
    
    @Test
    public void testIsNew() {
        assertTrue(newItem.isNew());
        assertFalse(existingItem.isNew());
    }
    
    @Test
    public void testGetLabel() {
        assertEquals("new item", newItem.getLabel());
        assertEquals("existing item", existingItem.getLabel());
    }
    
    @Test
    public void testGetTypes() { 
        String[] types = {"Q5"};
        Recon matchedRecon = TestingDataGenerator.makeMatchedRecon("Q453", "other item", types);
        ReconEntityIdValue existingIdWithTypes = new ReconItemIdValue(matchedRecon, "cell content");
        assertEquals(Collections.singletonList("Q5"), existingIdWithTypes.getTypes());
        assertEquals(Collections.emptyList(), existingItem.getTypes());
        assertEquals(Collections.emptyList(), newItem.getTypes());
    }
    
    @Test
    public void testGetId() {
        assertEquals("Q42", existingItem.getId());
        assertEquals("Q0", newItem.getId());
        assertEquals("P53", existingProp.getId());
        assertEquals("P0", newProp.getId());
    }
    
    @Test
    public void testGetIri() {
        assertEquals("http://www.wikidata.org/entity/Q42", existingItem.getIri());
        assertEquals("http://www.wikidata.org/entity/Q0", newItem.getIri());
    }
    
    @Test
    public void testGetSiteIri() {
        assertEquals("http://www.wikidata.org/entity/", existingItem.getSiteIri());
        assertEquals("http://www.wikidata.org/entity/", newItem.getSiteIri());
    }
    
    @Test
    public void testEquality() {
        // simple cases
        assertEquals(newItem, newItem);
        assertEquals(existingItem, existingItem);
        assertNotEquals(newItem, existingItem);
        assertNotEquals(existingItem, newItem);
        
        // a matched cell is equal to the canonical entity id of its item
        assertEquals(Datamodel.makeWikidataItemIdValue("Q42"), existingItem);
        // just checking this is symmetrical
        assertEquals(existingItem, Datamodel.makeWikidataItemIdValue("Q42"));
        
        // new cell equality relies on the judgmentHistoryEntry parameter
        assertEquals(newItem, sameNewItem);
        assertNotEquals(newItem, differentNewItem);
        // and on datatype
        assertNotEquals(newProp, newItem);
    }
    
    @Test
    public void testHashCode() {
        assertEquals(newItem.hashCode(), sameNewItem.hashCode());
        assertEquals(existingItem.hashCode(), Datamodel.makeWikidataItemIdValue("Q42").hashCode());
    }
    
    @Test
    public void testGetRecon() {
        assertEquals(newItem.getReconInternalId(), newItem.getRecon().judgmentHistoryEntry);
    }
    
    @Test
    public void testToString() {
        assertTrue(existingItem.toString().contains("Q42"));
        assertTrue(newItem.toString().contains("new"));
    }
}
