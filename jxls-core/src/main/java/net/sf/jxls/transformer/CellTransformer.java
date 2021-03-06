package net.sf.jxls.transformer;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import net.sf.jxls.formula.Formula;
import net.sf.jxls.parser.Cell;
import net.sf.jxls.parser.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Cell transformation class
 * @author Leonid Vysochyn
 */
public class CellTransformer {
    protected final Log log = LogFactory.getLog(getClass());


    private Configuration configuration;

    public CellTransformer(Configuration configuration) {
        if( configuration!=null ){
            this.configuration = configuration;
        }else{
            this.configuration = new Configuration();
        }
    }

    void transform( Cell cell ){
        try {
            if (cell.getPoiCell() != null && cell.getPoiCell().getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING) {
                if (cell.getCollectionProperty() == null) {
                    if (cell.getFormula() == null) {
                            if( cell.getExpressions().size() == 0 ){
                                if( cell.getMetaInfo() !=null ){
                                    cell.getPoiCell().setCellValue(cell.getPoiCell().getSheet().getWorkbook().getCreationHelper().createRichTextString(cell.getStringCellValue()));
                                }
                            }else if (cell.getExpressions().size() == 1) {
                                Object value = ((Expression) cell.getExpressions().get(0)).evaluate();
                                if (value == null) {
                                    cell.getPoiCell().setCellValue(cell.getPoiCell().getSheet().getWorkbook().getCreationHelper().createRichTextString(""));
                                    cell.getPoiCell().setCellType( org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK );
                                } else if (value instanceof Double) {
                                    cell.getPoiCell().setCellValue(((Double) value).doubleValue());
                                } else if (value instanceof BigDecimal) {
                                    cell.getPoiCell().setCellValue(((BigDecimal) value).doubleValue());
                                } else if (value instanceof Date) {
                                    cell.getPoiCell().setCellValue((Date) value);
                                }else if (value instanceof Calendar) {
                                    cell.getPoiCell().setCellValue((Calendar) value);
                                } else if (value instanceof Integer) {
                                    cell.getPoiCell().setCellValue(((Integer) value).intValue());
                                }else if (value instanceof Long) {
                                    cell.getPoiCell().setCellValue(((Long) value).longValue());
                                } else {
                                    // fixing possible CR/LF problem
                                    String fixedValue = value.toString();
                                    if (fixedValue != null) {
                                        fixedValue = fixedValue.replaceAll("\r\n", "\n");
                                    }
                                    if( fixedValue.length() == 0 ){
                                        cell.getPoiCell().setCellType( org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK );
                                    }else{
                                        cell.getPoiCell().setCellValue(cell.getPoiCell().getSheet().getWorkbook().getCreationHelper().createRichTextString(fixedValue));
                                    }
                                }
                            } else {
                                if (cell.getExpressions().size() > 1) {
                                    String value = "";
                                    for (int i = 0; i < cell.getExpressions().size(); i++) {
                                        Expression expr = (Expression) cell.getExpressions().get(i);
                                        Object propValue = expr.evaluate();
                                        if (propValue != null) {
                                            value += propValue.toString();
                                        }
                                    }
                                    setCellValue(cell, value);
                                }
                            }
                    }
                    else {
                        processFormulaCell( cell );
                    }
                } else {
                    String value = "";
                    for (int i = 0; i < cell.getExpressions().size(); i++) {
                        Expression expr = (Expression) cell.getExpressions().get(i);
                        if (expr.getCollectionProperty() == null) {
                            value += expr.evaluate();
                        } else {
                            value += configuration.getStartExpressionToken() + expr.getExpression() + configuration.getEndExpressionToken();
                        }
                    }
                    setCellValue(cell, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("Can't parse expression");
        }
    }

    private void setCellValue(Cell cell, String value) {
        if (value == null || value.length() == 0) {
            cell.getPoiCell().setCellType( org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK );
        } else {
            cell.getPoiCell().setCellValue(cell.getPoiCell().getSheet().getWorkbook().getCreationHelper().createRichTextString(value));
        }
    }

    private static void processFormulaCell(Cell cell) {
        // processing formula
        Formula formula = cell.getFormula();
        if (formula.isInline()) {
            if (cell.getCollectionName() != null) {
        // simple copy of inline formula template
        // it will be processed when individual rows are processed
                cell.getPoiCell().setCellValue(cell.getPoiCell().getSheet().getWorkbook().getCreationHelper().createRichTextString(cell.getStringCellValue()));
            } else {
        // processing of inline formulaString template
                String formulaString = formula.getInlineFormula(cell.getRow().getPoiRow().getRowNum() + 1);
                cell.getPoiCell().setCellFormula(formulaString);
            }
        }
    }
}
