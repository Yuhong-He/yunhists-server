package com.example.yunhists.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    // Create
    void register(User user);

    // Read
    User login(String email, String password);
    void googleRegister(User user);
    User getUserById(Integer id);
    User getUserByEmail(String email);
    List<User> getAll();
    List<User> getAllAdmin();

    // Update
    void updatePassword(Integer id, String password);

}
