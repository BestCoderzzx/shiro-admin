package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.mapper.ResourceMapper;
import com.example.demo.service.UserService;
import com.example.demo.utils.SpringUtil;
import com.example.demo.vo.RoleResourceVo;
import com.example.demo.vo.UserRoleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import com.example.demo.mapper.UserMapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2019/6/16.
 */
@Slf4j
public class MyRealm extends AuthorizingRealm {

    /**
     * 权限校验
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
       //1.根据用户名 查询用户拥有的资源权限
        System.out.println("============");
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        log.info("principal:"+principal);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        UserService userService = SpringUtil.getBean(UserService.class);

        //查询用户拥有的权限列表
        List<RoleResourceVo> roleResourceVos = userService.queryMyPermissions(principal.toString());
        if(roleResourceVos!=null&&roleResourceVos.size()>0){
            for(RoleResourceVo roleResourceVo:roleResourceVos){
                if(!StringUtils.isEmpty(roleResourceVo.getFlagName())){
                    simpleAuthorizationInfo.addStringPermission(roleResourceVo.getFlagName());
                }
//                simpleAuthorizationInfo.addStringPermission("userAdd");
            }
        }
        return simpleAuthorizationInfo;
    }

    /**
     * 登录认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        log.info("username:"+username);

        UserMapper userMapper = SpringUtil.getBean(UserMapper.class);
        User user = new User();
        user.setUserName(username);
        User db_user = userMapper.selectOne(user);

        if(db_user==null){
            return null;
        }

        String dbPassword = db_user.getPasswd();
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(username,dbPassword,username);
        return authenticationInfo;
    }

//    /**
//     * @title 刷新用户权限
//     * @desc principal为用户的认证信息
//     */
//    public static void  reloadAuthorizing(MyRealm myRealm,String username) throws Exception{
//        Subject subject = SecurityUtils.getSubject();
//        String realmName = subject.getPrincipals().getRealmNames().iterator().next();
//        System.out.println("realmName:"+realmName);
//        //第一个参数为用户名,第二个参数为realmName,test想要操作权限的用户
//        SimplePrincipalCollection principals = new SimplePrincipalCollection(username,realmName);
//        subject.runAs(principals);
//        myRealm.getAuthorizationCache().remove(subject.getPrincipals());
//        subject.releaseRunAs();
//    }
}
