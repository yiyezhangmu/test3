package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 邵凌志
 * @date 2020/12/8 21:30
 */
@Data
public class ExternalRegionImportDTO {

    @Excel(name = "描述", width = 30)
    private String dec;

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
    public RegionNodeDTO getRegionNodeDto(String rootName, String externalRegionName){
        RegionNodeDTO result = new RegionNodeDTO();
        result.setName(rootName);
        result.setId("1");
        result.setLevel(1);
        RegionNodeDTO secondNode = new RegionNodeDTO();
        secondNode.setName(externalRegionName);
        secondNode.setId(FixedRegionEnum.EXTERNAL_USER.getId().toString());
        secondNode.setLevel(2);
        result.setSubNode(secondNode);
        Field[] declaredFields = ExternalRegionImportDTO.class.getDeclaredFields();
        List<Field> sortField = Stream.of(declaredFields).filter(o-> Integer.valueOf(o.getAnnotation(Excel.class).orderNum()) > 0).sorted(Comparator.comparing(o -> Integer.valueOf(o.getAnnotation(Excel.class).orderNum()))).collect(Collectors.toList());
        Field field = sortField.get(0);
        try {
            RegionNodeDTO thirdNode = new RegionNodeDTO();
            String fieldValue = (String)field.get(this);
            if(StringUtils.isBlank(fieldValue)){
                return result;
            }
            secondNode.setSubNode(thirdNode);
            thirdNode.setName(fieldValue);
            if(FixedRegionEnum.MYSTERIOUS_GUEST.getName().equals(fieldValue)){
                thirdNode.setId(FixedRegionEnum.MYSTERIOUS_GUEST.getId().toString());
            }
            thirdNode.setLevel(3);
            thirdNode.setSubNode(getSubNode(sortField, 1, result));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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

    public static void main(String[] args) {
        ExternalRegionImportDTO region = new ExternalRegionImportDTO();
        region.setRegion1("一级");
        region.setRegion2("二级");
        region.setRegion3("三级");
        region.setRegion4("四级");
        region.setRegion5(null);
        region.setRegion6("六级");
        region.setRegion7("七级");
        region.setRegion8("八级");
        RegionNodeDTO regionNodeDto = region.getRegionNodeDto("AAA", "BBB");
        System.out.println(JSON.toJSONString(regionNodeDto));
    }

}
