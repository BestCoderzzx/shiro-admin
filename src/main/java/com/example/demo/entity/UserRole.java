package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "user_role")
public class UserRole {

    private Integer id;

    private Integer userId;

    private Integer roleId;

}
