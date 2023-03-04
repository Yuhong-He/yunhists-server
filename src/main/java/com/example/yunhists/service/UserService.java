package com.example.yunhists.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    // Create
    int register(User user);

    // Read
    User login(String email, String password);

    int googleRegister(User user);

    User getUserById(Integer id);
    User getUserByEmail(String email);
    List<User> getAll();

    List<User> getAllAdmin();

    // Update
    int updateUsername(Integer id, String username);
    int updateEmail(Integer id, String email);
    int updatePassword(Integer id, String password);
    int updateUserRights(Integer id, Integer userRights);
    int updateLang(Integer id, String lang);

    void updateEmailNotification(Integer id, String status);

    int addPoints(Integer id);
    int updateUserToDeletedUser(Integer id);

    // Delete
    int deleteUserById(Integer id);

}
