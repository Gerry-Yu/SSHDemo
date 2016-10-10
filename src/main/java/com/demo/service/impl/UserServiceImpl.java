package com.demo.service.impl;

import com.demo.dao.UserDao;
import com.demo.model.User;
import com.demo.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Pinggang Yu on 2016/10/9.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;


    public void addUser(User user) {
        userDao.saveUser(user);
    }
}
