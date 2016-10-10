package com.test;

import com.demo.dao.UserDao;
import com.demo.model.User;
import com.demo.service.UserService;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Pinggang Yu on 2016/10/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration({"classpath:conf/spring-config.xml","classpath:struts.xml"})
@Component
public class UserTest {

    @Resource
    private UserService userService;

    @Test
    @Transactional
    public void addUser() {
        User user = new User();
        user.setPassword("testPasswordServiceTest");
        user.setUsername("testUsernameServiceTest");
        userService.addUser(user);
    }
}
