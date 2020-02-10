package com.example.demo.controller;

import com.example.demo.entity.Resource;
import com.example.demo.service.ResourceService;
import com.example.demo.vo.ResourceVo;
import com.example.demo.vo.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 查询菜单树形数据
     * @return
     */
    @RequestMapping("/queryTree")
    public ResultData queryTree(){
        ResultData resultData = new ResultData();
        List<ResourceVo> resourceVos = resourceService.queryTree(0);
        resultData.setCode(200);
        resultData.setResult(resourceVos);
        return  resultData;
    }

    /**
     * 新增菜单
     * @param resource
     * @return
     */
    @RequestMapping("/saveResource")
    public ResultData saveResource(Resource resource){
        ResultData resultData = new ResultData();
        int num = resourceService.saveResource(resource);
        resultData.setCode(num>0?200:num);
        resultData.setResult(resource.getId());
        return resultData;
    }

    @RequestMapping("/delResource")
    public ResultData delResource(int id){
        ResultData resultData = new ResultData();
        resourceService.delResource(id);
        resultData.setCode(200);
        return resultData;
    }
}
