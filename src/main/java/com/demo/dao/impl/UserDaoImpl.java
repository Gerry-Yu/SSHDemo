package com.demo.dao.impl;

import com.demo.dao.UserDao;
import com.demo.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by Pinggang Yu on 2016/10/9.
 */

@Repository("userDao")
public class UserDaoImpl implements UserDao {
    private HibernateTemplate hibernateTemplate;
    @Resource
    public void setSessionFactory (SessionFactory sessionFactory) {
        this.hibernateTemplate =  new HibernateTemplate(sessionFactory);
    }

    public void saveUser(User user) {
        this.hibernateTemplate.save(user);
    }
}
