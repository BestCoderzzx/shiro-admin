package com.example.demo.controller;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Controller
@RequestMapping(value = "/filter")
public class FilterManageController {

//    Logger logger = LoggerFactory.getLogger(FilterManageController.class);

    //加载自定义的拦截工厂
    @Autowired
    private ShiroFilterFactoryBean myShiroFilterFactoryBean;


    @RequestMapping(value = "/change", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> updateFilter() {
        Map<String, String> filterMap = new HashMap<>();
        filterMap.put("/user/login**", "anon");
        filterMap.put("/admin/**", "anon");//把 admin 设置成不需要拦截
        filterMap.put("/super/**", "authc,roles[super],perms[super:info]");

        filterMap.put("/userAdd","perms[userAdd]");//用户添加权限
        filterMap.put("/userDel","perms[userDel]");//用户删除权限

        updatePermission(filterMap);

        return myShiroFilterFactoryBean.getFilterChainDefinitionMap();
    }

    /**
     * 动态更新新的权限
     * @param filterMap
     */
    private synchronized void updatePermission(Map<String, String> filterMap) {

        AbstractShiroFilter shiroFilter = null;
        try {
            shiroFilter = (AbstractShiroFilter) myShiroFilterFactoryBean.getObject();

            // 获取过滤管理器
            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                    .getFilterChainResolver();
            DefaultFilterChainManager filterManager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();

            //清空拦截管理器中的存储
            filterManager.getFilterChains().clear();
            /*
            清空拦截工厂中的存储,如果不清空这里,还会把之前的带进去
            ps:如果仅仅是更新的话,可以根据这里的 map 遍历数据修改,重新整理好权限再一起添加
             */
            myShiroFilterFactoryBean.getFilterChainDefinitionMap().clear();

            // 相当于新建的 map, 因为已经清空了
            Map<String, String> chains = myShiroFilterFactoryBean.getFilterChainDefinitionMap();
            //把修改后的 map 放进去
            chains.putAll(filterMap);

            //这个相当于是全量添加
            for (Map.Entry<String, String> entry : filterMap.entrySet()) {
                //要拦截的地址
                String url = entry.getKey().trim().replace(" ", "");
                //地址持有的权限
                String chainDefinition = entry.getValue().trim().replace(" ", "");
                //生成拦截
                filterManager.createChain(url, chainDefinition);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            logger.error("updatePermission error,filterMap=" + filterMap, e);
        }
    }
}

