package com.example.demo.vo;

import lombok.Data;

@Data
public class RoleResourceVo {

    private Integer roleId;

    private Integer resourceId;

    private String roleName;

    private String resourceName;

    private Integer pid;

    private Integer level;

    private String flagName;

}
