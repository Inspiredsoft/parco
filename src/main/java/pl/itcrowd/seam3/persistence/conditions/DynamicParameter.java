package pl.itcrowd.seam3.persistence.conditions;

import java.io.Serializable;

/**
 * Dynamic condition parameter that should be evaluated on AbstractCondition.evaluate().
 */
public interface DynamicParameter<E> extends Serializable {
// -------------------------- OTHER METHODS --------------------------

    abstract E getValue();
}
