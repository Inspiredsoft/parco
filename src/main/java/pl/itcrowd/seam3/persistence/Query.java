package pl.itcrowd.seam3.persistence;

import org.jboss.seam.transaction.Transactional;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.enterprise.inject.Instance;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for components which manage a query
 * result set. This class may be reused by either
 * configuration or extension, and may be bound
 * directly to a view, or accessed by some
 * intermediate Seam component.
 *
 * @author Gavin King
 */
@SuppressWarnings({"ManagedBeanInconsistencyInspection"})
public abstract class Query<T, E> {
// ------------------------------ FIELDS ------------------------------

    protected static final String DIR_ASC = "asc";

    protected static final String DIR_DESC = "desc";

    private static final Pattern FROM_PATTERN = Pattern.compile("(^|\\s)(from)\\s", Pattern.CASE_INSENSITIVE);

    private static final Pattern GROUP_PATTERN = Pattern.compile("\\s(group)(\\s)+by\\s", Pattern.CASE_INSENSITIVE);

    private static final String LOGIC_OPERATOR_AND = "and";

    private static final String LOGIC_OPERATOR_OR = "or";

    private static final Pattern ORDER_COLUMN_PATTERN = Pattern.compile("^\\w+(\\.\\w+)*$");

    private static final Pattern ORDER_PATTERN = Pattern.compile("\\s(order)(\\s)+by\\s", Pattern.CASE_INSENSITIVE);

    private static final Pattern SUBJECT_PATTERN = Pattern.compile("^select\\s+(\\w+(?:\\s*\\.\\s*\\w+)*?)(?:\\s*,\\s*(\\w+(?:\\s*\\.\\s*\\w+)*?))*?\\s+from",
        Pattern.CASE_INSENSITIVE);

    protected static final Pattern WHERE_PATTERN = Pattern.compile("\\s(where)\\s", Pattern.CASE_INSENSITIVE);

    protected DataModel<E> dataModel;

    private String ejbql;

    @Inject
    private Instance<EntityQuery.MyExpressions> expressions;

    @Inject
    private Instance<FacesContext> facesContext;

    private Integer firstResult;

    private String groupBy;

    private Integer maxResults;

    private String order;

    private String orderColumn;

    private String orderDirection;

    private String parsedEjbql;

    private List<String> parsedRestrictions;

    private List<Object> queryParameterValues;

    private List<ValueExpression> queryParameters;

    private String restrictionLogicOperator;

    private List<Object> restrictionParameterValues;

    private List<ValueExpression> restrictionParameters;

    private List<ValueExpression> restrictions = new ArrayList<ValueExpression>(0);

    private final List<Map.Entry<String, String>> sortFields = new ArrayList<Map.Entry<String, String>>();

    private final Map<String, Map.Entry<String, String>> sortFieldsMap = new HashMap<String, Map.Entry<String, String>>();

    private boolean useWildcardAsCountQuerySubject = true;

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * Wrap the result set in a JSF {@link DataModel}
     */
    @Transactional
    public DataModel<E> getDataModel()
    {
        if (dataModel == null) {
            dataModel = new ListDataModel<E>(getResultList());
        }
        return dataModel;
    }

    public String getEjbql()
    {
        return ejbql;
    }

    /**
     * Returns the index of the first result of the current page
     */
    public Integer getFirstResult()
    {
        return firstResult;
    }

    public String getGroupBy()
    {
        return groupBy;
    }

    /**
     * Group by used in count ejbql. By default it's "distinct " + #getGroupBy but if there is more than one column it may be overriden.
     *
     * @return group by part for count ejbql
     */
    protected String getCountGroupBy()
    {
        String by = getGroupBy();
        return by == null ? null : "distinct " + by;
    }

    public void setGroupBy(String groupBy)
    {
        this.groupBy = groupBy;
    }

    /**
     * The page size
     */
    public Integer getMaxResults()
    {
        return maxResults;
    }

    public String getOrderColumn()
    {
        return orderColumn;
    }

    public String getOrderDirection()
    {
        return orderDirection;
    }

    protected List<Object> getQueryParameterValues()
    {
        return queryParameterValues;
    }

    protected void setQueryParameterValues(List<Object> queryParameterValues)
    {
        this.queryParameterValues = queryParameterValues;
    }

    protected List<ValueExpression> getQueryParameters()
    {
        return queryParameters;
    }

    protected List<Object> getRestrictionParameterValues()
    {
        return restrictionParameterValues;
    }

    protected void setRestrictionParameterValues(List<Object> restrictionParameterValues)
    {
        this.restrictionParameterValues = restrictionParameterValues;
    }

    protected List<ValueExpression> getRestrictionParameters()
    {
        return restrictionParameters;
    }

    /**
     * List of restrictions to apply to the query.
     * <p/>
     * For a query such as 'from Foo f' a restriction could be
     * 'f.bar = #{foo.bar}'
     */
    public List<ValueExpression> getRestrictions()
    {
        return restrictions;
    }

    protected boolean isUseWildcardAsCountQuerySubject()
    {
        return useWildcardAsCountQuerySubject;
    }

    protected void setUseWildcardAsCountQuerySubject(boolean useCompliantCountQuerySubject)
    {
        this.useWildcardAsCountQuerySubject = useCompliantCountQuerySubject;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Clears sorting of results.
     */
    public void clearSorting()
    {
        this.sortFields.clear();
        sort(this.sortFields);
    }

    /**
     * Move the result set cursor to the beginning of the first page
     */
    public void first()
    {
        setFirstResult(0);
    }

    /**
     * Get the selected row of the JSF {@link DataModel}
     */
    public E getDataModelSelection()
    {
        return (E) getDataModel().getRowData();
    }

    /**
     * Get the index of the selected row of the JSF {@link DataModel}
     */
    public int getDataModelSelectionIndex()
    {
        return getDataModel().getRowIndex();
    }

    /**
     * Get the index of the first result of the last page
     */
    @Transactional
    public Long getLastFirstResult()
    {
        Integer pc = getPageCount();
        return pc == null ? null : (pc.longValue() - 1) * getMaxResults();
    }

    /**
     * Get the index of the first result of the next page
     */
    public int getNextFirstResult()
    {
        Integer fr = getFirstResult();
        return (fr == null ? 0 : fr) + getMaxResults();
    }

    /**
     * The order clause of the query
     */

    public String getOrder()
    {
        String column = getOrderColumn();

        if (column == null) {
            return order;
        }

        String direction = getOrderDirection();

        if (direction == null) {
            return column;
        } else {
            return column + ' ' + direction;
        }
    }

    /**
     * Get the total number of pages
     */
    @Transactional
    public Integer getPageCount()
    {
        if (getMaxResults() == null) {
            return null;
        } else {
            int rc = getResultCount().intValue();
            int mr = getMaxResults().intValue();
            int pages = rc / mr;
            return rc % mr == 0 ? pages : pages + 1;
        }
    }

    /**
     * Get the index of the first result of the previous page
     */
    public int getPreviousFirstResult()
    {
        Integer fr = getFirstResult();
        Integer mr = getMaxResults();
        return mr >= (fr == null ? 0 : fr) ? 0 : fr - mr;
    }

    public List<String> getRestrictionExpressionStrings()
    {
        List<String> expressionStrings = new ArrayList<String>();
        for (ValueExpression restriction : getRestrictions()) {
            expressionStrings.add(restriction.getExpressionString());
        }
        return expressionStrings;
    }

    public String getRestrictionLogicOperator()
    {
        return restrictionLogicOperator != null ? restrictionLogicOperator : LOGIC_OPERATOR_AND;
    }

    public abstract Long getResultCount();

    public abstract List<E> getResultList();

    public abstract E getSingleResult();

    public String getSortDir(String sortField)
    {
        final Map.Entry<String, String> entry = sortFieldsMap.get(sortField);
        return entry == null ? null : entry.getValue();
    }

    /**
     * Returns true if next page exists
     */
    public abstract boolean isNextExists();

    /**
     * Returns true if the query is paginated, revealing
     * whether navigation controls are needed.
     */
    public boolean isPaginated()
    {
        return isNextExists() || isPreviousExists();
    }

    /**
     * Returns true if the previous page exists
     */
    public boolean isPreviousExists()
    {
        return getFirstResult() != null && getFirstResult() != 0;
    }

    /**
     * Move the result set cursor to the beginning of the last page
     */
    @Transactional
    public void last()
    {
        setFirstResult(getLastFirstResult().intValue());
    }

    /**
     * Move the result set cursor to the beginning of the next page
     */
    public void next()
    {
        setFirstResult(getNextFirstResult());
    }

    /**
     * Move the result set cursor to the beginning of the previous page
     */
    public void previous()
    {
        setFirstResult(getPreviousFirstResult());
    }

    public void refresh()
    {
        clearDataModel();
    }

    /**
     * Set the ejbql to use.  Calling this causes the ejbql to be reparsed and
     * the query to be refreshed
     */
    public void setEjbql(String ejbql)
    {
        this.ejbql = ejbql;
        parsedEjbql = null;
        refresh();
    }

    /**
     * Set the index at which the page to display should start
     */
    public void setFirstResult(Integer firstResult)
    {
        this.firstResult = firstResult;
        refresh();
    }

    public void setMaxResults(Integer maxResults)
    {
        this.maxResults = maxResults;
        refresh();
    }

    public void setOrder(String order)
    {
        this.order = order;
        refresh();
    }

    public void setOrderColumn(String orderColumn)
    {
        this.orderColumn = sanitizeOrderColumn(orderColumn);
    }

    public void setOrderDirection(String orderDirection)
    {
        this.orderDirection = sanitizeOrderDirection(orderDirection);
    }

    /**
     * A convenience method for registering the restrictions from Strings. This
     * method is primarily intended to be used from Java, not to expose a bean
     * property for component configuration. Use setRestrictions() for the later.
     */
    public void setRestrictionExpressionStrings(List<String> expressionStrings)
    {
        List<ValueExpression> restrictionVEs = new ArrayList<ValueExpression>(expressionStrings.size());
        for (String expressionString : expressionStrings) {
            restrictionVEs.add(expressions.get().createValueExpression(expressionString));
        }
        setRestrictions(restrictionVEs);
    }

    public void setRestrictionLogicOperator(String operator)
    {
        restrictionLogicOperator = sanitizeRestrictionLogicOperator(operator);
    }

    /**
     * Calling setRestrictions causes the restrictions to be reparsed and the
     * query refreshed
     */
    public void setRestrictions(List<ValueExpression> restrictions)
    {
        this.restrictions = restrictions;
        parsedRestrictions = null;
        refresh();
    }

    /**
     * Sets sort order accordingly to provided list. Columns order will be as in list.
     * List should contain Map.Entry elements where key is column name and value is the order (asc,desc).
     *
     * @param sortFields list of entries representig column sort order
     */
    public void sort(List<Map.Entry<String, String>> sortFields)
    {
        /**
         * if sortFields param is different object then current attribute then we want to work on a copy
         */
        if (this.sortFields != sortFields) {
            this.sortFields.clear();
            this.sortFields.addAll(sortFields);
        }
        sortFieldsMap.clear();
        for (Map.Entry<String, String> entry : this.sortFields) {
            sortFieldsMap.put(entry.getKey(), entry);
        }
        /**
         * build order part of EJBQL
         */
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : this.sortFields) {
            if (isSortable(entry.getKey())) {
                builder.append(",").append(entry.getKey()).append(" ").append(entry.getValue());
            }
        }
        setOrderColumn(null);
        setOrder(builder.length() > 0 ? builder.substring(1) : null);
    }

    /**
     * Changes single column sort order on given column. Column must be something accesible from EJBQL.
     * If it doesn't belong to sorting order then direction is ASC.
     *
     * @param sortField name of field to toggle sorting direction for
     */
    public void toggleSort(String sortField)
    {
        toggleSort(sortField, true);
    }

    /**
     * Changes sort order on given column. Column must be something accesible from EJBQL.
     * If column already belongs to sorting order then only it's direction is changed.
     * If it doesn't belong to sorting order then it is appended to the end with default ASC direction.
     *
     * @param sortField  name of field to toggle sorting direction for
     * @param singleSort if true other fields will be removed from sorting
     */
    public void toggleSort(String sortField, boolean singleSort)
    {
        if (isSortable(sortField)) {
            Map.Entry<String, String> entry = sortFieldsMap.get(sortField);
            if (entry != null) {
                String order = DIR_ASC.equals(entry.getValue()) ? DIR_DESC : DIR_ASC;
                entry.setValue(order);
            } else {
                entry = new AbstractMap.SimpleEntry<String, String>(sortField, DIR_ASC);
                sortFields.add(entry);
            }
            if (singleSort && sortFields.size() > 1) {
                sortFields.clear();
                sortFields.add(entry);
            }
            sort(sortFields);
        } else {
            throw new IllegalArgumentException("Unsupported sort field: " + sortField);
        }
    }

    @PostConstruct
    public void validate()
    {
        if (getEjbql() == null) {
            throw new IllegalStateException("ejbql is null");
        }
    }

    protected void appendRestrictionsEjbql(StringBuilder builder)
    {
        for (int i = 0; i < getRestrictions().size(); i++) {
            Object parameterValue = restrictionParameters.get(i).getValue(facesContext.get().getELContext());
            if (isRestrictionParameterSet(parameterValue)) {
                if (WHERE_PATTERN.matcher(builder).find()) {
                    builder.append(" ").append(getRestrictionLogicOperator()).append(" ");
                } else {
                    builder.append(" where ");
                }
                builder.append(parsedRestrictions.get(i));
            }
        }
    }

    protected void clearDataModel()
    {
        dataModel = null;
    }

    protected void evaluateAllParameters()
    {
        setQueryParameterValues(getParameterValues(getQueryParameters()));
        setRestrictionParameterValues(getParameterValues(getRestrictionParameters()));
    }

    /**
     * Return the ejbql to used in a count query (for calculating number of
     * results)
     *
     * @return String The ejbql query
     */
    protected String getCountEjbql()
    {
        String renderedEjbql = getRenderedEjbql();

        Matcher fromMatcher = FROM_PATTERN.matcher(renderedEjbql);
        if (!fromMatcher.find()) {
            throw new IllegalArgumentException("no from clause found in query");
        }
        int fromLoc = fromMatcher.start(2);

        // TODO can we just create a protected method that builds the query w/o the order by and group by clauses?
        Matcher orderMatcher = ORDER_PATTERN.matcher(renderedEjbql);
        int orderLoc = orderMatcher.find() ? orderMatcher.start(1) : renderedEjbql.length();

        Matcher groupMatcher = GROUP_PATTERN.matcher(renderedEjbql);
        int groupLoc = groupMatcher.find() ? groupMatcher.start(1) : orderLoc;

        Matcher whereMatcher = WHERE_PATTERN.matcher(renderedEjbql);
        int whereLoc = whereMatcher.find() ? whereMatcher.start(1) : groupLoc;

        String subject;
        if (getCountGroupBy() != null) {
            subject = getCountGroupBy();
        } else if (useWildcardAsCountQuerySubject) {
            subject = "*";
        } else {
            // to be JPA-compliant, we need to make this query like "select count(u) from User u"
            // however, Hibernate produces queries some databases cannot run when the primary key is composite
            Matcher subjectMatcher = SUBJECT_PATTERN.matcher(renderedEjbql);
            if (subjectMatcher.find()) {
                subject = subjectMatcher.group(1);
            } else {
                throw new IllegalStateException("invalid select clause for query");
            }
        }

        final int selectCountStringLength = 15;
        return new StringBuilder(renderedEjbql.length() + selectCountStringLength).append("select count(").append(subject).append(") ").
            append(renderedEjbql.substring(fromLoc, whereLoc).replace("join fetch", "join")).
            append(renderedEjbql.substring(whereLoc, groupLoc)).toString().trim();
    }

    private List<Object> getParameterValues(List<ValueExpression> valueBindings)
    {
        List<Object> values = new ArrayList<Object>(valueBindings.size());
        for (int i = 0; i < valueBindings.size(); i++) {
            values.add(valueBindings.get(i).getValue(facesContext.get().getELContext()));
        }
        return values;
    }

    protected String getRenderedEjbql()
    {
        StringBuilder builder = new StringBuilder().append(parsedEjbql);

        appendRestrictionsEjbql(builder);

        if (getGroupBy() != null) {
            builder.append(" group by ").append(getGroupBy());
        }

        if (getOrder() != null) {
            builder.append(" order by ").append(getOrder());
        }

        return builder.toString();
    }

    protected boolean isAnyParameterDirty()
    {
        return isAnyParameterDirty(getQueryParameters(), getQueryParameterValues()) || isAnyParameterDirty(getRestrictionParameters(),
            getRestrictionParameterValues());
    }

    private boolean isAnyParameterDirty(List<ValueExpression> valueBindings, List<Object> lastParameterValues)
    {
        if (lastParameterValues == null) {
            return true;
        }
        for (int i = 0; i < valueBindings.size(); i++) {
            Object parameterValue = valueBindings.get(i).getValue(facesContext.get().getELContext());
            Object lastParameterValue = lastParameterValues.get(i);
            //treat empty strings as null, for consistency with isRestrictionParameterSet()
            if ("".equals(parameterValue)) {
                parameterValue = null;
            }
            if ("".equals(lastParameterValue)) {
                lastParameterValue = null;
            }
            if (parameterValue != lastParameterValue && (parameterValue == null || !parameterValue.equals(lastParameterValue))) {
                return true;
            }
        }
        return false;
    }

    protected boolean isRestrictionParameterSet(Object parameterValue)
    {
        return parameterValue != null && !"".equals(parameterValue) && (parameterValue instanceof Collection ? !((Collection) parameterValue).isEmpty() : true);
    }

    /**
     * Tells if this query can sort on given field.
     * By default this method returns always true for non null param values.
     * If you want to limit this please override this method in subclass
     * .
     *
     * @param sortField name of field to sort on
     *
     * @return true if query can sort on given field; false otherwise
     */
    protected boolean isSortable(String sortField)
    {
        return sortField != null;
    }

    protected void parseEjbql()
    {
        if (parsedEjbql == null || parsedRestrictions == null) {
            QueryParser qp = new QueryParser(getEjbql(), expressions.get());
            queryParameters = qp.getParameterValueBindings();
            parsedEjbql = qp.getEjbql();

            List<ValueExpression> restrictionFragments = getRestrictions();
            parsedRestrictions = new ArrayList<String>(restrictionFragments.size());
            restrictionParameters = new ArrayList<ValueExpression>(restrictionFragments.size());
            for (ValueExpression restriction : restrictionFragments) {
                QueryParser rqp = new QueryParser(restriction.getExpressionString(), queryParameters.size() + restrictionParameters.size(), expressions.get());
                if (rqp.getParameterValueBindings().size() != 1) {
                    throw new IllegalArgumentException("there should be exactly one value binding in a restriction: " + restriction);
                }
                parsedRestrictions.add(rqp.getEjbql());
                restrictionParameters.addAll(rqp.getParameterValueBindings());
            }
        }
    }

    private String sanitizeOrderColumn(String columnName)
    {
        if (columnName == null || columnName.trim().length() == 0) {
            return null;
        } else if (ORDER_COLUMN_PATTERN.matcher(columnName).find()) {
            return columnName;
        } else {
            throw new IllegalArgumentException(
                "invalid order column (\"" + columnName + "\" must match the regular expression \"" + ORDER_COLUMN_PATTERN + "\")");
        }
    }

    private String sanitizeOrderDirection(String direction)
    {
        if (direction == null || direction.length() == 0) {
            return null;
        } else if (direction.equalsIgnoreCase(DIR_ASC)) {
            return DIR_ASC;
        } else if (direction.equalsIgnoreCase(DIR_DESC)) {
            return DIR_DESC;
        } else {
            throw new IllegalArgumentException("invalid order direction");
        }
    }

    private String sanitizeRestrictionLogicOperator(String operator)
    {
        if (operator == null || operator.trim().length() == 0) {
            return LOGIC_OPERATOR_AND;
        }
        if (!(LOGIC_OPERATOR_AND.equals(operator) || LOGIC_OPERATOR_OR.equals(operator))) {
            throw new IllegalArgumentException("Invalid restriction logic operator: " + operator);
        } else {
            return operator;
        }
    }

    protected List<E> truncResultList(List<E> results)
    {
        Integer mr = getMaxResults();
        if (mr != null && results.size() > mr) {
            return results.subList(0, mr);
        } else {
            return results;
        }
    }
}
