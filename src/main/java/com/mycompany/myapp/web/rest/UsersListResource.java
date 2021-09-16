package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.UsersList;
import com.mycompany.myapp.repository.UsersListRepository;
import com.mycompany.myapp.service.UsersListQueryService;
import com.mycompany.myapp.service.UsersListService;
import com.mycompany.myapp.service.criteria.UsersListCriteria;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.UsersList}.
 */
@RestController
@RequestMapping("/api")
public class UsersListResource {

    private final Logger log = LoggerFactory.getLogger(UsersListResource.class);

    private static final String ENTITY_NAME = "usersList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UsersListService usersListService;

    private final UsersListRepository usersListRepository;

    private final UsersListQueryService usersListQueryService;

    public UsersListResource(
        UsersListService usersListService,
        UsersListRepository usersListRepository,
        UsersListQueryService usersListQueryService
    ) {
        this.usersListService = usersListService;
        this.usersListRepository = usersListRepository;
        this.usersListQueryService = usersListQueryService;
    }

    /**
     * {@code POST  /users-lists} : Create a new usersList.
     *
     * @param usersList the usersList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new usersList, or with status {@code 400 (Bad Request)} if the usersList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/users-lists")
    public ResponseEntity<UsersList> createUsersList(@RequestBody UsersList usersList) throws URISyntaxException {
        log.debug("REST request to save UsersList : {}", usersList);
        if (usersList.getId() != null) {
            throw new BadRequestAlertException("A new usersList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UsersList result = usersListService.save(usersList);
        return ResponseEntity
            .created(new URI("/api/users-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /users-lists/:id} : Updates an existing usersList.
     *
     * @param id the id of the usersList to save.
     * @param usersList the usersList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated usersList,
     * or with status {@code 400 (Bad Request)} if the usersList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the usersList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/users-lists/{id}")
    public ResponseEntity<UsersList> updateUsersList(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UsersList usersList
    ) throws URISyntaxException {
        log.debug("REST request to update UsersList : {}, {}", id, usersList);
        if (usersList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, usersList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!usersListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UsersList result = usersListService.save(usersList);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, usersList.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /users-lists/:id} : Partial updates given fields of an existing usersList, field will ignore if it is null
     *
     * @param id the id of the usersList to save.
     * @param usersList the usersList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated usersList,
     * or with status {@code 400 (Bad Request)} if the usersList is not valid,
     * or with status {@code 404 (Not Found)} if the usersList is not found,
     * or with status {@code 500 (Internal Server Error)} if the usersList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/users-lists/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<UsersList> partialUpdateUsersList(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UsersList usersList
    ) throws URISyntaxException {
        log.debug("REST request to partial update UsersList partially : {}, {}", id, usersList);
        if (usersList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, usersList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!usersListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UsersList> result = usersListService.partialUpdate(usersList);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, usersList.getId().toString())
        );
    }

    /**
     * {@code GET  /users-lists} : get all the usersLists.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of usersLists in body.
     */
    @GetMapping("/users-lists")
    public ResponseEntity<List<UsersList>> getAllUsersLists(UsersListCriteria criteria, Pageable pageable) {
        log.debug("REST request to get UsersLists by criteria: {}", criteria);
        Page<UsersList> page = usersListQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /users-lists/count} : count all the usersLists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/users-lists/count")
    public ResponseEntity<Long> countUsersLists(UsersListCriteria criteria) {
        log.debug("REST request to count UsersLists by criteria: {}", criteria);
        return ResponseEntity.ok().body(usersListQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /users-lists/:id} : get the "id" usersList.
     *
     * @param id the id of the usersList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the usersList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users-lists/{id}")
    public ResponseEntity<UsersList> getUsersList(@PathVariable Long id) {
        log.debug("REST request to get UsersList : {}", id);
        Optional<UsersList> usersList = usersListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(usersList);
    }

    /**
     * {@code DELETE  /users-lists/:id} : delete the "id" usersList.
     *
     * @param id the id of the usersList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users-lists/{id}")
    public ResponseEntity<Void> deleteUsersList(@PathVariable Long id) {
        log.debug("REST request to delete UsersList : {}", id);
        usersListService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
