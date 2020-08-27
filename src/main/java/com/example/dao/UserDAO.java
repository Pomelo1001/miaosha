package com.example.dao;

import com.example.entity.User;

/**
 * @version 1.1.0
 * @author：caopu
 * @time：2020-8-27
 * @Description: todo
 */
public interface UserDAO {
    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    User findById(Integer id);
}
