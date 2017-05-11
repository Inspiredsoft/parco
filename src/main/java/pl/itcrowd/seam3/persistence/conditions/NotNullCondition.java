package pl.itcrowd.seam3.persistence.conditions;

/**
 * Condition that is always rendered and is good for checking if value is not null.
 */
public class NotNullCondition extends AbstractNullabilityCondition {
// --------------------------- CONSTRUCTORS ---------------------------

    public NotNullCondition(final Object arg)
    {
        super(new DynamicParameter<Object>() {
            @Override
            public Object getValue()
            {
                return arg;
            }
        });
    }

    @Override
    protected String getInstructionEJBQL()
    {
        return "IS NOT NULL";
    }
}
