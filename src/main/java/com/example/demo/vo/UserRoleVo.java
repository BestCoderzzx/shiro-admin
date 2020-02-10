package com.example.demo.vo;

import lombok.Data;

@Data
public class UserRoleVo {

    private Integer userId;

    private String userName;

    private String roleName;

    private Integer roleId;

    private boolean check;
}
