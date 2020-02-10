package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "role_resource")
public class RoleResource {

    @Id
    private Integer id;

    private Integer roleId;

    private Integer resourceId;

}
