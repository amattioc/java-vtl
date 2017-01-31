package no.ssb.vtl.script.visitors;

import com.google.common.collect.Lists;
import no.ssb.vtl.model.Dataset;
import no.ssb.vtl.parser.VTLBaseVisitor;
import no.ssb.vtl.parser.VTLParser;
import no.ssb.vtl.script.operations.UnionOperation;
import no.ssb.vtl.script.visitors.join.JoinExpressionVisitor;

import javax.script.ScriptContext;
import java.util.List;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.*;

/**
 * A visitor that handles the relational operators.
 */
public class RelationalVisitor extends VTLBaseVisitor<Supplier<Dataset>> {

    private final AssignmentVisitor assignmentVisitor;
    private final JoinExpressionVisitor joinVisitor;

    public RelationalVisitor(AssignmentVisitor assignmentVisitor, ScriptContext context) {
        this.assignmentVisitor = checkNotNull(assignmentVisitor);
        this.joinVisitor = new JoinExpressionVisitor(context);
    }


    @Override
    public Supplier<Dataset> visitUnionExpression(VTLParser.UnionExpressionContext ctx) {
        List<Dataset> datasets = Lists.newArrayList();
        for (VTLParser.DatasetExpressionContext datasetExpressionContext : ctx.datasetExpression()) {
            Dataset dataset = assignmentVisitor.visit(datasetExpressionContext);
            datasets.add(dataset);
        }
        return new UnionOperation(datasets);
    }

    @Override
    public Supplier<Dataset> visitJoinExpression(VTLParser.JoinExpressionContext ctx) {
        Dataset visit = joinVisitor.visit(ctx);
        return () -> visit;
    }

}
