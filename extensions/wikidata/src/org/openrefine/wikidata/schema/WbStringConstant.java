package org.openrefine.wikidata.schema;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class WbStringConstant implements WbValueExpr<StringValue> {
    
    private String value;
    
    @JsonCreator
    public WbStringConstant(@JsonProperty("value") String value) {
        this.value = value;
    }
    
    @Override
    public StringValue evaluate(ExpressionContext ctxt) {
        return Datamodel.makeStringValue(value);
    }
    
    @JsonProperty("value")
    public String getValue() {
        return value;
    }
}
