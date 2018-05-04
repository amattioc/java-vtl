package no.ssb.vtl.script.operations.join;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import no.ssb.vtl.model.Component;
import no.ssb.vtl.model.DataPoint;
import no.ssb.vtl.model.DataStructure;
import no.ssb.vtl.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A key extractor used in join operations.
 */
public class JoinKeyExtractor implements UnaryOperator<DataPoint> {

    private final DataPoint buffer;
    private final int[] indices;

    /**
     * Create a new JoinKeyExtractor.
     *
     * @param childStructure original structure.
     * @param order the order representing the component to extract.
     *
     */
    public JoinKeyExtractor(
            DataStructure childStructure,
            Order order
    ) {
        this(childStructure, order, Function.identity());
    }

    /**
     * Create a new JoinKeyExtractor.
     *
     * @param childStructure original structure.
     * @param order the order representing the component to extract.
     * @param mapping the mapping used to translate order component to child components.
     *
     */
    public JoinKeyExtractor(
            DataStructure childStructure,
            Order order,
            Map<Component, Component> mapping
    ) {
        this(childStructure, order, mapping::get);
    }


    private JoinKeyExtractor(
            DataStructure childStructure,
            Order order,
            Function<Component, Component> mapper
    ) {

        ImmutableList<Component> fromList = ImmutableList.copyOf(childStructure.values());
        ImmutableList<Component> toList = ImmutableList.copyOf(order.keySet());

        // TODO
        //List<Component> mapped = fromList.stream().map(mapper).collect(Collectors.toList());
        //checkArgument(
        //        mapped.containsAll(toList),
        //        "could not create key extractor. Missing %s",
        //        Collections2.filter(toList, Predicates.not(Predicates.in(mapped)))
        //);

        ArrayList<Integer> indices = Lists.newArrayList();

        // For each component in order, find the child index.
        for (Component orderComponent : order.keySet()) {
            indices.add(
                    toList.indexOf(orderComponent),
                    fromList.indexOf(mapper.apply(orderComponent))
            );
        }

        this.indices = Ints.toArray(indices);
        this.buffer = DataPoint.create(order.size());
    }

    @Override
    public DataPoint apply(DataPoint dataPoint) {
        for (int i = 0; i < indices.length; i++) {
            buffer.set(i, dataPoint.get(indices[i]));
        }
        return (DataPoint) buffer.clone();
    }
}
