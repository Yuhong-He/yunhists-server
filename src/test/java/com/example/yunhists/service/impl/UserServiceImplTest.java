package com.example.yunhists.service.impl;

import com.example.yunhists.entity.User;
import com.example.yunhists.service.UserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest {
    @Autowired
    UserService userService;

    User testUser = new User("test", "test", "test@gmail.com", 0);

    @Order(1)
    @Test
    public void register_validEmail_success() {
        int id = userService.register(testUser);
        assertEquals(1, id);
    }

    @Order(2)
    @Test
    public void register_emailAlreadyUsed_fail() {
        DuplicateKeyException thrown = assertThrows(DuplicateKeyException.class, () -> userService.register(testUser));
        assertTrue(Objects.requireNonNull(thrown.getMessage()).contains("Cause: java.sql.SQLIntegrityConstraintViolationException"));
    }

    @Order(3)
    @Test
    public void login_passwordMatch_validUser() {
        User user = userService.login(testUser.getEmail(), testUser.getPassword());
        assertNotNull(user);
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Order(4)
    @Test
    public void login_emailNotExist_null() {
        User user = userService.login("xxxx@gmail.com", "xxxx");
        assertNull(user);
    }

    @Order(5)
    @Test
    public void login_passwordNotMatch_null() {
        User user = userService.login(testUser.getEmail(), testUser.getPassword() + "abc");
        assertNull(user);
    }

    @Order(6)
    @Test
    public void getUserById_idExist_validUser() {
        User user = userService.getUserById(userService.getUserByEmail(testUser.getEmail()).getId());
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Order(7)
    @Test
    public void getUserById_idNotExist_null() {
        User user = userService.getUserById(114514);
        assertNull(user);
    }

    @Order(8)
    @Test
    public void getUserByEmail_emailExist_validUser() {
        User user = userService.getUserByEmail(testUser.getEmail());
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Order(9)
    @Test
    public void getUserByEmail_emailNotExist_null() {
        User user = userService.getUserByEmail("xxxx@gmail.com");
        assertNull(user);
    }

    @Order(10)
    @Test
    public void updateUsername_userExist_success() {
        int row = userService.updateUsername(userService.getUserByEmail(testUser.getEmail()).getId(), String.valueOf(UUID.randomUUID()));
        assertEquals(1, row);
        assertNotEquals(testUser.getUsername(), userService.getUserByEmail(testUser.getEmail()).getUsername());
    }

    @Order(11)
    @Test
    public void updateUsername_userNotExist_fail() {
        assertThrows(NullPointerException.class, () -> userService.updateUsername(114514, String.valueOf(UUID.randomUUID())));
    }

    @Order(12)
    @Test
    public void updatePassword_userExist_success() {
        String testPassword = String.valueOf(UUID.randomUUID());
        int row = userService.updatePassword(userService.getUserByEmail(testUser.getEmail()).getId(), testPassword);
        assertEquals(1, row);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        assertTrue(passwordEncoder.matches(testPassword, userService.getUserByEmail(testUser.getEmail()).getPassword()));
    }

    @Order(13)
    @Test
    public void updateUserRights_userExist_success() {
        int row = userService.updateUserRights(userService.getUserByEmail(testUser.getEmail()).getId(), 1);
        assertEquals(1, row);
        assertNotEquals(0, userService.getUserByEmail(testUser.getEmail()).getUserRights());
    }

    @Order(14)
    @Test
    public void updateLang_userExist_success() {
        int row = userService.updateLang(userService.getUserByEmail(testUser.getEmail()).getId(), 1);
        assertEquals(1, row);
        assertNotEquals(0, userService.getUserByEmail(testUser.getEmail()).getLang());
    }

    @Order(15)
    @Test
    public void addPoints_userExist_success() {
        int originPoints = userService.getUserByEmail(testUser.getEmail()).getPoints();
        int row = userService.addPoints(userService.getUserByEmail(testUser.getEmail()).getId());
        assertEquals(1, row);
        assertEquals(originPoints + 1, userService.getUserByEmail(testUser.getEmail()).getPoints());
    }

    @Order(16)
    @Test
    public void deleteUserById_userExist_success() {
        int row = userService.deleteUserById(userService.getUserByEmail(testUser.getEmail()).getId());
        assertEquals(1, row);
    }
}
