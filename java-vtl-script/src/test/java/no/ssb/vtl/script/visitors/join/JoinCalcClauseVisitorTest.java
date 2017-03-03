package no.ssb.vtl.script.visitors.join;

import com.google.common.collect.Maps;
import no.ssb.vtl.model.Component;
import no.ssb.vtl.model.DataPoint;
import no.ssb.vtl.model.DataStructure;
import no.ssb.vtl.model.VTLExpression;
import no.ssb.vtl.model.VTLObject;
import no.ssb.vtl.parser.VTLLexer;
import no.ssb.vtl.parser.VTLParser;
import no.ssb.vtl.script.visitors.ReferenceVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("PointlessArithmeticExpression")
public class JoinCalcClauseVisitorTest {

    @Test
    public void testNumericalLiteralsSum() throws Exception {

        String test = "1 + 2 + 3 + 4 + 5 - 6 - 7 - 8 - 9";
        VTLLexer lexer = new VTLLexer(new ANTLRInputStream(test));
        VTLParser parser = new VTLParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());

        JoinCalcClauseVisitor visitor = new JoinCalcClauseVisitor();

        Function<DataPoint, VTLObject> result = visitor.visit(parser.joinCalcExpression());

        assertThat(result.apply(null).get()).isEqualTo(1 + 2 + 3 + 4 + 5 - 6 - 7 - 8 - 9);

    }

    @Test
    public void testNumericalLiteralsSumWithParenthesis() throws Exception {

        String test = "1 + 2 + ( 3 + 4 + 5 - 6 - 7 ) - 8 - 9";
        VTLLexer lexer = new VTLLexer(new ANTLRInputStream(test));
        VTLParser parser = new VTLParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());

        JoinCalcClauseVisitor visitor = new JoinCalcClauseVisitor();

        Function<DataPoint, VTLObject> result = visitor.visit(parser.joinCalcExpression());

        assertThat(result.apply(null).get()).isEqualTo(1 + 2 + (3 + 4 + 5 - 6 - 7) - 8 - 9);

    }

    @Test
    public void testNumericalLiteralsProduct() throws Exception {

        String test = "1 * 2 * 3 * 4 * 5 / 6 / 7 / 8 / 9";
        VTLLexer lexer = new VTLLexer(new ANTLRInputStream(test));
        VTLParser parser = new VTLParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());

        JoinCalcClauseVisitor visitor = new JoinCalcClauseVisitor();

        Function<DataPoint, VTLObject> result = visitor.visit(parser.joinCalcExpression());

        //noinspection PointlessArithmeticExpression
        assertThat(result.apply(null).get()).isEqualTo(1 * 2 * 3 * 4 * 5 / 6 / 7 / 8 / 9);

    }

    @Test
    public void testNumericalVariableReference() {
        String test = "1 * 2 + a * (b - c) / d - 10";

        VTLLexer lexer = new VTLLexer(new ANTLRInputStream(test));
        VTLParser parser = new VTLParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());

        DataStructure ds = DataStructure.of((o, aClass) -> o,
                "a", Component.Role.MEASURE, Integer.class,
                "b", Component.Role.MEASURE, Integer.class,
                "c", Component.Role.MEASURE, Integer.class,
                "d", Component.Role.MEASURE, Integer.class
        );

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("a", 20);
        variables.put("b", 15);
        variables.put("c", 10);
        variables.put("d", 5);

        DataPoint dataPoint = ds.wrap(variables);

        Map<String, Component> scope = Maps.newHashMap();
        scope.putAll(ds);

        JoinCalcClauseVisitor visitor = new JoinCalcClauseVisitor(new ReferenceVisitor(scope), ds);

        Function<DataPoint, VTLObject> result = visitor.visit(parser.joinCalcExpression());

        // TODO: Set variables.
        assertThat(result.apply(dataPoint).get()).isEqualTo(1 * 2 + 20 * (15 - 10) / 5 - 10);

    }

    @Test
    public void testNumericalLiteralsProductWithParenthesis() throws Exception {

        String test = "1 * 2 * ( 3 * 4 * 5 / 6 / 7 ) / 8 / 9";
        VTLLexer lexer = new VTLLexer(new ANTLRInputStream(test));
        VTLParser parser = new VTLParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());

        // Setup fake map.

        JoinCalcClauseVisitor visitor = new JoinCalcClauseVisitor();

        Function<DataPoint, VTLObject> result = visitor.visit(parser.joinCalcExpression());

        assertThat(result.apply(null).get()).isEqualTo(1 * 2 * (3 * 4 * 5 / 6 / 7) / 8 / 9);

    }

    @Test
    public void testNumericalReferenceNotFound() throws Exception {

        String test = "notFoundVariable + 1";
        VTLLexer lexer = new VTLLexer(new ANTLRInputStream(test));
        VTLParser parser = new VTLParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());
        JoinCalcClauseVisitor visitor = new JoinCalcClauseVisitor(new ReferenceVisitor(Collections.emptyMap()), null);

        assertThatThrownBy(() -> {
            // TODO: This should happen during execution (when data is computed).
            VTLExpression result = visitor.visit(parser.joinCalcExpression());
            result.apply(DataPoint.create(Collections.emptyList()));
        }).hasMessageContaining("variable")
                .hasMessageContaining("notFoundVariable");

    }

    private VTLObject createNumericalDataPoint(Integer value) {
        DataStructure structure = DataStructure.of(
                (o, aClass) -> o,
                "value",
                Component.Role.MEASURE,
                Integer.class
        );
        return structure.wrap("value", value);
    }
}