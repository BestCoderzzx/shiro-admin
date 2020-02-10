package com.example.demo.config;

import com.alibaba.fastjson.JSON;
import com.example.demo.vo.ResultData;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

public class MyHttpAuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("====FormAuthenticationFilter====");
        // 在这里进行验证码的校验
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/json");
        PrintWriter writer = httpServletResponse.getWriter();
        //没有登录的情况
        ResultData resultData = new ResultData();
        resultData.setCode(2001);
        resultData.setMessage("您没有登录");
        writer.println(JSON.toJSONString(resultData));
        writer.flush();
        writer.close();
        return super.onAccessDenied(request, response);
    }


}
