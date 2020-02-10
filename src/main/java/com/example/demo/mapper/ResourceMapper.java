package com.example.demo.mapper;

import com.example.demo.entity.Resource;
import com.example.demo.vo.RoleResourceVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ResourceMapper extends Mapper<Resource>,IdsMapper<Resource>{

    /**
     * 根据角色集合查询 该角色拥有的权限
     * @param roles
     * @return
     */
     @Select(" select r.id as roleId,r.role_name as roleName,res.id as resourceId,res.resource_name as resourceName,res.pid as pid,res.level as level,res.flag_name as flagName from role r" +
             " LEFT JOIN role_resource rr ON r.id = rr.role_id" +
             " LEFT JOIN resource res ON rr.resource_id = res.id where r.id IN (${roles})")
     public List<RoleResourceVo> queryUserResourcesByRoleIds(@Param("roles") String roles);

    @Select(" select res.id as resourceId,res.resource_name as resourceName,res.pid as pid,res.level as level,res.flag_name as flagName from " +
            " resource res")
    public List<RoleResourceVo> queryUserResources();
}
