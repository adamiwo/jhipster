package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.UsersList;
import com.mycompany.myapp.repository.UsersListRepository;
import com.mycompany.myapp.service.criteria.UsersListCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UsersListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UsersListResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/users-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UsersListRepository usersListRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUsersListMockMvc;

    private UsersList usersList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UsersList createEntity(EntityManager em) {
        UsersList usersList = new UsersList()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .adress(DEFAULT_ADRESS);
        return usersList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UsersList createUpdatedEntity(EntityManager em) {
        UsersList usersList = new UsersList()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .adress(UPDATED_ADRESS);
        return usersList;
    }

    @BeforeEach
    public void initTest() {
        usersList = createEntity(em);
    }

    @Test
    @Transactional
    void createUsersList() throws Exception {
        int databaseSizeBeforeCreate = usersListRepository.findAll().size();
        // Create the UsersList
        restUsersListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(usersList)))
            .andExpect(status().isCreated());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeCreate + 1);
        UsersList testUsersList = usersListList.get(usersListList.size() - 1);
        assertThat(testUsersList.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUsersList.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testUsersList.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUsersList.getAdress()).isEqualTo(DEFAULT_ADRESS);
    }

    @Test
    @Transactional
    void createUsersListWithExistingId() throws Exception {
        // Create the UsersList with an existing ID
        usersList.setId(1L);

        int databaseSizeBeforeCreate = usersListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUsersListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(usersList)))
            .andExpect(status().isBadRequest());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUsersLists() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList
        restUsersListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(usersList.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].adress").value(hasItem(DEFAULT_ADRESS)));
    }

    @Test
    @Transactional
    void getUsersList() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get the usersList
        restUsersListMockMvc
            .perform(get(ENTITY_API_URL_ID, usersList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(usersList.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.adress").value(DEFAULT_ADRESS));
    }

    @Test
    @Transactional
    void getUsersListsByIdFiltering() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        Long id = usersList.getId();

        defaultUsersListShouldBeFound("id.equals=" + id);
        defaultUsersListShouldNotBeFound("id.notEquals=" + id);

        defaultUsersListShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUsersListShouldNotBeFound("id.greaterThan=" + id);

        defaultUsersListShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUsersListShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUsersListsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where firstName equals to DEFAULT_FIRST_NAME
        defaultUsersListShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the usersListList where firstName equals to UPDATED_FIRST_NAME
        defaultUsersListShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByFirstNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where firstName not equals to DEFAULT_FIRST_NAME
        defaultUsersListShouldNotBeFound("firstName.notEquals=" + DEFAULT_FIRST_NAME);

        // Get all the usersListList where firstName not equals to UPDATED_FIRST_NAME
        defaultUsersListShouldBeFound("firstName.notEquals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultUsersListShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the usersListList where firstName equals to UPDATED_FIRST_NAME
        defaultUsersListShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where firstName is not null
        defaultUsersListShouldBeFound("firstName.specified=true");

        // Get all the usersListList where firstName is null
        defaultUsersListShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllUsersListsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where firstName contains DEFAULT_FIRST_NAME
        defaultUsersListShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the usersListList where firstName contains UPDATED_FIRST_NAME
        defaultUsersListShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where firstName does not contain DEFAULT_FIRST_NAME
        defaultUsersListShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the usersListList where firstName does not contain UPDATED_FIRST_NAME
        defaultUsersListShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where lastName equals to DEFAULT_LAST_NAME
        defaultUsersListShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the usersListList where lastName equals to UPDATED_LAST_NAME
        defaultUsersListShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByLastNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where lastName not equals to DEFAULT_LAST_NAME
        defaultUsersListShouldNotBeFound("lastName.notEquals=" + DEFAULT_LAST_NAME);

        // Get all the usersListList where lastName not equals to UPDATED_LAST_NAME
        defaultUsersListShouldBeFound("lastName.notEquals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultUsersListShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the usersListList where lastName equals to UPDATED_LAST_NAME
        defaultUsersListShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where lastName is not null
        defaultUsersListShouldBeFound("lastName.specified=true");

        // Get all the usersListList where lastName is null
        defaultUsersListShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllUsersListsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where lastName contains DEFAULT_LAST_NAME
        defaultUsersListShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the usersListList where lastName contains UPDATED_LAST_NAME
        defaultUsersListShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where lastName does not contain DEFAULT_LAST_NAME
        defaultUsersListShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the usersListList where lastName does not contain UPDATED_LAST_NAME
        defaultUsersListShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllUsersListsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where email equals to DEFAULT_EMAIL
        defaultUsersListShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the usersListList where email equals to UPDATED_EMAIL
        defaultUsersListShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUsersListsByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where email not equals to DEFAULT_EMAIL
        defaultUsersListShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the usersListList where email not equals to UPDATED_EMAIL
        defaultUsersListShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUsersListsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultUsersListShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the usersListList where email equals to UPDATED_EMAIL
        defaultUsersListShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUsersListsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where email is not null
        defaultUsersListShouldBeFound("email.specified=true");

        // Get all the usersListList where email is null
        defaultUsersListShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllUsersListsByEmailContainsSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where email contains DEFAULT_EMAIL
        defaultUsersListShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the usersListList where email contains UPDATED_EMAIL
        defaultUsersListShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUsersListsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where email does not contain DEFAULT_EMAIL
        defaultUsersListShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the usersListList where email does not contain UPDATED_EMAIL
        defaultUsersListShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUsersListsByAdressIsEqualToSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where adress equals to DEFAULT_ADRESS
        defaultUsersListShouldBeFound("adress.equals=" + DEFAULT_ADRESS);

        // Get all the usersListList where adress equals to UPDATED_ADRESS
        defaultUsersListShouldNotBeFound("adress.equals=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllUsersListsByAdressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where adress not equals to DEFAULT_ADRESS
        defaultUsersListShouldNotBeFound("adress.notEquals=" + DEFAULT_ADRESS);

        // Get all the usersListList where adress not equals to UPDATED_ADRESS
        defaultUsersListShouldBeFound("adress.notEquals=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllUsersListsByAdressIsInShouldWork() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where adress in DEFAULT_ADRESS or UPDATED_ADRESS
        defaultUsersListShouldBeFound("adress.in=" + DEFAULT_ADRESS + "," + UPDATED_ADRESS);

        // Get all the usersListList where adress equals to UPDATED_ADRESS
        defaultUsersListShouldNotBeFound("adress.in=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllUsersListsByAdressIsNullOrNotNull() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where adress is not null
        defaultUsersListShouldBeFound("adress.specified=true");

        // Get all the usersListList where adress is null
        defaultUsersListShouldNotBeFound("adress.specified=false");
    }

    @Test
    @Transactional
    void getAllUsersListsByAdressContainsSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where adress contains DEFAULT_ADRESS
        defaultUsersListShouldBeFound("adress.contains=" + DEFAULT_ADRESS);

        // Get all the usersListList where adress contains UPDATED_ADRESS
        defaultUsersListShouldNotBeFound("adress.contains=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllUsersListsByAdressNotContainsSomething() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        // Get all the usersListList where adress does not contain DEFAULT_ADRESS
        defaultUsersListShouldNotBeFound("adress.doesNotContain=" + DEFAULT_ADRESS);

        // Get all the usersListList where adress does not contain UPDATED_ADRESS
        defaultUsersListShouldBeFound("adress.doesNotContain=" + UPDATED_ADRESS);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUsersListShouldBeFound(String filter) throws Exception {
        restUsersListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(usersList.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].adress").value(hasItem(DEFAULT_ADRESS)));

        // Check, that the count call also returns 1
        restUsersListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUsersListShouldNotBeFound(String filter) throws Exception {
        restUsersListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUsersListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUsersList() throws Exception {
        // Get the usersList
        restUsersListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUsersList() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        int databaseSizeBeforeUpdate = usersListRepository.findAll().size();

        // Update the usersList
        UsersList updatedUsersList = usersListRepository.findById(usersList.getId()).get();
        // Disconnect from session so that the updates on updatedUsersList are not directly saved in db
        em.detach(updatedUsersList);
        updatedUsersList.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).adress(UPDATED_ADRESS);

        restUsersListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUsersList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUsersList))
            )
            .andExpect(status().isOk());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeUpdate);
        UsersList testUsersList = usersListList.get(usersListList.size() - 1);
        assertThat(testUsersList.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUsersList.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUsersList.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUsersList.getAdress()).isEqualTo(UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void putNonExistingUsersList() throws Exception {
        int databaseSizeBeforeUpdate = usersListRepository.findAll().size();
        usersList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUsersListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, usersList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(usersList))
            )
            .andExpect(status().isBadRequest());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUsersList() throws Exception {
        int databaseSizeBeforeUpdate = usersListRepository.findAll().size();
        usersList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUsersListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(usersList))
            )
            .andExpect(status().isBadRequest());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUsersList() throws Exception {
        int databaseSizeBeforeUpdate = usersListRepository.findAll().size();
        usersList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUsersListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(usersList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUsersListWithPatch() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        int databaseSizeBeforeUpdate = usersListRepository.findAll().size();

        // Update the usersList using partial update
        UsersList partialUpdatedUsersList = new UsersList();
        partialUpdatedUsersList.setId(usersList.getId());

        partialUpdatedUsersList.lastName(UPDATED_LAST_NAME).adress(UPDATED_ADRESS);

        restUsersListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUsersList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUsersList))
            )
            .andExpect(status().isOk());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeUpdate);
        UsersList testUsersList = usersListList.get(usersListList.size() - 1);
        assertThat(testUsersList.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUsersList.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUsersList.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUsersList.getAdress()).isEqualTo(UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void fullUpdateUsersListWithPatch() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        int databaseSizeBeforeUpdate = usersListRepository.findAll().size();

        // Update the usersList using partial update
        UsersList partialUpdatedUsersList = new UsersList();
        partialUpdatedUsersList.setId(usersList.getId());

        partialUpdatedUsersList.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).adress(UPDATED_ADRESS);

        restUsersListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUsersList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUsersList))
            )
            .andExpect(status().isOk());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeUpdate);
        UsersList testUsersList = usersListList.get(usersListList.size() - 1);
        assertThat(testUsersList.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUsersList.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUsersList.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUsersList.getAdress()).isEqualTo(UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void patchNonExistingUsersList() throws Exception {
        int databaseSizeBeforeUpdate = usersListRepository.findAll().size();
        usersList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUsersListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, usersList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(usersList))
            )
            .andExpect(status().isBadRequest());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUsersList() throws Exception {
        int databaseSizeBeforeUpdate = usersListRepository.findAll().size();
        usersList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUsersListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(usersList))
            )
            .andExpect(status().isBadRequest());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUsersList() throws Exception {
        int databaseSizeBeforeUpdate = usersListRepository.findAll().size();
        usersList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUsersListMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(usersList))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UsersList in the database
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUsersList() throws Exception {
        // Initialize the database
        usersListRepository.saveAndFlush(usersList);

        int databaseSizeBeforeDelete = usersListRepository.findAll().size();

        // Delete the usersList
        restUsersListMockMvc
            .perform(delete(ENTITY_API_URL_ID, usersList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UsersList> usersListList = usersListRepository.findAll();
        assertThat(usersListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
