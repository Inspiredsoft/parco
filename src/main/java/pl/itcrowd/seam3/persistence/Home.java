package pl.itcrowd.seam3.persistence;

import org.jboss.seam.transaction.DefaultTransaction;
import org.jboss.seam.transaction.SeamTransaction;
import org.jboss.seam.transaction.Transactional;
import org.jboss.solder.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Base class for components which provide persistence
 * operations to a managed entity instance. This class
 * may be reused by either configuration or extension,
 * and may be bound directly to a view, or accessed by
 * some intermediate Seam component.
 *
 * @author Gavin King
 */
@SuppressWarnings({"ManagedBeanInconsistencyInspection"})
public abstract class Home<T, E> extends MutableController<T> {
// ------------------------------ FIELDS ------------------------------

    protected E instance;

    @Inject
    @DefaultTransaction
    protected Instance<SeamTransaction> transaction;

    private Class<E> entityClass;

    private Object id;

    @Inject
    private Logger log;

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * Get the class of the entity being managed.
     * <br />
     * If not explicitly specified, the generic type of implementation is used.
     */
    public Class<E> getEntityClass()
    {
        if (entityClass == null) {
            Class clazz = getClass();
            /**
             * Loop is necessary if SecurityInterceptor (and probably others) are turned on and this method is invoked on some proxy
             */
            while (entityClass == null && !Object.class.equals(clazz.getSuperclass())) {
                Type type = clazz.getGenericSuperclass();
//            This is for as7 i think
//            Type type = getClass().getSuperclass().getGenericSuperclass();
                if (type instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) type;
                    if (paramType.getActualTypeArguments().length == 2) {
                        // likely dealing with -> new EntityHome<Person>().getEntityClass()
                        if (paramType.getActualTypeArguments()[1] instanceof TypeVariable) {
                            throw new IllegalArgumentException("Could not guess entity class by reflection");
                        } else {
                            // likely dealing with -> new Home<EntityManager, Person>() { ... }.getEntityClass()
                            entityClass = (Class<E>) paramType.getActualTypeArguments()[1];
                        }
                    } else {
                        // likely dealing with -> new PersonHome().getEntityClass() where PersonHome extends EntityHome<Person>
                        entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
                    }
                }
                clazz = clazz.getSuperclass();
            }
            if (entityClass == null) {
                throw new IllegalArgumentException("Could not guess entity class by reflection");
            }
        }
        return entityClass;
    }

    /**
     * Set the class of the entity being managed.
     * <br />
     * Useful for configuring {@link Home} components from
     * <code>components.xml</code>.
     */
    public void setEntityClass(Class<E> entityClass)
    {
        this.entityClass = entityClass;
    }

    /**
     * Get the id of the object being managed.
     */
    public Object getId()
    {
        return id;
    }

    /**
     * Set/change the entity being managed by id.
     *
     * @see #assignId(Object)
     */
    public void setId(Object id)
    {
        if (setDirty(this.id, id)) {
            setInstance(null);
        }
        this.id = id;
    }

    /**
     * Get the managed entity, using the id from {@link #getId()} to load it from
     * the Persistence Context or creating a new instance if the id is not
     * defined.
     *
     * @see #getId()
     */
    @Transactional
    public E getInstance()
    {
        joinTransaction();
        if (instance == null) {
            initInstance();
        }
        return instance;
    }

    /**
     * Set/change the entity being managed.
     */
    public void setInstance(E instance)
    {
        setDirty(this.instance, instance);
        this.instance = instance;
    }

    protected Logger getLog()
    {
        return log;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Clear the managed entity (and id), allowing the {@link EntityHome} to be
     * reused.
     */
    public void clearInstance()
    {
        setInstance(null);
        setId(null);
    }

    /**
     * Run on {@link Home} instantiation to check the Home component is in a
     * valid state.
     * <br />
     * Validates that the class of the entity to be managed has been specified.
     */
    @PostConstruct
    public void create()
    {
        if (getEntityClass() == null) {
            throw new IllegalStateException("entityClass is null");
        }
    }

    /**
     * Returns true if the id of the object managed is known.
     */
    public boolean isIdDefined()
    {
        return getId() != null && !"".equals(getId());
    }

    /**
     * Set the id of entity being managed.
     * <br />
     * Does not alter the instance so used if the id of the managed object is
     * changed.
     *
     * @see #setId(Object)
     */
    protected void assignId(Object identifier)
    {
        setDirty(this.id, identifier);
        this.id = identifier;
    }

    /**
     * Create a new instance of the entity.
     * <br />
     * Utility method called by {@link #initInstance()} to create a new instance
     * of the entity.
     */
    protected E createInstance()
    {
        if (getEntityClass() != null) {
            try {
                return getEntityClass().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    /**
     * Hook method called by {@link #initInstance()} to allow the implementation
     * to load the entity from the Persistence Context.
     */
    protected E find()
    {
        return null;
    }

    /**
     * Hook method to get the name of the managed entity
     */
    protected abstract String getEntityName();

    /**
     * The simple name of the managed entity
     */
    protected String getSimpleEntityName()
    {
        String name = getEntityName();
        if (name != null) {
            return name.lastIndexOf(".") > 0 && name.lastIndexOf(".") < name.length() ? name.substring(name.lastIndexOf(".") + 1, name.length()) : name;
        } else {
            return null;
        }
    }

    /**
     * Utility method called by the framework when no entity is found in the
     * Persistence Context.
     */
    protected E handleNotFound()
    {
        throw new EntityNotFoundException(String.format("Entity of type %s with id: %s not found", getEntityClass(), getId()));
    }

    /**
     * Load the instance if the id is defined otherwise create a new instance
     * <br />
     * Utility method called by {@link #getInstance()} to load the instance from
     * the Persistence Context if the id is defined. Otherwise a new instance is
     * created.
     *
     * @see #find()
     * @see #createInstance()
     */
    protected void initInstance()
    {
        if (isIdDefined()) {
            if (!isTransactionMarkedRollback()) {
                //we cache the instance so that it does not "disappear"
                //after remove() is called on the instance
                //is this really a Good Idea??
                setInstance(find());
            }
        } else {
            setInstance(createInstance());
        }
    }

    protected boolean isTransactionMarkedRollback()
    {
        try {
            return transaction.get().isMarkedRollback();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Hook method called to allow the implementation to join the current
     * transaction when necessary.
     */
    protected void joinTransaction()
    {
    }
}

