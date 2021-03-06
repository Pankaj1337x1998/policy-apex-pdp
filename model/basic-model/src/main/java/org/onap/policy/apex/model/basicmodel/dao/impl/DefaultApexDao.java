/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.model.basicmodel.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.dao.ApexDao;
import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class DefaultApexDao is an JPA implementation of the {@link ApexDao} class for Apex concepts ({@link AxConcept}).
 * It uses the default JPA implementation in the javax {@link Persistence} class.
 *
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class DefaultApexDao implements ApexDao {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(DefaultApexDao.class);

    private static final String SELECT_C_FROM = "SELECT c FROM ";
    private static final String AND_C_KEY_LOCAL_NAME = "' AND c.key.localName='";
    private static final String AND_C_KEY_PARENT_KEY_VERSION = "' AND c.key.parentKeyVersion='";
    private static final String C_WHERE_C_KEY_PARENT_KEY_NAME = " c WHERE c.key.parentKeyName='";
    private static final String AND_C_KEY_VERSION = "' AND c.key.version='";
    private static final String C_WHERE_C_KEY_NAME = " c WHERE c.key.name='";
    private static final String DELETE_FROM = "DELETE FROM ";

    // Entity manager for JPA
    private EntityManagerFactory emf = null;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final DaoParameters daoParameters) throws ApexException {
        if (daoParameters == null || daoParameters.getPersistenceUnit() == null) {
            LOGGER.error("Apex persistence unit parameter not set");
            throw new ApexException("Apex persistence unit parameter not set");
        }

        LOGGER.debug("Creating Apex persistence unit \"" + daoParameters.getPersistenceUnit() + "\" . . .");
        try {
            emf = Persistence.createEntityManagerFactory(daoParameters.getPersistenceUnit(),
                    daoParameters.getJdbcProperties());
        } catch (final Exception e) {
            LOGGER.warn("Creation of Apex persistence unit \"" + daoParameters.getPersistenceUnit() + "\" failed", e);
            throw new ApexException(
                    "Creation of Apex persistence unit \"" + daoParameters.getPersistenceUnit() + "\" failed", e);
        }
        LOGGER.debug("Created Apex persistence unit \"" + daoParameters.getPersistenceUnit() + "\"");
    }

    /**
     * Gets the entity manager for this DAO.
     *
     * @return the entity manager
     */
    protected final synchronized EntityManager getEntityManager() {
        if (emf == null) {
            LOGGER.warn("Apex DAO has not been initialized");
            throw new ApexRuntimeException("Apex DAO has not been initialized");
        }

        return emf.createEntityManager();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void close() {
        if (emf != null) {
            emf.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> void create(final T obj) {
        if (obj == null) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.merge(obj);
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> void delete(final T obj) {
        if (obj == null) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.remove(mg.contains(obj) ? obj : mg.merge(obj));
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> void delete(final Class<T> someClass, final AxArtifactKey key) {
        if (key == null) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.createQuery(DELETE_FROM + someClass.getSimpleName() + C_WHERE_C_KEY_NAME + key.getName()
                    + AND_C_KEY_VERSION + key.getVersion() + "'", someClass).executeUpdate();
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> void delete(final Class<T> someClass, final AxReferenceKey key) {
        if (key == null) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.createQuery(DELETE_FROM + someClass.getSimpleName() + C_WHERE_C_KEY_PARENT_KEY_NAME
                    + key.getParentKeyName() + AND_C_KEY_PARENT_KEY_VERSION + key.getParentKeyVersion()
                    + AND_C_KEY_LOCAL_NAME + key.getLocalName() + "'", someClass).executeUpdate();
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> void createCollection(final Collection<T> objs) {
        if (objs == null || objs.isEmpty()) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            for (final T t : objs) {
                mg.merge(t);
            }
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> void deleteCollection(final Collection<T> objs) {
        if (objs == null || objs.isEmpty()) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            for (final T t : objs) {
                mg.remove(mg.contains(t) ? t : mg.merge(t));
            }
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> int deleteByArtifactKey(final Class<T> someClass,
            final Collection<AxArtifactKey> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        int deletedCount = 0;
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            for (final AxArtifactKey key : keys) {
                deletedCount += mg.createQuery(DELETE_FROM + someClass.getSimpleName() + C_WHERE_C_KEY_NAME
                        + key.getName() + AND_C_KEY_VERSION + key.getVersion() + "'", someClass).executeUpdate();
            }
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
        return deletedCount;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> int deleteByReferenceKey(final Class<T> someClass,
            final Collection<AxReferenceKey> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        int deletedCount = 0;
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            for (final AxReferenceKey key : keys) {
                deletedCount +=
                        mg.createQuery(
                                DELETE_FROM + someClass.getSimpleName() + C_WHERE_C_KEY_PARENT_KEY_NAME
                                        + key.getParentKeyName() + AND_C_KEY_PARENT_KEY_VERSION
                                        + key.getParentKeyVersion() + AND_C_KEY_LOCAL_NAME + key.getLocalName() + "'",
                                someClass).executeUpdate();
            }
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
        return deletedCount;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> void deleteAll(final Class<T> someClass) {
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.createQuery(DELETE_FROM + someClass.getSimpleName() + " c ", someClass).executeUpdate();
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> T get(final Class<T> someClass, final AxArtifactKey key) {
        if (someClass == null) {
            return null;
        }
        final EntityManager mg = getEntityManager();
        try {
            final T t = mg.find(someClass, key);
            if (t != null) {
                // This clone is created to force the JPA DAO to recurse down through the object
                try {
                    final T clonedT = someClass.getDeclaredConstructor().newInstance();
                    t.copyTo(clonedT);
                    return clonedT;
                } catch (final Exception e) {
                    LOGGER.warn("Could not clone object of class \"" + someClass.getName() + "\"", e);
                    return null;
                }
            } else {
                return null;
            }
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> T get(final Class<T> someClass, final AxReferenceKey key) {
        if (someClass == null) {
            return null;
        }
        final EntityManager mg = getEntityManager();
        try {
            final T t = mg.find(someClass, key);
            if (t != null) {
                try {
                    final T clonedT = someClass.getDeclaredConstructor().newInstance();
                    t.copyTo(clonedT);
                    return clonedT;
                } catch (final Exception e) {
                    LOGGER.warn("Could not clone object of class \"" + someClass.getName() + "\"", e);
                    return null;
                }
            } else {
                return null;
            }
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> List<T> getAll(final Class<T> someClass) {
        if (someClass == null) {
            return Collections.emptyList();
        }
        final EntityManager mg = getEntityManager();
        try {
            return mg.createQuery(SELECT_C_FROM + someClass.getSimpleName() + " c", someClass).getResultList();
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> List<T> getAll(final Class<T> someClass, final AxArtifactKey parentKey) {
        if (someClass == null) {
            return Collections.emptyList();
        }
        final EntityManager mg = getEntityManager();
        try {
            return mg
                    .createQuery(
                            SELECT_C_FROM + someClass.getSimpleName() + C_WHERE_C_KEY_PARENT_KEY_NAME
                                    + parentKey.getName() + AND_C_KEY_PARENT_KEY_VERSION + parentKey.getVersion() + "'",
                            someClass)
                    .getResultList();
        } finally {
            mg.close();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> T getArtifact(final Class<T> someClass, final AxArtifactKey key) {
        if (someClass == null || key == null) {
            return null;
        }
        final EntityManager mg = getEntityManager();
        List<T> ret;
        try {
            ret = mg.createQuery(SELECT_C_FROM + someClass.getSimpleName() + C_WHERE_C_KEY_NAME + key.getName()
                    + AND_C_KEY_VERSION + key.getVersion() + "'", someClass).getResultList();
        } finally {
            mg.close();
        }
        if (ret == null || ret.isEmpty()) {
            return null;
        }
        if (ret.size() > 1) {
            throw new IllegalArgumentException("More than one result was returned for search for " + someClass
                    + " with key " + key.getId() + ": " + ret);
        }
        return ret.get(0);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> T getArtifact(final Class<T> someClass, final AxReferenceKey key) {
        if (someClass == null || key == null) {
            return null;
        }
        final EntityManager mg = getEntityManager();
        List<T> ret;
        try {
            ret = mg.createQuery(SELECT_C_FROM + someClass.getSimpleName() + C_WHERE_C_KEY_PARENT_KEY_NAME
                    + key.getParentKeyName() + AND_C_KEY_PARENT_KEY_VERSION + key.getParentKeyVersion()
                    + AND_C_KEY_LOCAL_NAME + key.getLocalName() + "'", someClass).getResultList();
        } finally {
            mg.close();
        }
        if (ret == null || ret.isEmpty()) {
            return null;
        }
        if (ret.size() > 1) {
            throw new IllegalArgumentException("More than one result was returned for search for " + someClass
                    + " with key " + key.getId() + ": " + ret);
        }
        return ret.get(0);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> T update(final T obj) {
        final EntityManager mg = getEntityManager();
        T ret;
        try {
            mg.getTransaction().begin();
            ret = mg.merge(obj);
            mg.flush();
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
        return ret;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T extends AxConcept> long size(final Class<T> someClass) {
        if (someClass == null) {
            return 0;
        }
        final EntityManager mg = getEntityManager();
        long size = 0;
        try {
            size = mg.createQuery("SELECT COUNT(c) FROM " + someClass.getSimpleName() + " c", Long.class)
                    .getSingleResult();
        } finally {
            mg.close();
        }
        return size;
    }
}
