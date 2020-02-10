package com.example.demo.controller;

import com.example.demo.config.MyRealm;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2019/6/16.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/userAdd")
    public String userAdd(){

        return "/user/userAdd";
    }

    @RequestMapping("/userDel")
    public String userDel(){
        return "/user/userDel";
    }

    @Autowired
    private MyRealm myRealm;

    @RequestMapping("/refresh")
    public void refresh()throws Exception{
//        MyRealm.reloadAuthorizing(myRealm,"admin");
    }

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/queryUserInfo")
    @ResponseBody
    public User queryUserInfo(){
        User u = new User();
        u.setId(1);
        User user = userMapper.selectOne(u);
        return user;
    }

    /**
     * 分页查询用户信息
     * @return
     */
    @RequestMapping("/queryList")
    @ResponseBody
    public ResultData queryList(String username, int currentPage, int pageSize, HttpSession httpSession){
        User user = (User) httpSession.getAttribute("userInfo");
        ResultData resultData =  userService.queryPage(username,currentPage,pageSize,user.getId());
        return resultData;
    }

    /**
     * 新增用户
     * @param user
     * @return
     */
    @RequestMapping("/saveUser")
    @ResponseBody
    public ResultData saveUser(User user,HttpSession httpSession){
        User login_user = (User) httpSession.getAttribute("userInfo");
        ResultData resultData = new ResultData();
        user.setCreateId(login_user.getId());
        int num = userService.saveUser(user);
        resultData.setCode(num>0?200:500);
        return resultData;
    }

    @RequestMapping("/queryRoleList")
    @ResponseBody
    public ResultData queryRoleList(Integer userId, HttpSession httpSession){
        ResultData resultData = new ResultData();
        User user = (User) httpSession.getAttribute("userInfo");
        List<UserRoleVo> userRoleVos = userService.queryRoleList(userId,user.getId());
        resultData.setCode(200);
        resultData.setResult(userRoleVos);
        return resultData;
    }

    @RequestMapping("/saveUserRoleIds")
    @ResponseBody
    public ResultData saveUserRoleIds(Integer userId,String roleIds){
        ResultData resultData = new ResultData();
        userService.saveUserRoles(userId,roleIds);
        resultData.setCode(200);
        return resultData;
    }

    /**
     * 查询我的权限列表
     * @param httpSession
     * @return
     */
    @RequestMapping("/queryMyPermission")
    @ResponseBody
    public ResultData queryMyPermission(HttpSession httpSession){
        ResultData resultData = new ResultData();
        List<String> permissionList = new ArrayList<>();
        User user = (User) httpSession.getAttribute("userInfo");
        List<RoleResourceVo> roleResourceVos = userService.queryMyPermissions(user.getUserName());
        if(roleResourceVos!=null&&roleResourceVos.size()>0){
            for(RoleResourceVo roleResourceVo:roleResourceVos){
                if(!StringUtils.isEmpty(roleResourceVo.getFlagName())){
                    permissionList.add(roleResourceVo.getFlagName());
                }
            }
        }
        resultData.setCode(200);
        resultData.setResult(permissionList);
        return resultData;
    }
}
