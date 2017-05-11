package pl.itcrowd.seam3.persistence.conditions;

/**
 * Condition that renders non null arguments concatenated with AND condition
 */
public class AndCondition extends AbstractLogicalCondition {
// --------------------------- CONSTRUCTORS ---------------------------

    public AndCondition(Object... args)
    {
        super(args);
    }

    @Override
    protected String getOperator()
    {
        return " AND ";
    }
}
