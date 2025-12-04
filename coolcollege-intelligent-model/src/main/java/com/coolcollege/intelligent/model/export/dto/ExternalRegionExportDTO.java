package com.coolcollege.intelligent.model.export.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import com.coolcollege.intelligent.model.impoetexcel.dto.RegionNodeDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 邵凌志
 * @date 2020/12/8 21:30
 */
@Data
public class ExternalRegionExportDTO {

    @Excel(name = "一级部门", orderNum = "1", width = 10)
    private String region1;

    @Excel(name = "二级部门", orderNum = "2", width = 10)
    private String region2;

    @Excel(name = "三级部门", orderNum = "3", width = 10)
    private String region3;

    @Excel(name = "四级部门", orderNum = "4", width = 10)
    private String region4;

    @Excel(name = "五级部门", orderNum = "5", width = 10)
    private String region5;

    @Excel(name = "六级部门", orderNum = "6", width = 10)
    private String region6;

    @Excel(name = "七级部门", orderNum = "7", width = 10)
    private String region7;

    @Excel(name = "八级部门", orderNum = "8", width = 10)
    private String region8;

    /**
     * 转换成链表结构
     * @return
     */
    public ExternalRegionExportDTO getExportDto(List<String> nodeNames){
        ExternalRegionExportDTO result = new ExternalRegionExportDTO();
        Field[] declaredFields = ExternalRegionExportDTO.class.getDeclaredFields();
        int size = nodeNames.size();
        for (Field field : declaredFields) {
            Excel annotation = field.getAnnotation(Excel.class);
            if(Objects.isNull(annotation)){
                continue;
            }
            int index = Integer.valueOf(annotation.orderNum());
            if(index > size){
                continue;
            }
            String nodeName = nodeNames.get(index-1);
            try {
                field.set(result, nodeName);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取子节点
     * @param fieldList
     * @param index
     * @param root
     * @return
     */
    public RegionNodeDTO getSubNode(List<Field> fieldList, int index, RegionNodeDTO root){
        if(index < 0 || index >= fieldList.size()){
            return null;
        }
        RegionNodeDTO result = null;
        try {
            Field field = fieldList.get(index);
            String fieldValue = (String)field.get(this);
            if(StringUtils.isBlank(fieldValue)){
                return null;
            }
            result = new RegionNodeDTO();
            result.setName(fieldValue);
            result.setLevel(index + 1);
            result.setSubNode(getSubNode(fieldList, index + 1, root));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<ExternalRegionExportDTO> convertList(List<RegionDO> regionList){
        if(CollectionUtils.isEmpty(regionList)){
            return Lists.newArrayList();
        }
        Map<String, RegionDO> regionIdMap = regionList.stream().collect(Collectors.toMap(k -> k.getRegionId(), Function.identity()));
        List<ExternalRegionExportDTO> resultList = new ArrayList<>();
        for (RegionDO region : regionList) {
            if(FixedRegionEnum.EXTERNAL_USER.getId().equals(region.getId())){
                continue;
            }
            ExternalRegionExportDTO export = new ExternalRegionExportDTO();
            String fullRegionPath = region.getFullRegionPath();
            String[] nodeIds = fullRegionPath.replace(FixedRegionEnum.EXTERNAL_USER.getFullRegionPath(), "").split(Constants.SLASH);
            List<String> nodeNames = new ArrayList<>();
            for (String nodeId : nodeIds) {
                if(StringUtils.isBlank(nodeId)){
                    continue;
                }
                RegionDO regionDO = regionIdMap.get(nodeId);
                nodeNames.add(Optional.ofNullable(regionDO).map(RegionDO::getName).orElse(""));
            }
            resultList.add(export.getExportDto(nodeNames));
        }
        return resultList;
    }

}
