package pl.itcrowd.seam3.persistence.conditions;

/**
 * Condition that is always rendered and is good for checking if value is null.
 */
public class IsNullCondition extends AbstractNullabilityCondition {
// --------------------------- CONSTRUCTORS ---------------------------

    public IsNullCondition(Object arg)
    {
        super(arg);
    }

    @Override
    protected String getInstructionEJBQL()
    {
        return "IS NULL";
    }
}
