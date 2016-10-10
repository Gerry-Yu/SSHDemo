package com.demo.action;

import com.demo.model.User;
import com.demo.service.UserService;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;


/**
 * Created by Pinggang Yu on 2016/10/9.
 */
@Controller
public class AddUserAction extends ActionSupport {
    private String username;
    private String password;

    @Resource
    private UserService userService;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String execute() throws Exception {
        User user = new User();
        user.setUsername(getUsername());
        user.setPassword(getPassword());
        userService.addUser(user);
        return SUCCESS;
    }
}
