package pl.itcrowd.seam3.persistence;

import org.jboss.seam.transaction.DefaultTransaction;
import org.jboss.seam.transaction.SeamTransaction;
import org.jboss.seam.transaction.Transactional;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.enterprise.inject.Instance;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;
import java.util.List;
import java.util.Map;

/**
 * A Query object for JPA.
 *
 * @author Gavin King
 */
public abstract class EntityQuery<E> extends Query<EntityManager, E> {
// ------------------------------ FIELDS ------------------------------

    @Inject
    @DefaultTransaction
    protected Instance<SeamTransaction> transaction;

    @Inject
    private Instance<EntityManager> entityManagerInstance;

    @Inject
    private Instance<MyExpressions> expressionsInstance;

    private Map<String, String> hints;

    private Long resultCount;

    private List<E> resultList;

    private E singleResult;

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public DataModel<E> getDataModel()
    {
        if (dataModel == null) {
            dataModel = new EntityQueryDataModel<E>(this);
        }
        return dataModel;
    }

    public Map<String, String> getHints()
    {
        return hints;
    }

    public void setHints(Map<String, String> hints)
    {
        this.hints = hints;
    }

    /**
     * Get the number of results this query returns
     * <p/>
     * Any changed restriction values will be applied
     */
    @Transactional
    @Override
    public Long getResultCount()
    {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initResultCount();
        return resultCount;
    }

    /**
     * Get a single result from the query
     * <p/>
     * Any changed restriction values will be applied
     *
     * @throws javax.persistence.NonUniqueResultException
     *          if there is more than one result
     */
    @Transactional
    @Override
    public E getSingleResult()
    {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initSingleResult();
        return singleResult;
    }

// -------------------------- OTHER METHODS --------------------------

    public EntityManager getEntityManager()
    {
        return entityManagerInstance.get();
    }

    /**
     * Get the list of results this query returns
     * <p/>
     * Any changed restriction values will be applied
     */
    @Transactional
    @Override
    public List<E> getResultList()
    {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initResultList();
        return truncResultList(resultList);
    }

    @Override
    @Transactional
    public boolean isNextExists()
    {
        return resultList != null && getMaxResults() != null && resultList.size() > getMaxResults();
    }

    /**
     * The refresh method will cause the result to be cleared.  The next access
     * to the result set will cause the query to be executed.
     * <p/>
     * This method <b>does not</b> cause the ejbql or restrictions to reread.
     * If you want to update the ejbql or restrictions you must call
     * {@link #setEjbql(String)} or {@link #setRestrictions(java.util.List)}
     */
    @Override
    public void refresh()
    {
        super.refresh();
        resultCount = null;
        resultList = null;
        singleResult = null;
    }

    /**
     * Validate the query
     *
     * @throws IllegalStateException if the query is not valid
     */
    @Override
    public void validate()
    {
        super.validate();
        if (getEntityManager() == null) {
            throw new IllegalStateException("entityManager is null");
        }
//TODO this is remain of Seam2. I don't know how to check this cause persistenceProvider doesn't have supportsFeature method
//        We could use DefaultPersistenceProvider, but it is in impl
//      if (!PersistenceProvider.instance().supportsFeature(Feature.WILDCARD_AS_COUNT_QUERY_SUBJECT)) {
//         setUseWildcardAsCountQuerySubject(false);
//      }
    }

    protected javax.persistence.Query createCountQuery()
    {
        parseEjbql();

        evaluateAllParameters();

        joinTransaction();

        javax.persistence.Query query = getEntityManager().createQuery(getCountEjbql());
        setParameters(query, getQueryParameterValues(), 0);
        setParameters(query, getRestrictionParameterValues(), getQueryParameterValues().size());
        return query;
    }

    protected javax.persistence.Query createQuery()
    {
        parseEjbql();

        evaluateAllParameters();

        joinTransaction();

        javax.persistence.Query query = getEntityManager().createQuery(getRenderedEjbql());
        setParameters(query, getQueryParameterValues(), 0);
        setParameters(query, getRestrictionParameterValues(), getQueryParameterValues().size());
        if (getFirstResult() != null) {
            query.setFirstResult(getFirstResult());
        }
        if (getMaxResults() != null) {
            query.setMaxResults(getMaxResults() + 1);
        } //add one, so we can tell if there is another page
        if (getHints() != null) {
            for (Map.Entry<String, String> me : getHints().entrySet()) {
                query.setHint(me.getKey(), me.getValue());
            }
        }
        return query;
    }

    protected MyExpressions getExpressions()
    {
        return expressionsInstance.get();
    }

    private void initResultCount()
    {
        if (resultCount == null) {
            javax.persistence.Query query = createCountQuery();
            resultCount = query == null ? null : (Long) query.getSingleResult();
        }
    }

    private void initResultList()
    {
        if (resultList == null) {
            javax.persistence.Query query = createQuery();
            resultList = query == null ? null : query.getResultList();
        }
    }

    private void initSingleResult()
    {
        if (singleResult == null) {
            javax.persistence.Query query = createQuery();
            singleResult = (E) (query == null ? null : query.getSingleResult());
        }
    }

    protected void joinTransaction()
    {
        try {
            transaction.get().enlist(getEntityManager());
        } catch (SystemException se) {
            throw new RuntimeException("could not join transaction", se);
        }
    }

    private void setParameters(javax.persistence.Query query, List<Object> parameters, int start)
    {
        for (int i = 0; i < parameters.size(); i++) {
            Object parameterValue = parameters.get(i);
            if (isRestrictionParameterSet(parameterValue)) {
                query.setParameter(QueryParser.getParameterName(start + i), parameterValue);
            }
        }
    }

// -------------------------- INNER CLASSES --------------------------

    public static class MyExpressions {
// ------------------------------ FIELDS ------------------------------

        private ELContext elContext;

        private ExpressionFactory expressionFactory;

// --------------------------- CONSTRUCTORS ---------------------------

        /**
         * Create a new instance of the {@link org.jboss.seam.solder.el.Expressions} class, providing the
         * {@link javax.el.ELContext} and {@link javax.el.ExpressionFactory} to be used.
         *
         * @param elContext           the {@link javax.el.ELContext} against which to operate
         * @param expressionFactory the {@link javax.el.ExpressionFactory} to use
         *
         * @throws IllegalArgumentException if <code>context</code> is null or
         *                                  <code>expressionFactory</code> is null
         */
        @Inject
        public MyExpressions(ELContext elContext, ExpressionFactory expressionFactory)
        {
            if (elContext == null) {
                throw new IllegalArgumentException("context must not be null");
            }
            if (expressionFactory == null) {
                throw new IllegalArgumentException("expressionFactory must not be null");
            }
            this.elContext = elContext;
            this.expressionFactory = expressionFactory;
        }

// --------------------- GETTER / SETTER METHODS ---------------------

        public ELContext getElContext()
        {
            return elContext;
        }

        public ExpressionFactory getExpressionFactory()
        {
            return expressionFactory;
        }

// -------------------------- OTHER METHODS --------------------------

        public ValueExpression createValueExpression(String expression)
        {
            return this.getExpressionFactory().createValueExpression(getElContext(), expression, Object.class);
        }
    }
}
