package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.User;
import com.example.yunhists.mapper.UserMapper;
import com.example.yunhists.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userServiceImpl")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public int register(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return baseMapper.insert(user);
    }

    @Override
    public User login(String email, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = this.getUserByEmail(email);
        if(user != null && passwordEncoder.matches(password, user.getPassword())) {
            return this.getUserByEmail(email);
        } else {
            return null;
        }
    }

    @Override
    public User getUserById(Integer id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public int updateUsername(Integer id, String username) {
        User user = this.getUserById(id);
        user.setUsername(username);
        return baseMapper.updateById(user);
    }

    @Override
    public int updatePassword(Integer id, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = this.getUserById(id);
        user.setPassword(passwordEncoder.encode(password));
        return baseMapper.updateById(user);
    }

    @Override
    public int updateUserRights(Integer id, Integer userRights) {
        User user = this.getUserById(id);
        user.setUserRights(userRights);
        return baseMapper.updateById(user);
    }

    @Override
    public int updateLang(Integer id, String lang) {
        User user = this.getUserById(id);
        user.setLang(lang);
        return baseMapper.updateById(user);
    }

    @Override
    public int addPoints(Integer id) {
        User user = this.getUserById(id);
        user.setPoints(user.getPoints() + 1);
        return baseMapper.updateById(user);
    }

    @Override
    public int updateUserToDeletedUser(Integer id) {
        User user = this.getUserById(id);
        user.setUsername("Deleted User");
        user.setEmail("");
        user.setPassword("");
        return baseMapper.updateById(user);
    }

    @Override
    public int deleteUserById(Integer id) {
        return baseMapper.deleteById(id);
    }
}
