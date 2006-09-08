package net.sf.jxls.transformer;

import net.sf.jxls.tag.Tag;
import net.sf.jxls.tag.Block;
import net.sf.jxls.transformer.BaseRowTransformer;
import net.sf.jxls.transformer.SheetTransformer;
import net.sf.jxls.parser.Cell;
import net.sf.jxls.controller.SheetTransformationController;
import net.sf.jxls.transformation.ResultTransformation;

import java.util.Map;

/**
 * Implementation of {@link RowTransformer} for transforming jx tags
 * @author Leonid Vysochyn
 */
public class TagRowTransformer extends BaseRowTransformer {

    Cell cell;
    Tag tag;

    private ResultTransformation resultTransformation;

    public TagRowTransformer(Row row, Cell cell) {
        this.cell = cell;
        this.row = row;
        this.tag = cell.getTag();
    }

    public ResultTransformation getTransformationResult() {
        return resultTransformation;
    }

    public ResultTransformation transform(SheetTransformationController stc, SheetTransformer sheetTransformer, Map beans) {
        log.info("transforming tag: " + tag);
        Block body = tag.getTagContext().getTagBody();
        tag.getTagContext().setSheetTransformationController( stc );
//        int shiftNumber = body.getNumberOfRows() - 1;
        resultTransformation = tag.process( sheetTransformer );
//        int SHIFT = resultTransformation.getNextRowShift();

        return resultTransformation;
    }

    public Block getTransformationBlock() {
        return tag.getTagContext().getTagBody();
    }

    public void setTransformationBlock(Block block) {
        tag.getTagContext().setTagBody( block );
    }

}
