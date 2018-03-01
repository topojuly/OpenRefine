package org.openrefine.wikidata.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.refine.browsing.Engine;
import com.google.refine.browsing.FilteredRows;
import com.google.refine.browsing.RowVisitor;
import com.google.refine.model.OverlayModel;
import com.google.refine.model.Project;
import com.google.refine.model.Row;
import org.openrefine.wikidata.schema.WbItemDocumentExpr;
import org.openrefine.wikidata.schema.exceptions.SkipSchemaExpressionException;
import org.openrefine.wikidata.schema.ExpressionContext;
import org.openrefine.wikidata.utils.JacksonJsonizable;

public class WikibaseSchema implements OverlayModel {

    final static Logger logger = LoggerFactory.getLogger("RdfSchema");
	
    protected List<WbItemDocumentExpr> itemDocumentExprs = new ArrayList<WbItemDocumentExpr>();
    
    protected String baseUri = "http://www.wikidata.org/entity/";

    @Override
    public void onBeforeSave(Project project) {
    }
    
    @Override
    public void onAfterSave(Project project) {
    }
    
   @Override
    public void dispose(Project project) {

    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public WikibaseSchema() {
    	
    }
    
    
    public String getBaseUri() {
        return baseUri;
    }

    public List<WbItemDocumentExpr> getItemDocumentExpressions() {
        return itemDocumentExprs;
    }
    
    public void setItemDocumentExpressions(List<WbItemDocumentExpr> exprs) {
        this.itemDocumentExprs = exprs;
    }
    
    /**
     * Evaluates all item documents in a particular expression context.
     * @param ctxt
     * @return
     */
    public List<ItemUpdate> evaluateItemDocuments(ExpressionContext ctxt) {
        List<ItemUpdate> result = new ArrayList<ItemUpdate>();
        for (WbItemDocumentExpr expr : itemDocumentExprs) {
            
            try {
                result.add(expr.evaluate(ctxt));
            } catch (SkipSchemaExpressionException e) {
                continue;
            }
        }
        return result;
    }
    
    public List<ItemUpdate> evaluate(Project project, Engine engine) {
        List<ItemUpdate> result = new ArrayList<ItemUpdate>();
        FilteredRows filteredRows = engine.getAllFilteredRows();
        filteredRows.accept(project, new EvaluatingRowVisitor(result));
        return result;
    }
    
    protected class EvaluatingRowVisitor implements RowVisitor {
        private List<ItemUpdate> result;
        public EvaluatingRowVisitor(List<ItemUpdate> result) {
            this.result = result;
        }
        
        @Override
        public void start(Project project) {
            ; 
        }

        @Override
        public boolean visit(Project project, int rowIndex, Row row) {
            ExpressionContext ctxt = new ExpressionContext(baseUri, rowIndex, row, project.columnModel);
            result.addAll(evaluateItemDocuments(ctxt));
            return false;
        }

        @Override
        public void end(Project project) {
            ;
        }
    }

    static public WikibaseSchema reconstruct(JSONObject o) throws JSONException {
        
        JSONArray changeArr = o.getJSONArray("itemDocuments");
        WikibaseSchema schema = new WikibaseSchema();
        for (int i = 0; i != changeArr.length(); i++) {
            WbItemDocumentExpr changeExpr = JacksonJsonizable.fromJSONClass(changeArr.getJSONObject(i), WbItemDocumentExpr.class);
            schema.itemDocumentExprs.add(changeExpr);
        }
        return schema;
    }

    @Override
    public void write(JSONWriter writer, Properties options)
            throws JSONException {
        writer.object();
        writer.key("itemDocuments");
        writer.array();
        for (WbItemDocumentExpr changeExpr : itemDocumentExprs) {
            changeExpr.write(writer, options);
        }
        writer.endArray();
        writer.endObject();
    }
    
    static public WikibaseSchema load(Project project, JSONObject obj) throws Exception {
        return reconstruct(obj);
    }
}