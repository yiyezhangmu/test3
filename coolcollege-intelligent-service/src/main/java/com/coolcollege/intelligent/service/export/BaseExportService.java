package com.coolcollege.intelligent.service.export;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecate 目前只支持easy poi的导出
 * @author shuchang.wei
 * @date 2021/6/4 10:51
 */
public interface BaseExportService {

    /**
     * 请求参数校验
     *
     * @param fileExportBaseRequest 请求参数
     */
    void validParam(FileExportBaseRequest fileExportBaseRequest);


    /**
     * 导出总数
     *
     * @param enterpriseId 企业id
     * @param request      请求参数
     * @return
     */
    Long getTotalNum(String enterpriseId, FileExportBaseRequest request);



    /**
     * 导出实现类的名称
     *
     * @return
     */
    ExportServiceEnum getExportServiceEnum();


    /**
     * 分页导出列表
     *
     * @param enterpriseId 企业id
     * @param request      请求参数
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum);

    /**
     * 多sheet导出
     */
    default Map<String,List<?>> exportListSheet(String enterpriseId, JSONObject request){
         return null;
     }

    /**
     * 动态字段导出，注解导出返回Null
     * @param request
     * @return
     */
    default List<ExcelExportEntity> exportFields(JSONObject request){
        return null;
    }

    default String getTitle(){
        return null;
    }

    /**
     * 是否是多sheet导出
     * @return
     */
    default Boolean sheetExport(){
        return false;
    }

    /**
     * 多sheet标题
     */
    default Map<String,String> getTitleSheet(){
        return new HashMap<>();
    }

    /**
     * 获取sheet名称
     * @param request
     * @return
     */
    default String getSheetName(ExportMsgSendRequest request){
        return null;
    }

}
