package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.vo.*;

import java.util.List;
import java.util.Map;

public interface UserService {

    ResultData queryPage(String username, int currentPage, int pageSize,Integer userId);

    int saveUser(User user);

    Map<String,Object> queryResourceTree(String userName, Integer roleId);

    List<UserRoleVo> queryRoleList(Integer userId, Integer createId);

    void saveUserRoles(Integer userId, String roleIds);

    /**
     * 查询拥有的权限
     * @param userName
     * @return
     */
    List<RoleResourceVo> queryMyPermissions(String userName);
}
