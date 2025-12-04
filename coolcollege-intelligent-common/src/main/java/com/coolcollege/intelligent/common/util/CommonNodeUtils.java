package com.coolcollege.intelligent.common.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/21
 */
public class CommonNodeUtils {
    /**
     * 树型数据的所有子项(list结构包含本身)(节点为Long)
     * @param rootId
     * @param id
     * @param all 树的所有节点
     * @param parentGroupMap Map<parentId,List<id>>
     * @return
     */
    public static List<Long> getAllChildListContainSelf(Long rootId,Long id, List<Long> all,Map<Long,List<Long>> parentGroupMap) {
        List<Long> childList = new LinkedList<>();
        if(CollectionUtils.isEmpty(all)){
            return Collections.emptyList();
        }
        if(id.equals(rootId)&&!all.contains(id)){
            return Collections.emptyList();
        }
        List<Long> idList = parentGroupMap.get(id);
        childList.add(id);
        if(CollectionUtils.isEmpty(idList)){
            return childList;
        }
        childList.addAll(idList);
        for (Long item:idList){
            getChildList(item,parentGroupMap,childList);
        }
        return childList;
    }

    private static void  getChildList(Long id,Map<Long,List<Long>> parentGroupMap, List<Long> childList) {
        List<Long> child = parentGroupMap.get(id);
        if (CollectionUtils.isNotEmpty(child)) {
            childList.addAll(child);
            for (Long cid : child) {
                getChildList(cid, parentGroupMap, childList);
            }
        }
    }




    /**
     * 树型数据的所有子项(list结构包含本身)(节点为String)
     * @param rootId
     * @param id
     * @param all 树的所有节点
     * @param parentGroupMap Map<parentId,List<id>>
     * @return
     */
    public static List<String> getAllChildListContainSelf(String rootId,String id, List<String> all,Map<String,List<String>> parentGroupMap) {
        List<String> childList = new LinkedList<>();
        if(CollectionUtils.isEmpty(all)){
            return Collections.emptyList();
        }
        if(id.equals(rootId)&&!all.contains(id)){
            return Collections.emptyList();
        }
        List<String> idList = parentGroupMap.get(id);
        childList.add(id);
        if(CollectionUtils.isEmpty(idList)){
            return childList;
        }
        childList.addAll(idList);
        for (String item:idList){
            getChildList(item,parentGroupMap,childList);
        }
        return childList;
    }

    private static void  getChildList(String id,Map<String,List<String>> parentGroupMap, List<String> childList) {
        List<String> child = parentGroupMap.get(id);
        if (CollectionUtils.isNotEmpty(child)) {
            childList.addAll(child);
            for (String cid : child) {
                getChildList(cid, parentGroupMap, childList);
            }
        }
    }


}
