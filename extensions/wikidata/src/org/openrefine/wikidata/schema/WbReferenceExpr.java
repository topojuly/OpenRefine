package org.openrefine.wikidata.schema;

import java.util.ArrayList;
import java.util.List;

import org.openrefine.wikidata.schema.exceptions.SkipSchemaExpressionException;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An expression for a reference (list of reference snaks).
 * 
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use=JsonTypeInfo.Id.NONE)
public class WbReferenceExpr implements WbExpression<Reference> {
    private List<WbSnakExpr> snakExprs;
    
    @JsonCreator
    public WbReferenceExpr(
            @JsonProperty("snaks") List<WbSnakExpr> snakExprs) {
        this.snakExprs = snakExprs;
    }
    
    @Override
    public Reference evaluate(ExpressionContext ctxt) throws SkipSchemaExpressionException {
        List<SnakGroup> snakGroups = new ArrayList<SnakGroup>();
        for (WbSnakExpr expr : getSnaks()) {
            List<Snak> snakList = new ArrayList<Snak>(1);
            try {
                snakList.add(expr.evaluate(ctxt));
                snakGroups.add(Datamodel.makeSnakGroup(snakList));
            } catch (SkipSchemaExpressionException e) {
                continue;
            }
        }
        if (! snakGroups.isEmpty()) {
            return Datamodel.makeReference(snakGroups);
        } else {
            throw new SkipSchemaExpressionException();
        }
    }

    @JsonProperty("snaks")
    public List<WbSnakExpr> getSnaks() {
        return snakExprs;
    }

    @Override
    public boolean equals(Object other) {
        if(other == null || !WbReferenceExpr.class.isInstance(other)) {
            return false;
        }
        WbReferenceExpr otherExpr = (WbReferenceExpr)other;
        return snakExprs.equals(otherExpr.getSnaks());
    }
    
    @Override
    public int hashCode() {
        return snakExprs.hashCode();
    }
}
