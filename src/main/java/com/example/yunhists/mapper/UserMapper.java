package com.example.yunhists.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yunhists.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
