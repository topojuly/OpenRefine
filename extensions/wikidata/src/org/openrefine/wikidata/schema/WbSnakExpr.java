package org.openrefine.wikidata.schema;

import org.openrefine.wikidata.schema.exceptions.SkipSchemaExpressionException;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An expression for a snak (pair of property and value).
 * 
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use=JsonTypeInfo.Id.NONE)
public class WbSnakExpr implements WbExpression<Snak> {
    
    private WbExpression<? extends PropertyIdValue> prop;
    private WbExpression<? extends Value> value;
    
    @JsonCreator
    public WbSnakExpr(
            @JsonProperty("prop") WbExpression<? extends PropertyIdValue> propExpr,
            @JsonProperty("value") WbExpression<? extends Value> valueExpr) {
        this.prop = propExpr;
        this.value = valueExpr;
    }

    @Override
    public Snak evaluate(ExpressionContext ctxt) throws SkipSchemaExpressionException {
        PropertyIdValue propertyId = getProp().evaluate(ctxt);
        Value evaluatedValue = value.evaluate(ctxt);
        return Datamodel.makeValueSnak(propertyId, evaluatedValue);
    }

    @JsonProperty("prop")
    public WbExpression<? extends PropertyIdValue> getProp() {
        return prop;
    }

    @JsonProperty("value")
    public WbExpression<? extends Value> getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null || !WbSnakExpr.class.isInstance(other)) {
            return false;
        }
        WbSnakExpr otherExpr = (WbSnakExpr) other;
        return prop.equals(otherExpr.getProp()) && value.equals(otherExpr.getValue());
    }
    
    @Override
    public int hashCode() {
        return prop.hashCode() + value.hashCode();
    }
}
