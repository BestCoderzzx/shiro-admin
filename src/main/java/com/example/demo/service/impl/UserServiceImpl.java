package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.entity.UserRole;
import com.example.demo.mapper.ResourceMapper;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.UserRoleMapper;
import com.example.demo.service.UserService;
import com.example.demo.utils.SpringUtil;
import com.example.demo.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.mapper.UserMapper;
import com.example.demo.entity.User;
import tk.mybatis.mapper.entity.Example;

import java.util.*;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ResultData queryPage(String username, int currentPage, int pageSize,Integer userId) {
        ResultData resultData = new ResultData();
        PageHelper.startPage(currentPage,pageSize);
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNoneBlank(username)){
            criteria.andEqualTo("userName",username);
        }
        criteria.andEqualTo("createId",userId);
        example.setOrderByClause("id desc");
        List<User> users = userMapper.selectByExample(example);
        PageInfo<User> userList = new PageInfo<>(users);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(userList.getTotal());
        pageResult.setTotalPage(userList.getPages());
        pageResult.setItems(userList.getList());
        resultData.setCode(200);
        resultData.setResult(pageResult);
        return resultData;
    }

    @Override
    public int saveUser(User user) {
        user.setCreateDate(new Date());
        return userMapper.insert(user);
    }

    @Override
    public Map<String,Object> queryResourceTree(String userName,Integer roleId) {
        Map<String,Object> resultMap = new HashMap<>();
        List<RoleResourceVo> roleResourceVos = new ArrayList<>();
        HashMap<Integer,RoleResourceVo> myResourceMap = new HashMap<>();//所有的权限菜单
        HashMap<Integer,RoleResourceVo> thisRoleResourceMap = new HashMap<>();//roleId 所拥有的权限菜单
        if(userName.equals("admin")){
            //超级管路员 查询所有权限
            roleResourceVos = resourceMapper.queryUserResources();
        }else{
            List<UserRoleVo> userRoleVos = userMapper.queryUserRolesByUserName(userName);
            if(userRoleVos!=null){
                StringBuffer roleIds = new StringBuffer();
                userRoleVos.stream().forEach(roles ->{roleIds.append(roles.getRoleId()+",");});
                if(!org.springframework.util.StringUtils.isEmpty(roleIds)){
                    roleResourceVos = resourceMapper.queryUserResourcesByRoleIds(roleIds.toString().substring(0,roleIds.lastIndexOf(",")));
                }
            }
        }
        if(roleResourceVos!=null&&roleResourceVos.size()>0){
            for(RoleResourceVo roleResourceVo:roleResourceVos){
                myResourceMap.put(roleResourceVo.getResourceId(),roleResourceVo);
            }
        }

        //查询roleId 这个角色已经授权过的菜单
        roleResourceVos = resourceMapper.queryUserResourcesByRoleIds(roleId+"");
        if(roleResourceVos!=null&&roleResourceVos.size()>0){
            for(RoleResourceVo roleResourceVo:roleResourceVos){
                thisRoleResourceMap.put(roleResourceVo.getResourceId(),roleResourceVo);
            }
        }
        List<Integer> resourceCheckIds = new ArrayList<>();
        //递归组装Vue_element需要的数据结构
        List<ResourceVo> resourceVos = dg_queryUserTree(0, myResourceMap, thisRoleResourceMap, resourceCheckIds);
        resultMap.put("ids",resourceCheckIds);
        resultMap.put("list",resourceVos);
        return resultMap;
    }

    public List<ResourceVo> dg_queryUserTree(int pid,HashMap<Integer,RoleResourceVo> myResourceMap
            ,HashMap<Integer,RoleResourceVo> thisRoleResourceMap,List<Integer> resourceCheckIds){

        List<ResourceVo> resourceVos = new ArrayList<>();
        Iterator<Map.Entry<Integer, RoleResourceVo>> iterator = myResourceMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer, RoleResourceVo> next = iterator.next();
            Integer id = next.getKey();
            RoleResourceVo roleResourceVo = next.getValue();
            if(roleResourceVo.getPid().compareTo(pid)==0){
                ResourceVo resourceVo = new ResourceVo();
                resourceVo.setPid(id);
                if(thisRoleResourceMap!=null&&thisRoleResourceMap.get(id)!=null&&roleResourceVo.getLevel()>=3){
                    //说明有这个菜单权限
                    resourceCheckIds.add(id);
                }
                resourceVo.setId(roleResourceVo.getResourceId());
                resourceVo.setLabel(roleResourceVo.getResourceName());
                resourceVo.setChildren(dg_queryUserTree(id,myResourceMap,thisRoleResourceMap,resourceCheckIds));
                resourceVos.add(resourceVo);
            }
        }
        return resourceVos;
    }

    @Override
    public List<UserRoleVo> queryRoleList(Integer userId, Integer createId) {
        //查询这个用户所有的角色ID
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        List<UserRole> myUserRoles = userRoleMapper.select(userRole);
        Map<Integer,Integer> userCheckRoleIdMap = new HashMap<>();
        if(myUserRoles!=null&&myUserRoles.size()>0){
            for(UserRole userRole1:myUserRoles){
                userCheckRoleIdMap.put(userRole1.getRoleId(),userRole1.getRoleId());
            }
        }
        //查询所有的角色信息
        Role role = new Role();
        role.setCreateId(createId);
        List<Role> roleList = roleMapper.select(role);
        List<UserRoleVo> userRoleVos = new ArrayList<>();
        if(roleList!=null&&roleList.size()>0){
            for(Role r:roleList){
                UserRoleVo userRoleVo = new UserRoleVo();
                if(!userCheckRoleIdMap.isEmpty()&&userCheckRoleIdMap.get(r.getId())!=null){
                    userRoleVo.setCheck(true);
                }
                userRoleVo.setRoleId(r.getId());
                userRoleVo.setRoleName(r.getRoleName());
                userRoleVos.add(userRoleVo);
            }
        }

        return userRoleVos;
    }

    @Override
    public void saveUserRoles(Integer userId, String roleIds) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRoleMapper.delete(userRole);
        String[] roleArr = roleIds.split(",");
        List<UserRole> userRoles = new ArrayList<>();

        if(roleArr.length>0){
            for(String roleId:roleArr){
                UserRole userRole1 = new UserRole();
                userRole1.setUserId(userId);
                userRole1.setRoleId(Integer.parseInt(roleId));
                userRoles.add(userRole1);
            }
        }
        userRoleMapper.insertList(userRoles);
    }

    @Override
    public List<RoleResourceVo> queryMyPermissions(String userName) {
        List<RoleResourceVo> roleResourceVos = new ArrayList<>();
        //根据用户名查询用户拥有的权限
        if(userName.equals("admin")){
            //超级管路员 查询所有权限
            roleResourceVos = resourceMapper.queryUserResources();
        }else{
            UserMapper userMapper = SpringUtil.getBean(UserMapper.class);
            List<UserRoleVo> userRoleVos = userMapper.queryUserRolesByUserName(userName);
            if(userRoleVos!=null){
                StringBuffer roleIds = new StringBuffer();
                userRoleVos.stream().forEach(roles ->{roleIds.append(roles.getRoleId()+",");});
                if(!org.springframework.util.StringUtils.isEmpty(roleIds)){
                    roleResourceVos = resourceMapper.queryUserResourcesByRoleIds(roleIds.toString().substring(0,roleIds.lastIndexOf(",")));
                }
            }
        }
        return roleResourceVos;
    }
}
