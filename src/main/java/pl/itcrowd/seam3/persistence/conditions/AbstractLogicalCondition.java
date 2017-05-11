package pl.itcrowd.seam3.persistence.conditions;

/**
 * Abstract class for AND and OR conditions.
 */
public abstract class AbstractLogicalCondition extends AbstractCondition {
// ------------------------------ FIELDS ------------------------------

    private String renderedEJBQL;

// --------------------------- CONSTRUCTORS ---------------------------

    public AbstractLogicalCondition(Object... args)
    {
        super(args);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public String getRenderedEJBQL()
    {
        return renderedEJBQL;
    }

    /**
     * Operator for this condition, i.e.: AND, OR.
     *
     * @return EJBQL condition concatenator
     */
    protected abstract String getOperator();

    @Override
    protected void renderEJBQL()
    {
        StringBuilder builder = new StringBuilder("(");
        boolean anyArgumentNotNull = false;
        for (Object o : argValues) {
            final String ejbqlPart = toEJBQLPart(o);
            if (ejbqlPart == null || "".equals(ejbqlPart.trim())) {
                continue;
            }
            if (anyArgumentNotNull) {
                builder.append(getOperator());
            }
            builder.append(ejbqlPart);
            anyArgumentNotNull = true;
        }
        builder.append(")");
        renderedEJBQL = anyArgumentNotNull ? builder.toString() : "";
    }
}
