package pl.itcrowd.seam3.persistence.conditions;

/**
 * Condition that is rendered only if all arguments are not null.
 */
public class FreeCondition extends AbstractCondition {
// ------------------------------ FIELDS ------------------------------

    private String renderedEJBQL;

// --------------------------- CONSTRUCTORS ---------------------------

    public FreeCondition(Object... args)
    {
        super(args);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public String getRenderedEJBQL()
    {
        return renderedEJBQL;
    }

    @Override
    protected void renderEJBQL()
    {
        StringBuilder builder = new StringBuilder();
        for (Object o : argValues) {
            final String ejbqlPart = toEJBQLPart(o);
            if (ejbqlPart == null) {
                renderedEJBQL = "";
                return;
            }
            builder.append(ejbqlPart);
        }
        renderedEJBQL = builder.toString();
    }
}
