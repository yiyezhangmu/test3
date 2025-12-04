package com.coolcollege.intelligent.common.util;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: ListUtils
 * @Description: 集合处理
 * @date 2022-03-04 20:04
 */
public class ListOptUtils {

    public static <T> List<T>  getIntersection(List<T> listA, List<T> listB){
        if(CollectionUtils.isEmpty(listA) || CollectionUtils.isEmpty(listB)){
            return Lists.newArrayList();
        }
        return listA.stream().filter(item -> listB.contains(item)).collect(Collectors.toList());
    }


    /**
     * long集合转Stirng集合
     * @param listA
     * @return
     */
    public static List<String>  longListConvertStringList(List<Long> listA){
        List<String> result = Lists.newArrayList();
        if(CollectionUtils.isEmpty(listA)){
            return result;
        }
        for (Long temp:listA) {
            result.add(String.valueOf(temp));
        }
        return result;
    }


}
