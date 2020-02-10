package com.example.demo.service;

import com.example.demo.entity.Resource;
import com.example.demo.vo.ResourceVo;

import java.util.List;

/**
 * 资源管理Service
 * @Author 你微笑时很美
 */
public interface ResourceService {

    public List<ResourceVo> queryTree(int id);

    int saveResource(Resource resource);

    public void delResource(int id);
}
