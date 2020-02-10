package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by lenovo on 2019/6/16.
 */
@Controller
public class UnauthorizedController {

    @RequestMapping("/unauthorized")
    public  String unauthorized(){
        return "/unauthorized";
    }

}
