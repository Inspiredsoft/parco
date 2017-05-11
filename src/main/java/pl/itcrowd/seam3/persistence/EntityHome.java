package pl.itcrowd.seam3.persistence;

import org.jboss.seam.persistence.ManagedPersistenceContext;
import org.jboss.seam.persistence.SeamPersistenceProvider;
import org.jboss.seam.transaction.Transactional;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Base class for Home objects of JPA entities.
 *
 * @author Gavin King
 */
@SuppressWarnings({"ManagedBeanInconsistencyInspection"})
public abstract class EntityHome<E> extends Home<EntityManager, E> {
// ------------------------------ FIELDS ------------------------------

    @Inject
    protected BeanManager beanManager;

    @Inject
    protected Instance<EntityManager> entityManagerInstance;

    @Inject
    protected Instance<SeamPersistenceProvider> persistenceProvider;

    @Inject
    @Any
    private Instance<ManagedPersistenceContext> managedPersistenceContexts;

// -------------------------- OTHER METHODS --------------------------

    /**
     * Implementation of {@link Home#find() find()} for JPA
     *
     * @see Home#find()
     */
    @Transactional
    @Override
    public E find()
    {
        if (getEntityManager().isOpen()) {
            E result = loadInstance();
            if (result == null) {
                result = handleNotFound();
            }
            return result;
        } else {
            return null;
        }
    }

    public EntityManager getEntityManager()
    {
        return entityManagerInstance.get();
    }

    /**
     * Returns true if the entity instance is managed
     */
    @Transactional
    public boolean isManaged()
    {
        return getInstance() != null && getEntityManager().contains(getInstance());
    }

    @Transactional
    public boolean persist()
    {
        final EntityManager entityManager = getEntityManager();
        entityManager.persist(getInstance());
        entityManager.flush();
        for (ManagedPersistenceContext context : managedPersistenceContexts) {
            final Object id = context.getProvider().getId(getInstance(), getEntityManager());
            if (id != null) {
                assignId(id);
                break;
            }
        }
        beanManager.fireEvent(getInstance(), new AnnotationLiteral<EntityPersisted>() {
        });
        return true;
    }

    @Transactional
    public boolean remove()
    {
        final EntityManager entityManager = getEntityManager();
        entityManager.remove(getInstance());
        entityManager.flush();
        beanManager.fireEvent(getInstance(), new AnnotationLiteral<EntityRemoved>() {
        });
        return true;
    }

    @Transactional
    public boolean update()
    {
        joinTransaction();
        final EntityManager entityManager = getEntityManager();
        setInstance(entityManager.merge(getInstance()));
        entityManager.flush();
        beanManager.fireEvent(getInstance(), new AnnotationLiteral<EntityUpdated>() {
        });
        return true;
    }

    /**
     * Implementation of {@link Home#getEntityName() getEntityName()} for JPA
     *
     * @see Home#getEntityName()
     */
    @Override
    protected String getEntityName()
    {
        try {
            return persistenceProvider.get().getName(getInstance(), getEntityManager());
        } catch (IllegalArgumentException e) {
            // Handle that the passed object may not be an entity
            return null;
        }
    }

    /**
     * Implementation of {@link Home#joinTransaction() joinTransaction()} for
     * JPA.
     */
    @Override
    protected void joinTransaction()
    {
        if (getEntityManager().isOpen()) {
            try {
                transaction.get().enlist(getEntityManager());
            } catch (javax.transaction.SystemException se) {
                throw new RuntimeException("could not join transaction", se);
            }
        }
    }

    /**
     * Utility method to load entity instance from the {@link EntityManager}.
     * Called by {@link #find()}.
     * <br />
     * Can be overridden to support eager fetching of associations.
     *
     * @return The entity identified by {@link Home#getEntityClass() getEntityClass()},
     *         {@link Home#getId() getId()}
     */
    protected E loadInstance()
    {
        return getEntityManager().find(getEntityClass(), getId());
    }
}

