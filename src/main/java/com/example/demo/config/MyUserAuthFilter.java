package com.example.demo.config;

import com.alibaba.fastjson.JSON;
import com.example.demo.vo.ResultData;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyUserAuthFilter extends AuthorizationFilter{

    /**
     * 只有 isAccessAllowed 返回false时候才会进来 ，我们响应JSON串给前端
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        System.out.println("======onAccessDenied");
        Subject subject = this.getSubject(request, response);
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/json");
        PrintWriter writer = httpServletResponse.getWriter();
        if (subject.getPrincipal() == null) {
            //没有登录的情况
            ResultData resultData = new ResultData();
            resultData.setCode(2001);
            resultData.setMessage("您没有登录");
            writer.println(JSON.toJSONString(resultData));
        } else {
            //没有权限
            ResultData resultData = new ResultData();
            resultData.setCode(403);
            resultData.setMessage("您没有该资源的访问权限");
            writer.println(JSON.toJSONString(resultData));
        }
        writer.flush();
        writer.close();
        return false;
    }

    /**
     * 判断是否有该资源的访问权限，如果有返回true 否则返回false
     * @param servletRequest
     * @param servletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        System.out.println("=====isAccessAllowed");
        String perms[] = (String [])o;

        boolean isperms = true;
        Subject subject = this.getSubject(servletRequest, servletResponse);
        if(perms!=null&&perms.length>0){
            if(perms.length==1){
                if(!subject.isPermitted(perms[0])){
                    isperms=false;
                }
            }else{
                if(!subject.isPermittedAll(perms)){
                    isperms=false;
                }
            }
        }
        System.out.println("isperms:"+isperms);
        return isperms;
    }
}
