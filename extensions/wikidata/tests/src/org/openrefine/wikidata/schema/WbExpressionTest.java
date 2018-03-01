package org.openrefine.wikidata.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import org.openrefine.wikidata.qa.QAWarningStore;
import org.openrefine.wikidata.schema.exceptions.SkipSchemaExpressionException;
import org.openrefine.wikidata.testing.TestingDataGenerator;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import com.google.refine.model.Cell;
import com.google.refine.model.ModelException;
import com.google.refine.model.Project;
import com.google.refine.model.Recon;
import com.google.refine.model.Row;
import com.google.refine.tests.RefineTest;

public class WbExpressionTest<T> extends RefineTest {
    
    protected Project project;
    protected Row row;
    protected ExpressionContext ctxt;
    protected QAWarningStore warningStore;
    
    @BeforeMethod
    public void createProject() throws IOException, ModelException {
        project = createCSVProject("Wikidata variable test project", "column A,column B,column C,column D\n"+
                "value A,value B,value C,value D");
        warningStore = new QAWarningStore();
        row = project.rows.get(0);
        ctxt = new ExpressionContext("http://www.wikidata.org/entity/", 0,
                row, project.columnModel, warningStore);
    }
    
    /**
     * Test that a particular expression evaluates to some object.
     * 
     * @param expected
     *          the expected evaluation of the value
     * @param expression
     *          the expression to evaluate
     */
    public void evaluatesTo(T expected, WbExpression<T> expression) {
        try {
            T result = expression.evaluate(ctxt);
            Assert.assertEquals(expected, result);
        } catch (SkipSchemaExpressionException e) {
            Assert.fail("Value was skipped by evaluator");
        }
    }
    
    /**
     * Test that a particular expression is skipped.
     * 
     * @param expected
     *          the expected evaluation of the value
     * @param expression
     *          the expression to evaluate
     */
    public void isSkipped(WbExpression<T> expression) {
        try {
            expression.evaluate(ctxt);
            Assert.fail("Value was not skipped by evaluator");
        } catch (SkipSchemaExpressionException e) {
            return;
        }
    }
    
    /**
     * Sets the context to a row with the given values.
     * 
     * @param rowValues
     *     the list of row values. They can be cells or cell values.
     */
    public void setRow(Object... rowValues) {
        Row row = new Row(rowValues.length);
        for(int i = 0; i != rowValues.length; i++) {
            Object val = rowValues[i];
            if(Cell.class.isInstance(val)) {
                row.cells.add((Cell)val);
            } else {
                Cell cell = new Cell((Serializable)val, (Recon)null);
                row.cells.add(cell);
            }
        }
        ctxt = new ExpressionContext("http://www.wikidata.org/entity/", 0,
                row, project.columnModel, warningStore);
    }
    
    /**
     * Creates a make-shift reconciled cell for a given Qid.
     * 
     * @param qid
     * @return
     *      a cell for use in setRow
     */
    public Cell recon(String qid) {
        return TestingDataGenerator.makeMatchedCell(qid, qid);
    }
}
