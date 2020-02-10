package com.example.demo.service.impl;

import com.example.demo.entity.Resource;
import com.example.demo.mapper.ResourceMapper;
import com.example.demo.service.ResourceService;
import com.example.demo.vo.ResourceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService{

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public List<ResourceVo> queryTree(int id) {
        List<ResourceVo> resourceVos = new ArrayList<>();
        Example example = new Example(Resource.class);
        example.createCriteria().andEqualTo("pid",id);
        example.setOrderByClause("sort desc");
        List<Resource> resourceList = resourceMapper.selectByExample(example);
        for(Resource resource:resourceList){
            ResourceVo resourceVo = new ResourceVo();
            resourceVo.setPid(id);
            resourceVo.setId(resource.getId());
            resourceVo.setLevel(resource.getLevel());
            resourceVo.setLabel(resource.getResourceName());
            resourceVo.setSort(resource.getSort());
            resourceVo.setChildren(queryTree(resource.getId()));
            resourceVos.add(resourceVo);
        }
        return resourceVos;
    }

    @Override
    public int saveResource(Resource resource) {
        resource.setCreateTime(new Date());
        return resourceMapper.insert(resource);
    }

    @Override
    public void delResource(int id){
        Map<Integer,Integer> resourceMap = new HashMap<>();
        List<Resource> resourceList = resourceMapper.selectAll();
        if(resourceList!=null&&resourceList.size()>0){
            for(Resource resource:resourceList){
                resourceMap.put(resource.getId(),resource.getPid());
            }
        }
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        queryAllNeedDelIds(ids,id,resourceMap);
        System.out.println("需要删除的所有节点数据:"+ids);
        //批量删除
        List<String> strings = ids.stream().map(x -> x + "").collect(Collectors.toList());
        resourceMapper.deleteByIds(String.join(",",strings));
    }

    //递归获取需要删除的节点集合
    public List<Integer> queryAllNeedDelIds(List<Integer> ids,Integer delId,Map<Integer,Integer> resourceMap){
        Iterator<Map.Entry<Integer, Integer>> iterator = resourceMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, Integer> next = iterator.next();
            Integer id = next.getKey();
            Integer pid = next.getValue();
            if(delId.compareTo(pid)==0){
                ids.add(id);
                queryAllNeedDelIds(ids, id,resourceMap);
        }
        }
        return ids;
    }
}
