package pl.itcrowd.seam3.persistence.conditions;

/**
 * Abstract condition that renders EJBQL even if attribute is null.
 */
public abstract class AbstractNullabilityCondition extends AbstractCondition {
// ------------------------------ FIELDS ------------------------------

    private String renderedEJBQL;

// --------------------------- CONSTRUCTORS ---------------------------

    public AbstractNullabilityCondition(Object... args)
    {
        super(args);
        includeNullParameters = true;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public String getRenderedEJBQL()
    {
        return renderedEJBQL;
    }

    /**
     * Gets EJBQL instruction for this condition.
     *
     * @return EJBQL instruction for this condition
     */
    protected abstract String getInstructionEJBQL();

    @Override
    protected void renderEJBQL()
    {
        StringBuilder builder = new StringBuilder();
        final String ejbqlPart = toEJBQLPart(argValues[0]);
        builder.append(ejbqlPart);
        builder.append(" ");
        builder.append(getInstructionEJBQL());
        oldEJBQL = renderedEJBQL;
        renderedEJBQL = builder.toString();
    }
}
