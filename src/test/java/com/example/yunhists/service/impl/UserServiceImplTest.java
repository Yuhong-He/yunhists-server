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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest {
    @Autowired
    UserService userService;

    User testUser = new User("implTest", "implTest", "impl@gmail.com", "zh", 0);

    static int testUserId;

    @Order(1)
    @Test
    public void register_validEmail_success() {
        int result = userService.register(testUser);
        assertEquals(1, result);
    }

    @Order(2)
    @Test
    public void login_passwordMatch_validUser() {
        User user = userService.login(testUser.getEmail(), testUser.getPassword());
        testUserId = user.getId();
        assertNotNull(user);
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Order(3)
    @Test
    public void login_emailNotExist_null() {
        User user = userService.login("xxxx@gmail.com", "xxxx");
        assertNull(user);
    }

    @Order(4)
    @Test
    public void login_passwordNotMatch_null() {
        User user = userService.login(testUser.getEmail(), testUser.getPassword() + "abc");
        assertNull(user);
    }

    @Order(5)
    @Test
    public void getUserById_idExist_validUser() {
        User user = userService.getUserById(testUserId);
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Order(6)
    @Test
    public void getUserById_idNotExist_null() {
        User user = userService.getUserById(114514);
        assertNull(user);
    }

    @Order(7)
    @Test
    public void getUserByEmail_emailExist_validUser() {
        User user = userService.getUserByEmail(testUser.getEmail());
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Order(8)
    @Test
    public void getUserByEmail_emailNotExist_null() {
        User user = userService.getUserByEmail("xxxx@gmail.com");
        assertNull(user);
    }

    @Order(9)
    @Test
    public void updateUsername_userExist_success() {
        int row = userService.updateUsername(testUserId, String.valueOf(UUID.randomUUID()));
        assertEquals(1, row);
        assertNotEquals(testUser.getUsername(), userService.getUserById(testUserId).getUsername());
    }

    @Order(10)
    @Test
    public void updateUsername_userNotExist_fail() {
        assertThrows(NullPointerException.class, () -> userService.updateUsername(114514, String.valueOf(UUID.randomUUID())));
    }

    @Order(11)
    @Test
    public void updatePassword_userExist_success() {
        String testPassword = String.valueOf(UUID.randomUUID());
        int row = userService.updatePassword(testUserId, testPassword);
        assertEquals(1, row);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        assertTrue(passwordEncoder.matches(testPassword, userService.getUserById(testUserId).getPassword()));
    }

    @Order(12)
    @Test
    public void updateUserRights_userExist_success() {
        int row = userService.updateUserRights(testUserId, 1);
        assertEquals(1, row);
        assertNotEquals(0, userService.getUserById(testUserId).getUserRights());
    }

    @Order(13)
    @Test
    public void updateLang_userExist_success() {
        int row = userService.updateLang(testUserId, "en");
        assertEquals(1, row);
        assertNotEquals("zh", userService.getUserById(testUserId).getLang());
    }

    @Order(14)
    @Test
    public void addPoints_userExist_success() {
        int originPoints = userService.getUserById(testUserId).getPoints();
        int row = userService.addPoints(testUserId);
        assertEquals(1, row);
        assertEquals(originPoints + 1, userService.getUserById(testUserId).getPoints());
    }

    @Order(15)
    @Test
    public void updateEmail_userExist_success() {
        int row = userService.updateEmail(testUserId, "test@yunnanhistory.com");
        assertEquals(1, row);
        assertEquals("test@yunnanhistory.com", userService.getUserById(testUserId).getEmail());
    }

    @Order(16)
    @Test
    public void updateUserToDeletedUser_userExist_success() {
        int row = userService.updateUserToDeletedUser(testUserId);
        assertEquals(1, row);
    }

    @Order(17)
    @Test
    public void deleteUserById_userExist_success() {
        int row = userService.deleteUserById(testUserId);
        assertEquals(1, row);
    }
}
