package com.coolcollege.intelligent.service.importexcel;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.BaseException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.model.export.ExportView;
import com.coolcollege.intelligent.service.fileUpload.OssClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author 邵凌志
 * @date 2020/12/11 16:36
 */
@Service
@Slf4j
public class GenerateOssFileService {

    @Autowired
    private OssClientService ossClientService;

    private static final String EXCEL_SUFFIX = ".xlsx";

    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public String generateOssExcel(List<?> dataList, String eid, String title, String shellName, String contentType, Class<?> clazz) {
        // 设置excel的基本参数
        ExportParams params = new ExportParams(title, shellName, ExcelType.XSSF);
        // 生成excel对象
        Workbook workbook = ExcelExportUtil.exportBigExcel(params, clazz, dataList);
        // 获取excel的字节
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            log.error("获取文件流失败", e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        } finally {
            try{
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            try {
                bos.close();
            } catch (IOException e) {
                log.error("关闭字节流失败", e);
            }
        }

        byte[] bytes = bos.toByteArray();
        long size = bytes.length;
        InputStream is = new ByteArrayInputStream(bytes);
        String fileName = getUploadPath(eid) + UUIDUtils.get32UUID() + EXCEL_SUFFIX;
        contentType = StrUtil.isBlank(contentType) ? CONTENT_TYPE : contentType;
        String url;
        try {
            url = ossClientService.putObject(fileName, is, size, contentType);
        } catch (Exception e) {
            log.error("fileUpload upload err, originFileName={}", fileName, e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return url;
    }

    public String generateOssExcelSheet( String eid, List<ExportView> exportViewList, String contentType,String fileName) {

        // 生成excel对象
        Workbook workbook = ExcelExportUtil.exportExcel(getExportMap(exportViewList),ExcelType.XSSF);
        // 获取excel的字节
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            log.error("获取文件流失败", e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        } finally {
            try{
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            try {
                bos.close();
            } catch (IOException e) {
                log.error("关闭字节流失败", e);
            }
        }

        byte[] bytes = bos.toByteArray();
        long size = bytes.length;
        InputStream is = new ByteArrayInputStream(bytes);
        String newFileName = getUploadPath(eid) + fileName + UUIDUtils.get32UUID() + EXCEL_SUFFIX;
        contentType = StrUtil.isBlank(contentType) ? CONTENT_TYPE : contentType;
        String url;
        try {
            url = ossClientService.putObject(newFileName, is, size, contentType);
        } catch (Exception e) {
            log.error("fileUpload upload err, originFileName={}", newFileName, e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return url;
    }

    private List<Map<String,Object>> getExportMap(List<ExportView> exportViewList){
        List<Map<String, Object>> exportParamList= new ArrayList<>();
        //迭代导出对象，将对应的配置信息写入到实际的配置中
        for(ExportView view:exportViewList){
            Map<String, Object> valueMap= new HashMap<>();
            valueMap.put("title",view.getExportParams());
            valueMap.put(NormalExcelConstants.DATA_LIST,view.getDataList());
            valueMap.put(NormalExcelConstants.CLASS,view.getCls());
            exportParamList.add(valueMap);
        }
        return exportParamList;
    }

    public String generateOssExcel(byte[] bytes, String eid) {

        long size = bytes.length;
        InputStream is = new ByteArrayInputStream(bytes);
        String fileName = getUploadPath(eid) + UUIDUtils.get32UUID() + EXCEL_SUFFIX;
        String url;
        try {
            url = ossClientService.putObject(fileName, is, size, CONTENT_TYPE);
        } catch (Exception e) {
            log.error("fileUpload upload err, originFileName={}", fileName, e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return url;
    }


    public String generateOssWorkBookExcel(String eid, Workbook workbook, String name) {
        // 获取excel的字节
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            log.error("获取文件流失败", e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("关闭字节流失败", e);
            }
        }

        byte[] bytes = bos.toByteArray();
        long size = bytes.length;
        InputStream is = new ByteArrayInputStream(bytes);
        if(StringUtils.isNotBlank(name)&&name.contains(".")){
            name = name.substring(0, name.lastIndexOf("."));
        }
        String fileName = getUploadPath(eid) + name + "_" + UUIDUtils.get32UUID() + EXCEL_SUFFIX;
        String url;
        try {
            url = ossClientService.putObject(fileName, is, size, CONTENT_TYPE);
        } catch (Exception e) {
            log.error("fileUpload upload err, originFileName={}", fileName, e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return encodeUrl(url);
    }

    /**
     * 特殊字符转义，保留url结构，仅转义特殊字符'+'，' '
     * @param url url
     * @return 转义url
     */
    public String encodeUrl(String url) {
        try {
            // 1. 提取路径中的文件名
            String[] pathSegments = url.split("/");
            String filename = pathSegments[pathSegments.length - 1];
            // +号转义
            String encodedFileName = filename.replace("+", "%2B");

            // 3. 重构路径（保留其他部分不变）
            StringBuilder newPath = new StringBuilder();
            for (int i = 0; i < pathSegments.length - 1; i++) {  // 跳过协议、空串和域名部分
                newPath.append(pathSegments[i]).append("/");
            }
            newPath.append(encodedFileName);
            return newPath.toString();
        } catch (Exception e) {
            log.error("url转义失败", e);
            return url;
        }
    }

    public String getUploadPath(String eid){
        String time = DateUtil.format(new Date(),"yyMM");
        return "eid"+"/"+eid + "/" + time + "/";
    }
}
