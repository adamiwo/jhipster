package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.UsersList;
import com.mycompany.myapp.repository.UsersListRepository;
import com.mycompany.myapp.service.criteria.UsersListCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UsersList} entities in the database.
 * The main input is a {@link UsersListCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UsersList} or a {@link Page} of {@link UsersList} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UsersListQueryService extends QueryService<UsersList> {

    private final Logger log = LoggerFactory.getLogger(UsersListQueryService.class);

    private final UsersListRepository usersListRepository;

    public UsersListQueryService(UsersListRepository usersListRepository) {
        this.usersListRepository = usersListRepository;
    }

    /**
     * Return a {@link List} of {@link UsersList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UsersList> findByCriteria(UsersListCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UsersList> specification = createSpecification(criteria);
        return usersListRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link UsersList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UsersList> findByCriteria(UsersListCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UsersList> specification = createSpecification(criteria);
        return usersListRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UsersListCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UsersList> specification = createSpecification(criteria);
        return usersListRepository.count(specification);
    }

    /**
     * Function to convert {@link UsersListCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UsersList> createSpecification(UsersListCriteria criteria) {
        Specification<UsersList> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UsersList_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), UsersList_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), UsersList_.lastName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), UsersList_.email));
            }
            if (criteria.getAdress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAdress(), UsersList_.adress));
            }
        }
        return specification;
    }
}
