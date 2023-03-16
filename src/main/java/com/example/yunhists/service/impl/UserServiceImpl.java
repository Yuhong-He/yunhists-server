package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.User;
import com.example.yunhists.mapper.UserMapper;
import com.example.yunhists.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userServiceImpl")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public void register(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        baseMapper.insert(user);
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
    public void googleRegister(User user) {
        baseMapper.insert(user);
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
    public List<User> getAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<User> getAllAdmin() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_rights", 1);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void updatePassword(Integer id, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = this.getUserById(id);
        System.out.println(user);
        user.setPassword(passwordEncoder.encode(password));
        baseMapper.updateById(user);
    }
}
