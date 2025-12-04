package com.coolcollege.intelligent.common.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 门店路径去重
 * @Author chenyupeng
 * @Date 2021/7/7
 * @Version 1.0
 */
public class DistinctRegionPathUtil {
    private DistinctRegionPathUtil() {}
    /**
     * 门店路径去重（用在计算总数）
     * @Author chenyupeng
     * @Date 2021/7/8
     * @param regionPaths
     * @return: java.util.List<java.lang.String>
     */
    public static List<String> distinctRegionPath(List<String> regionPaths){
        Set<String> repeatRegionPathSet =  getRepeatRegionPath(regionPaths);
        return regionPaths.stream().filter(e -> !repeatRegionPathSet.contains(e)).collect(Collectors.toList());
    }

    /**
     * 获取重复的路径
     * @Author chenyupeng
     * @Date 2021/7/8
     * @param regionPaths
     * @return: java.util.Set<java.lang.String>
     */
    public static Set<String> getRepeatRegionPath(List<String> regionPaths){
        Set<String> tempList = new HashSet<>();
        Collections.sort(regionPaths);
        for (int i = 0; i < regionPaths.size() - 1;i++) {
            for (int j = i + 1; j < regionPaths.size(); j++) {
                //如果重复
                if(regionPaths.get(j).contains(regionPaths.get(i))){
                    tempList.add(regionPaths.get(j));
                }else {
                    break;
                }
            }
        }
        return tempList;
    }
}
