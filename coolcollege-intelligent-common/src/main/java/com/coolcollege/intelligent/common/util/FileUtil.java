package com.coolcollege.intelligent.common.util;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.joor.Reflect;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 导出/上传/下载工具类
 */
@Slf4j
public class FileUtil {

    protected static final Integer size = 100000;

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response) {
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);

    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName, ExcelType.XSSF));
    }

    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        defaultExport(list, fileName, response);
    }

    public static void exportBigDataExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) {
        defaultBigDataExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName, ExcelType.XSSF));
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        if (workbook != null) ;
        downLoadExcel(fileName, response, workbook);
    }

    private static void defaultBigDataExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        try {
            exportParams.setMaxNum(1100000);
            Workbook workbook = ExcelExportUtil.exportBigExcel(exportParams, pojoClass, list);
            if (workbook != null) ;
            downLoadExcel(fileName, response, workbook);
        } catch (Exception e) {
            log.error("Excel Export Error:{}", e.getMessage());
        } finally {
            try{
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
        }
    }

    public static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("下载excel异常{}",e.getMessage(),e);
        }
    }

    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if (workbook != null) ;
        downLoadExcel(fileName, response, workbook);
    }

    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (Exception e) {
            log.error("导入excel异常{}",e.getMessage(),e);
        }
        return list;
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (Exception e) {
            log.error("导入excel异常{}",e.getMessage(),e);
        }
        return list;
    }


    /**
     * 大数据导出
     */
    public static <T> void bigDataExportExcel(String title, String sheetName, String fileName
            , HttpServletResponse response, Object obj, Map<String, Object> paraMap
            , String methodNameQuery, String methodNameCount, Class<?> clazz) {
        int total = Reflect.on(obj).call(methodNameCount, paraMap).get();
        int num = total % size == 0 ? total / size : (total / size) + 1;
        ExportParams params = new ExportParams(title, sheetName);
        Workbook workbook = null;
        List<T> resultList1 = null;
        for (int i = 0; i < num; i++) {
            int start = i * size;
            paraMap.put("pageStart", start);
            paraMap.put("pageNo", size);
            resultList1 = Reflect.on(obj).call(methodNameQuery, paraMap).get();
            if(start<500000){
                // 数据分页导出
                workbook = ExcelExportUtil.exportBigExcel(params, clazz, resultList1);
                resultList1.clear();
            }
        }
        try{
            ExcelExportUtil.closeExportBigExcel();
        } catch (Exception ex) {
            log.error("EXCEL文件导出对象关闭异常", ex);
        }
        downLoadExcel(fileName, response, workbook);
    }
    
    
    public static String bigDataExportExcel(String title, String sheetName, String localDirPath, String fileName,
			Map<String, Object> paraMap, Function<Map<String, Object>, List<?>> queryFunc, Class<?> exportClass) {
		final String exportPath = localDirPath + fileName;
		ExportParams params = new ExportParams(title, sheetName);
		Workbook workbook;
			List<?> resultList = queryFunc.apply(paraMap);
			workbook = ExcelExportUtil.exportBigExcel(params, exportClass, resultList);
			resultList.clear();
        try{
            ExcelExportUtil.closeExportBigExcel();
        } catch (Exception ex) {
            log.error("EXCEL文件导出对象关闭异常", ex);
        }
		try {
			if (workbook != null) {
				workbook.write(new FileOutputStream(exportPath));
				workbook.close();
			}
		} catch (Exception e) {
			log.error("导出异常", e);
		} finally {
            try{
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
			log.info("导出完成");
		}
		return exportPath;
	}


    
    public static void mkDir(String DirPath) {
		File dirFile = new File(DirPath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	}

    public static String getTypeByDisposition(String disposition) {
        String ext = null;
        if (!StringUtils.isEmpty(disposition)) {
            disposition = StringUtils.replace(disposition, "\"", "");
            String[] strs = disposition.split(";");
            for (String string : strs) {
                if (string.toLowerCase().contains("filename=")) {
                    ext = StringUtils.substring(string, string.lastIndexOf("."));
                    break;
                }
            }
        }
        return ext;
    }



    /**
     * @param strUrl
     * @return byte[]
     * @throws
     * @description: 获取网络图片转成字节流
     * @author zhy
     * @date 2019/10/23 8:59
     */
    public static byte[] getImageFromNetByUrl(String strUrl) {
        if (!isURL(strUrl)){
            return null;
        }
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2 * 1000);
            InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
            byte[] btImg = readInputStream(inStream);// 得到图片的二进制数据
            return btImg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] compressImage(byte[] imageData){
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), type);
            java.awt.Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos);
            baos.flush();
            byte[] compressedImage = baos.toByteArray();
            baos.close();
            return compressedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从输入流中获取字节流数据
     *
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[10240];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public static boolean isURL(String str) {
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"
                + "|"
                + "([0-9a-z_!~*'()-]+\\.)*"
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."
                + "[a-z]{2,6})"
                + "(:[0-9]{1,5})?"
                + "((/?)|"
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return str.matches(regex);
    }




}

