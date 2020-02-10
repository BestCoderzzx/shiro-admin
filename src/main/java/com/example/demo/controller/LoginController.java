package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.vo.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by lenovo on 2019/6/16.
 */
@Controller
@Slf4j
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    @ResponseBody
    public ResultData login(){
        log.info("login get ====");
        ResultData resultData = new ResultData();
        resultData.setMessage("用户名或密码错误");
        resultData.setCode(3001);
        return resultData;
//        return "/login";
    }

    /**
     * 登录
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public ResultData postLogin(String username, String password, Model model, HttpSession httpSession){
        ResultData resultData = new ResultData();
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        try{
            subject.login(token);
        }catch (Exception e){
            e.printStackTrace();
//            model.addAttribute("error","用户名或密码错误");
            resultData.setMessage("用户名或密码错误");
            resultData.setCode(3001);
            return resultData;
        }
        User user = new User();
        user.setUserName(username);
        User db_user = userMapper.selectOne(user);
        httpSession.setAttribute("userInfo",db_user);
        db_user.setPasswd("");
        resultData.setCode(200);
        resultData.setResult(db_user);
        return resultData;
    }

    /**
     * 退出登录
     */
    @RequestMapping("/logout")
    @ResponseBody
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "logout success";
    }
}
