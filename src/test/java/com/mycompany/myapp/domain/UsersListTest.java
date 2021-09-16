package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UsersListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UsersList.class);
        UsersList usersList1 = new UsersList();
        usersList1.setId(1L);
        UsersList usersList2 = new UsersList();
        usersList2.setId(usersList1.getId());
        assertThat(usersList1).isEqualTo(usersList2);
        usersList2.setId(2L);
        assertThat(usersList1).isNotEqualTo(usersList2);
        usersList1.setId(null);
        assertThat(usersList1).isNotEqualTo(usersList2);
    }
}
