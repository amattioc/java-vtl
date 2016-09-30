package kohl.hadrien;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Created by hadrien on 07/09/16.
 */
public class DataStructure {

    final ImmutableList<Component> variables;

    public DataStructure(Iterable<Identifier> identifiers, Iterable<Component> measures) {
        variables = ImmutableList.copyOf(
                Iterables.concat(
                        checkNotNull(identifiers, "identifier list was null"),
                        checkNotNull(measures, "measure list was null")
                )
        );
    }
}
