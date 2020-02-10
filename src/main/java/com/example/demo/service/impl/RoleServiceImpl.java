package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.entity.RoleResource;
import com.example.demo.entity.User;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.RoleResourceMapper;
import com.example.demo.service.RoleService;
import com.example.demo.vo.PageResult;
import com.example.demo.vo.ResultData;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleResourceMapper roleResourceMapper;

    @Override
    public ResultData queryPage(int userId,String rolename, int currentPage, int pageSize) {
        ResultData resultData = new ResultData();
        PageHelper.startPage(currentPage,pageSize);
        Example example = new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNoneBlank(rolename)){
            criteria.andEqualTo("roleName",rolename);
        }
        criteria.andEqualTo("createId",userId);
        example.setOrderByClause("id desc");
        List<Role> roles = roleMapper.selectByExample(example);
        PageInfo<Role> userList = new PageInfo<>(roles);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(userList.getTotal());
        pageResult.setTotalPage(userList.getPages());
        pageResult.setItems(userList.getList());
        resultData.setCode(200);
        resultData.setResult(pageResult);
        return resultData;
    }

    @Override
    public int saveRole(Role role) {
        role.setCreateDate(new Date());
        return roleMapper.insert(role);
    }

    @Override
    public void commitGrantResource(int roleId, String resourceIds) {
        RoleResource roleResource =  new RoleResource();
        roleResource.setRoleId(roleId);
        roleResourceMapper.delete(roleResource);

        List<RoleResource> roleResources = new ArrayList<>();
        String[] resourceIdList = resourceIds.split(",");
        for(String resourceId:resourceIdList){
            RoleResource r_resource = new RoleResource();
            r_resource.setRoleId(roleId);
            r_resource.setResourceId(Integer.parseInt(resourceId));
            roleResources.add(r_resource);
        }
        roleResourceMapper.insertList(roleResources);
    }
}
