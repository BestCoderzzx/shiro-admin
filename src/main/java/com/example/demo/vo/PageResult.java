package com.example.demo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: shiro权限管理系统
 * @description: 分页对象
 * @author: 你微笑时很美
 * @create: 2019-11-26 22:16
 **/
@Data
@NoArgsConstructor
public class PageResult<T> {

    private Long total;
    private Integer totalPage;
    private List<T> items;

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }

}
