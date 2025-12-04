package com.coolcollege.intelligent.common.util;

import com.github.pagehelper.PageHelper; 
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Deprecated  //垃圾代码，内存分页
public class PageInfoUtil {
    /**
     * 获取分页数据
     * @param resultList  结果集合
     * @param <T>  结果实体类
     * @return
     */
    public static <T> PageInfo<T> getPageInfo(List<T> resultList){
        return new PageInfo<T>(resultList);
    }

    /**
     * 获取分页数据
     * @param resultList  结果集合
     * @param <T>  结果实体类
     * @return
     */
    public static <T> ListPageInfo<T> getListPageInfo(List<T> resultList, Map<String,Object> paraMap){
        Integer pageNum = getInteger("pageNum",paraMap);
        Integer pageSize =  getInteger("pageSize",paraMap);
        return getListPageInfo(resultList,pageNum,pageSize);
    }
    
    /**
     * 获取导出数据
     * @param resultList  结果集合
     * @param <T>  结果实体类
     * @return
     */
    public static <T> ListPageInfo<T> getExportListPageInfo(List<T> resultList, Map<String,Object> paraMap){
        Integer pageSize =  getInteger("pageSize",paraMap);
        return getExportListPageInfo(resultList,pageSize);
    }
    
    /**
     * 获取分页数据
     * @param resultList  结果集合
     * @param <T>  结果实体类
     * @return
     */
    public static <T> ListPageInfo<T> getExportListPageInfo(List<T> resultList,Integer pageSize){
        return new ListPageInfo<T>(resultList,pageSize);
    }
    
    /**
     * 获取分页数据
     * @param resultList  结果集合
     * @param <T>  结果实体类
     * @return
     */
    //FIXME　垃圾写的代码，　内存中分页
    public static <T> ListPageInfo<T> getListPageInfo(List<T> resultList, Integer pageNum, Integer pageSize){
        return new ListPageInfo<T>(resultList,pageNum,pageSize);
    }
    
    
    
    public static  <E> PageInfo<E> getPageHelper(int pageNum, int pageSize, List<E> list) {
    	PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<E>(list);
    }

    public static <T> Map<String,Object> getPage(List list, PageInfo pageInfo){
        Map<String,Object> objectMap = new HashMap<String,Object>();
        objectMap.put("pageNum",pageInfo.getPageNum());
        objectMap.put("pageSize",pageInfo.getPageSize());
        objectMap.put("isFirstPage",pageInfo.isIsFirstPage());
        objectMap.put("isLastPage",pageInfo.isIsLastPage());
        objectMap.put("pages",pageInfo.getPages());
        objectMap.put("total",pageInfo.getTotal());
        objectMap.put("list",list);
        return objectMap;
    }

    public static <T> Map<String,Object> getPage(List list){
        Map<String,Object> objectMap = new HashMap<String,Object>();
        objectMap.put("list",list);
        return objectMap;
    }

    public static <T> Map<String,Object> getPage(List list, ListPageInfo pageInfo){
        Map<String,Object> objectMap = new HashMap<String,Object>();
        objectMap.put("pageNum",pageInfo.getPageNum());
        objectMap.put("pageSize",pageInfo.getPageSize());
        objectMap.put("pages",pageInfo.getPages());
        objectMap.put("total",pageInfo.getTotal());
        objectMap.put("list",list);
        return objectMap;
    }


    public static Integer getInteger(String key,Map<String,Object> map){
        if(map.get(key)!= null){
            if(map.get(key) instanceof Integer){
                return (Integer)map.get(key);
            }else if(map.get(key) instanceof String){
                try {
                    return Integer.valueOf((String)map.get(key));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }else{
                return 0;
            }
        }else{
            return 0;
        }
    }

    public static <T> Map<String,Object> getPage(List list,int pageNum,int pageSize,int count){
        Map<String,Object> objectMap = Maps.newHashMap();
        objectMap.put("pageNum",pageNum);
        objectMap.put("pageSize",pageSize);
        objectMap.put("pages",count%pageSize == 0 ? count/pageSize : (count/pageSize+1));
        objectMap.put("total",count);
        objectMap.put("list",list);
        return objectMap;
    }
    
   
    public static Map<String,Object> getExportParaMap(Map<String, Object> paraMap) {
    	if(paraMap==null) {
    		paraMap = new HashMap<String, Object>();
    	}
    	paraMap.put("pageNum", 1);
    	paraMap.put("pageSize", Integer.MAX_VALUE);
    	
    	return paraMap;
    }


}
