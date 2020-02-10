package com.example.demo.config;

import com.example.demo.entity.Resource;
import com.example.demo.mapper.ResourceMapper;
import com.example.demo.utils.SpringUtil;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2019/6/16.
 */
@Configuration
public class ShiroConfig {

    @Autowired
    private ResourceMapper resourceMapper;

    @Bean("shiroFilterFactoryBean")
    ShiroFilterFactoryBean ShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
//        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");//没有权限跳转的页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        Map<String,String> filterChainDefinitionMap = new HashMap<>();
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/**","authc");//登录认证权限
//        filterChainDefinitionMap.put("/userAdd","perms[userAdd]");//用户添加权限
//        filterChainDefinitionMap.put("/userDel","perms[userDel]");//用户删除权限
//        shiroFilterFactoryBean.setSuccessUrl();
        //动态查询 所有资源需要的权限
        List<Resource> resourceList = resourceMapper.selectAll();
        if(resourceList!=null&&resourceList.size()>0){
            for(Resource resource:resourceList){
                filterChainDefinitionMap.put(resource.getUrl(),"perms["+resource.getFlagName()+"]");
            }
        }
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        //设置自定义权限认证 fileter
        Map<String,Filter> filterMap = new HashMap<>();
        filterMap.put("authc",new MyHttpAuthenticationFilter());
        filterMap.put("perms",new MyUserAuthFilter());

        shiroFilterFactoryBean.setFilters(filterMap);

        return shiroFilterFactoryBean;
    }

    @Bean("defaultWebSecurityManager")
    DefaultWebSecurityManager DefaultWebSecurityManager(MyRealm myRealm){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(myRealm);
        return defaultWebSecurityManager;
    }

    /**
     * myrealm
     * @return
     */
    @Bean("myRealm")
    MyRealm myRealm(){
        return new MyRealm();
    }

//    @Bean
//    public ShiroLoginFilter shiroLoginFilter(){
//        return new ShiroLoginFilter();
//    }
}
