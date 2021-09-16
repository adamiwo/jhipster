package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.UsersList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UsersList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UsersListRepository extends JpaRepository<UsersList, Long>, JpaSpecificationExecutor<UsersList> {}
