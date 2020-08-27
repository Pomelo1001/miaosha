package com.example.service;

import com.example.entity.User;

/**
 * @version 1.1.0
 * @author：caopu
 * @time：2020-8-27
 * @Description: todo
 */
public interface UserService {
    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    User findById(Integer userId);
}
