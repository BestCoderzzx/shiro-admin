package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import com.example.demo.vo.ResourceVo;
import com.example.demo.vo.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户信息
     * @return
     */
    @RequestMapping("/queryList")
    @ResponseBody
    public ResultData queryList(String rolename, int currentPage, int pageSize, HttpSession httpSession){
        User user = (User) httpSession.getAttribute("userInfo");
        ResultData resultData =  roleService.queryPage(user.getId(),rolename,currentPage,pageSize);
        return resultData;
    }

    /**
     * 新增角色
     * @param role
     * @return
     */
    @RequestMapping("/saveRole")
    @ResponseBody
    public ResultData saveRole(Role role, HttpSession httpSession){
        ResultData resultData = new ResultData();
        User user = (User) httpSession.getAttribute("userInfo");
        role.setCreateId(user.getId());
        int num = roleService.saveRole(role);
        resultData.setCode(num>0?200:500);
        return resultData;
    }

    @RequestMapping("/queryResourceTree")
    @ResponseBody
    public ResultData queryResourceTree(HttpSession httpSession,Integer roleId){
        User user = (User) httpSession.getAttribute("userInfo");
        ResultData resultData = new ResultData();
        Map<String, Object> stringObjectMap = userService.queryResourceTree(user.getUserName(), roleId);
        resultData.setResult(stringObjectMap);
        resultData.setCode(200);
        return resultData;
    }

    /**
     * 提交授权菜单
     * @param roleId
     * @param resourceIds
     * @return
     */
    @RequestMapping("/commitGrantResource")
    @ResponseBody
    public ResultData commitGrantResource(int roleId,String resourceIds){
        ResultData resultData = new ResultData();
        roleService.commitGrantResource(roleId,resourceIds);
        resultData.setCode(200);
        return resultData;
    }

}
