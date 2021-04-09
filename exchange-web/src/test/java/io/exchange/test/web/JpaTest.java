package io.exchange.test.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.exchange.core.service.DefaultDataService;
import io.exchange.domain.hibernate.user.User;

//@RunWith(SpringRunner.class)
//@DataJpaTest
//@ActiveProfiles({"dev", "prd"})
public class JpaTest {

//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private DefaultDataService defaultDataService;
//
//    @Before
//    public void init() {
////        this.defaultDataService.createDefaultAdminAndWallet();
//    }
//
//    @Test
//    public void whenFindByName_thenReturnEmployee() {
//        // given
//        User user = User.builder().email("test@email.com").build();
//
//        entityManager.persist(user);
//        entityManager.flush();
//
//        // when
//        User existUser = defaultDataService.userService.get(user.getEmail());
//
//        // then
//        assertThat(existUser.getEmail()).isEqualTo(user.getName());
//    }
}
