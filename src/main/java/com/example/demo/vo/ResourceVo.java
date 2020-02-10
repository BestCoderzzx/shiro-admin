package com.example.demo.vo;

import lombok.Data;

import java.util.List;

@Data
public class ResourceVo {

    private Integer id;

    private Integer pid;

    private String label;

    private Integer sort;

    private Integer level;

    private List<ResourceVo> children;

}
