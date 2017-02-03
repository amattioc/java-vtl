package no.ssb.vtl.script.visitors.join;

import no.ssb.vtl.model.Component;
import no.ssb.vtl.model.Dataset;
import no.ssb.vtl.parser.VTLBaseVisitor;
import no.ssb.vtl.parser.VTLParser;
import no.ssb.vtl.script.operations.DropOperator;
import no.ssb.vtl.script.visitors.ReferenceVisitor;

import javax.script.Bindings;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

/**
 * Visit the drop clauses.
 */
public class JoinDropClauseVisitor extends VTLBaseVisitor<DropOperator> {

    private final Dataset dataset;
    private final ReferenceVisitor referenceVisitor;

    public JoinDropClauseVisitor(Dataset dataset, Bindings scope) {
        this.dataset = checkNotNull(dataset, "dataset was null");
        this.referenceVisitor = new ReferenceVisitor(scope);
    }

    @Override
    public DropOperator visitJoinDropExpression(VTLParser.JoinDropExpressionContext ctx) {
        Set<Component> components = ctx.componentRef().stream()
                .map(referenceVisitor::visit)
                .map(o -> (Component) o) //TODO: Safe?
                .collect(Collectors.toSet());
        return new DropOperator(dataset, components);
    }
}
