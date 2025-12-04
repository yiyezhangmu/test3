package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty;

import lombok.Data;

import java.util.Objects;

@Data
public class PageReq{

    private Integer pageNum;

    private Integer pageSize;

    public Integer getPageNum() {
        if (Objects.isNull(pageNum)) {
            return 1;
        }
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        if (Objects.isNull(pageSize)) {
            return 20;
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
