package pl.itcrowd.seam3.persistence;

import pl.itcrowd.seam3.persistence.conditions.AbstractCondition;

import javax.persistence.Query;
import java.util.List;

public class DynParamEntityQuery<E> extends EntityQuery<E> {
// ------------------------------ FIELDS ------------------------------

    private List<? extends AbstractCondition> conditions;

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<? extends AbstractCondition> getConditions()
    {
        return conditions;
    }

    public void setConditions(List<? extends AbstractCondition> conditions)
    {
        this.conditions = conditions;
    }

    @Override
    protected void appendRestrictionsEjbql(StringBuilder builder)
    {
        super.appendRestrictionsEjbql(builder);
        if (conditions != null) {
            for (AbstractCondition condition : conditions) {
                String ejbqlPart = condition.getRenderedEJBQL();
                if (ejbqlPart != null && !"".equals(ejbqlPart.trim())) {
                    if (WHERE_PATTERN.matcher(builder).find()) {
                        builder.append(" ").append(getRestrictionLogicOperator()).append(" ");
                    } else {
                        builder.append(" where ");
                    }
                    builder.append(ejbqlPart);
                }
            }
        }
    }

    @Override
    protected Query createCountQuery()
    {
        Query countQuery = super.createCountQuery();
        setParameters(countQuery);
        return countQuery;
    }

    @Override
    protected Query createQuery()
    {
        Query query = super.createQuery();
        setParameters(query);
        return query;
    }

    @Override
    protected void evaluateAllParameters()
    {
        super.evaluateAllParameters();
        int paramOffset = getRestrictionParameterValues().size() + getQueryParameterValues().size() + 1;
        if (conditions != null) {
            for (AbstractCondition condition : conditions) {
                condition.setParamIndexOffset(paramOffset);
                condition.evaluate();
                paramOffset += condition.getDynamicParametersCount();
            }
        }
    }

    protected boolean isAnyConditionParameterDirty()
    {
        if (conditions != null) {
            for (AbstractCondition condition : conditions) {
                condition.evaluate();
                if (condition.isDirty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isAnyParameterDirty()
    {
        return super.isAnyParameterDirty() || isAnyConditionParameterDirty();
    }

    protected void setParameters(Query query)
    {
        if (conditions != null) {
            for (AbstractCondition condition : conditions) {
                condition.markParametersSet();
                if (condition.rendersEJBQL()) {
                    for (AbstractCondition.LocalDynamicParameter parameter : condition.getParamsToSet()) {
                        query.setParameter(parameter.getName(), parameter.getValue());
                    }
                }
            }
        }
    }
}
