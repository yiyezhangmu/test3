package com.coolcollege.intelligent.common.util;

import com.coolcollege.intelligent.common.page.PageVO;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 分页参数工具类
 * @ClassName  PageHelperUtil
 * @Description 分页参数工具类
 * @author Aaron
 */
@Data
public class PageHelperUtil {

    /**
     * @Title getPageInfo
     * @Description 分页参数封装
     * @param  pageInfo
     * @return Map
     * @throws
     */
    public static <T> Map<String,Object>  getPageInfo(PageInfo pageInfo){
        Map<String,Object> map = Maps.newHashMap();
        map.put("page_size",pageInfo.getPageSize());
        map.put("total",pageInfo.getTotal());
        map.put("list",pageInfo.getList());
        map.put("page_num",pageInfo.getPageNum());
        //by jeffrey 兼容处理
        map.put("pageSize",pageInfo.getPageSize());
        map.put("pageNum",pageInfo.getPageNum());
        return map;
    }

    public static <T> PageVO<T> getPageVO(PageInfo<T> pageInfo) {
        List<T> list = pageInfo.getList();
        PageVO<T> pageVO =new PageVO<>();
        pageVO.setList(list);
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPage_num(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        pageVO.setPage_size(pageInfo.getPageSize());
        pageVO.setTotal(pageInfo.getTotal());
        return pageVO;
    }

    /**
     * @Title 数据返回
     * @Description 数据返回
     * @param  list
     * @return Map
     * @throws
     */
    public static <T> Map<String,Object> getInfo(List list){
        Map<String,Object> map = Maps.newHashMap();
        map.put("list",list);
        return map;
    }

    /**
     * @Title getPageInfo
     * @Description 分页参数封装
     * @param  listPageInfo
     * @return Map
     * @throws
     */
    public static <T> Map<String,Object> getPageInfo(ListPageInfo listPageInfo){
        Map<String,Object> map = Maps.newHashMap();
        map.put("page_size",listPageInfo.getPageSize());
        map.put("total",listPageInfo.getTotal());
        map.put("list",listPageInfo.getList());
        map.put("page_num",listPageInfo.getPageNum());
        //by jeffrey 兼容处理
        map.put("pageSize",listPageInfo.getPageSize());
        map.put("pageNum",listPageInfo.getPageNum());
        return map;
    }


}
