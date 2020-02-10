package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "`resource`")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String resourceName;

    private String url;

    private Integer pid;

    private String flagName;

    @Column(name = "`sort`")
    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer level;

}
