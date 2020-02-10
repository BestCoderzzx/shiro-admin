package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.vo.ResultData;

public interface RoleService {

    ResultData queryPage(int userId,String rolename, int currentPage, int pageSize);

    int saveRole(Role role);

    void commitGrantResource(int roleId, String resourceIds);
}
