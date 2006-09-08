package net.sf.jxls.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import net.sf.jxls.tag.Block;
import net.sf.jxls.controller.SheetTransformationController;
import net.sf.jxls.processor.RowProcessor;
import net.sf.jxls.transformation.ResultTransformation;

import java.util.List;
import java.util.Map;

/**
 * Controls a list of transformers
 * @author Leonid Vysochyn
 */
public class ChainTransformer{
    protected final Log log = LogFactory.getLog(getClass());

    List transformers;
    Sheet sheet;
    List rowProcessors;
    Row parentRow;

    public ChainTransformer(List transformers, Sheet sheet, List rowProcessors, Row parentRow) {
        this.transformers = transformers;
        this.sheet = sheet;
        this.rowProcessors = rowProcessors;
        this.parentRow = parentRow;
    }

    public ChainTransformer(List transformers, List rowProcessors, Row parentRow) {
        this.transformers = transformers;
        this.rowProcessors = rowProcessors;
        this.parentRow = parentRow;
    }

    public ChainTransformer(List transformers, List rowProcessors) {
        this.transformers = transformers;
        this.rowProcessors = rowProcessors;
    }

    public ChainTransformer(List transformers) {
        this.transformers = transformers;
    }



    ResultTransformation transform(SheetTransformationController stc, SheetTransformer sheetTransformer, Map beans){
        ResultTransformation resultTransformation = new ResultTransformation();
        for (int i = 0; i < transformers.size(); i++) {
            RowTransformer rowTransformer = (RowTransformer) transformers.get(i);
            Block transformationBlock = rowTransformer.getTransformationBlock();
            transformationBlock = resultTransformation.transformBlock( transformationBlock );
            rowTransformer.setTransformationBlock( transformationBlock );
            log.debug(rowTransformer.getClass().getName() + ", " + rowTransformer.getTransformationBlock());
            //rowTransformer
            Row row = rowTransformer.getRow();
            row.setParentRow( parentRow );
            applyRowProcessors(sheet, row );
//            Util.writeToFile("beforeTransformBlock.xls", sheet.getHssfWorkbook());
            resultTransformation.add( rowTransformer.transform(stc, sheetTransformer, beans ) );
//            Util.writeToFile("afterTransformBlock.xls", sheet.getHssfWorkbook());

        }
        return resultTransformation;
    }

    /**
     * Applies all registered RowProcessors to a row
     *
     * @param row - {@link net.sf.jxls.transformer.Row} object with row information
     */
    private void applyRowProcessors(Sheet sheet, Row row) {
        for (int i = 0; i < rowProcessors.size(); i++) {
            RowProcessor rowProcessor = (RowProcessor) rowProcessors.get(i);
            rowProcessor.processRow(row, sheet.getNamedCells());
        }
    }


}
