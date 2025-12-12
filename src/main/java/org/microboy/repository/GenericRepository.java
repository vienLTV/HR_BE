package org.microboy.repository;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.microboy.constants.ExceptionConstants;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Contains common CRUD methods
 *
 * @author Khanh Tran
 * @param <T>
 * @param <ID>
 */
@Slf4j
public abstract class GenericRepository<T, ID> {

	@Inject
	EntityManager entityManager;

	private Class<T> entityClass;

	private void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	@SuppressWarnings("unchecked")
	private Class<T> resolveReturnedClassFromGenericType() {
		ParameterizedType parameterizedType = this.resolveReturnedClassFromGenericType(this.getClass());
		setEntityClass((Class) parameterizedType.getActualTypeArguments()[0]);
		return entityClass;
	}

	private Class<T> getEntityClass() {
		if (entityClass == null) {
			try {
				this.entityClass = this.resolveReturnedClassFromGenericType();
			} catch (Exception ex) {
				log.error("Unable to resolve EntityClass. Please use according setter!");
			}
		}

		return this.entityClass;
	}

	private ParameterizedType resolveReturnedClassFromGenericType(Class<?> clazz) {
		Object genericSuperclass = clazz.getGenericSuperclass();
		if (genericSuperclass instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType)genericSuperclass;
			Type rawtype = parameterizedType.getRawType();
			if (GenericRepository.class.equals(rawtype)) {
				return parameterizedType;
			}
		}

		return this.resolveReturnedClassFromGenericType(clazz.getSuperclass());
	}

	@Transactional
	public T save(T entity) {
		entityManager.persist(entity);
		return entity;
	}

	@Transactional
	public T update(T entity) {
		return entityManager.merge(entity);
	}

	public List<T> findAll() {
		var criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
		Root<T> root = criteriaQuery.from(getEntityClass());

		criteriaQuery.select(root);
		TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

		return query.getResultList();
	}

	public T findById(ID id) {
		return entityManager.find(getEntityClass(), id);
	}

	public boolean existById(ID id) {
		return findById(id) != null;
	}

	@Transactional
	public void deleteById(ID id) {
		T entity = findById(id);
		if (entity != null) {
			entityManager.remove(entity);
		} else {
			throw new EntityNotFoundException(ExceptionConstants.ENTITY_NOT_FOUND);
		}
	}

	@Transactional
	public void delete(T entity) {
		entityManager.remove(entity);
	}

	List<T> findAllByOffset(int from, int offset) {
		var criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
		Root<T> root = criteriaQuery.from(getEntityClass());
		criteriaQuery.select(root);

		TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
		query.setFirstResult(from);
		query.setMaxResults(offset);

		return query.getResultList();
	}
}
