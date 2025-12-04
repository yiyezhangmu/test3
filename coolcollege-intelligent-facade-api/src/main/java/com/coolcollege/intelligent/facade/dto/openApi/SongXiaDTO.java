package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

@Data
public class SongXiaDTO {
    /**
     * 页码
     */
    private Integer pageNum=1;

    /**
     * 条数
     */
    private Integer pageSize=20;


    private String startReportDate;

    private String endReportDate;
}
