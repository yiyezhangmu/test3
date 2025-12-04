package com.coolcollege.intelligent.model.impoetexcel.dto;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: RegionNodeDTO
 * @Description:
 * @date 2021-09-18 14:44
 */
@Data
public class RegionNodeDTO {

    /**
     * 区域名称
     */
    private String name;

    /**
     * 级别
     */
    private Integer level;

    private String id;

    /**
     * 子节点
     */
    private RegionNodeDTO subNode;

    public RegionNodeDTO(String name, RegionNodeDTO subNode){
        this.name = name;
        this.subNode = subNode;
    }

    public RegionNodeDTO(String name){
        this.name = name;
    }

    public RegionNodeDTO(){
    }
}
