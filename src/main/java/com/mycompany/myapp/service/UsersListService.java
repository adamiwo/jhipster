package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.UsersList;
import com.mycompany.myapp.repository.UsersListRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UsersList}.
 */
@Service
@Transactional
public class UsersListService {

    private final Logger log = LoggerFactory.getLogger(UsersListService.class);

    private final UsersListRepository usersListRepository;

    public UsersListService(UsersListRepository usersListRepository) {
        this.usersListRepository = usersListRepository;
    }

    /**
     * Save a usersList.
     *
     * @param usersList the entity to save.
     * @return the persisted entity.
     */
    public UsersList save(UsersList usersList) {
        log.debug("Request to save UsersList : {}", usersList);
        return usersListRepository.save(usersList);
    }

    /**
     * Partially update a usersList.
     *
     * @param usersList the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UsersList> partialUpdate(UsersList usersList) {
        log.debug("Request to partially update UsersList : {}", usersList);

        return usersListRepository
            .findById(usersList.getId())
            .map(
                existingUsersList -> {
                    if (usersList.getFirstName() != null) {
                        existingUsersList.setFirstName(usersList.getFirstName());
                    }
                    if (usersList.getLastName() != null) {
                        existingUsersList.setLastName(usersList.getLastName());
                    }
                    if (usersList.getEmail() != null) {
                        existingUsersList.setEmail(usersList.getEmail());
                    }
                    if (usersList.getAdress() != null) {
                        existingUsersList.setAdress(usersList.getAdress());
                    }

                    return existingUsersList;
                }
            )
            .map(usersListRepository::save);
    }

    /**
     * Get all the usersLists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UsersList> findAll(Pageable pageable) {
        log.debug("Request to get all UsersLists");
        return usersListRepository.findAll(pageable);
    }

    /**
     * Get one usersList by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UsersList> findOne(Long id) {
        log.debug("Request to get UsersList : {}", id);
        return usersListRepository.findById(id);
    }

    /**
     * Delete the usersList by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UsersList : {}", id);
        usersListRepository.deleteById(id);
    }
}
