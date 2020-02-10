package com.example.demo.mapper;

import com.example.demo.entity.User;
import com.example.demo.vo.UserRoleVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserMapper extends Mapper<User> {

    /**
     * 根据用户名称 查询该用户拥有的角色集合
     * @param userName
     * @return
     */
    @Select(" SELECT u.id as userId,u.user_name as userName,r.role_name as roleName,r.id as roleId FROM `user` u " +
            " LEFT JOIN user_role ru on u.id = ru.user_id " +
            " LEFT JOIN role r ON ru.role_id = r.id where u.user_name=#{userName} ")
    public List<UserRoleVo> queryUserRolesByUserName(@Param("userName") String userName);

}
