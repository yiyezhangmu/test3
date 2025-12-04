package com.coolcollege.intelligent.common.util;

import java.util.Collections;
import java.util.List;


public class ListPageInfo<T> {

    /** 当前页 */
    private int pageNum;

    /** 每页条数 */
    private int pageSize;

    /** 总页数 */
    private int pages;

    /** 总数据条数 */
    private int total;

    /** 分页数据 */
    private List<T> list;

    public ListPageInfo() {
    }

    public ListPageInfo(List<T> data, int pageNum, int pageSize) {
        if(pageSize <= 0){
            this.pageSize = data.size();
        }else{
            this.pageSize = pageSize;
        }
        if(pageNum <=0 ){
            this.pageNum = 1;
        } else {
            this.pageNum = pageNum;
        }
        if (data == null || data.isEmpty()) {
            this.list = Collections.emptyList();
            this.total = 0;
            this.pages = 0;
        }else{
            this.total = data.size();
            this.list = getPageList(data);
            this.pages = (total + this.pageSize - 1) / this.pageSize;
        }
    }
    
    public ListPageInfo(List<T> data, int number) {
    	 
        if(number <= 500000){
            this.pageSize = data.size();
        }
        else {
        	this.pageSize=500000;
        }
       
       this.pageNum = 1;
        if (data == null || data.isEmpty()) {
            this.list = Collections.emptyList();
            this.total = 0;
            this.pages = 0;
        }
        else{
            this.total = data.size();
            this.list = getPageList(data);
            this.pages = (total + this.pageSize - 1) / this.pageSize;
        }
    }
    
    

    public ListPageInfo(List<T> data) {
        if(pageSize <= 0){
            this.pageSize = data.size();
        }
        if(pageNum <=0 ){
            this.pageNum = 1;
        }
        if (data == null || data.isEmpty()) {
            this.list = Collections.emptyList();
            this.total = 0;
            this.pages = 0;
        }
    }

    /**
     * 得到分页后的数据
     * @return 分页后结果
     */
    public List<T> getPageList(List<T> data) {
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();//空数组
        }
        if(fromIndex<0){
            return Collections.emptyList();//空数组
        }
        int toIndex = pageNum * pageSize;
        if (toIndex >= data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPages() {
        return pages;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getList() {
        return list;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
