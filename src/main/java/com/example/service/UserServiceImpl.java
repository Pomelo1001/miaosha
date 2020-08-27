package com.example.service;

import com.example.dao.UserDAO;
import com.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @version 1.1.0
 * @author：caopu
 * @time：2020-8-27
 * @Description: todo
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserDAO userDAO;
    @Override
    public User findById(Integer userId) {
        User user = userDAO.findById(userId);
        return user;
    }
}
