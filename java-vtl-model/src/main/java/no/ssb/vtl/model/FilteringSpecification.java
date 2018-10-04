package no.ssb.vtl.model;

import java.util.Collection;

public interface FilteringSpecification {

    Collection<Clause> getClauses();

    enum Operator {
        EQ, GT, GE, IN
    }

    interface Clause {
        Collection<Literal> getLiterals();
    }

    interface Literal {
        String getColumn();

        Operator getOperator();

        Collection<VTLObject> getValues();

        Boolean isNegated();
    }

}