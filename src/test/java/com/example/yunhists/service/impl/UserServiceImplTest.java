package com.example.yunhists.service.impl;

import com.example.yunhists.entity.User;
import com.example.yunhists.mapper.UserMapper;
import com.example.yunhists.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {
    @Autowired
    UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService2;

    @Test
    public void register() {
        MockitoAnnotations.openMocks(this);
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("abc");
        userService2.register(user);
        verify(userMapper, times(1)).insert(user);
    }

    @Test
    public void login() {
        assertNull(userService.login("abc", "abc"));
    }

    @Test
    public void googleRegister() {
        MockitoAnnotations.openMocks(this);
        User user = new User();
        user.setEmail("test@google.com");
        userService2.googleRegister(user);
        verify(userMapper, times(1)).insert(user);
    }

    @Test
    public void getUserById() {
        assertNull(userService.getUserById(-1));
    }

    @Test
    public void getUserByEmail() {
        assertNull(userService.getUserByEmail("abc"));
    }

    @Test
    public void getAll() {
        assertTrue(userService.getAll().size() > 0);
    }

    @Test
    public void getAllAdmin() {
        assertTrue(userService.getAllAdmin().size() > 0);
    }
}
