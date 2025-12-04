package com.coolcollege.intelligent.service.importexcel;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.collection.CollUtil;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/12/17 14:11
 */
@Component
@Slf4j
public class ImportBaseService {

    @Autowired
    private RedisUtilPool redis;

    public static final String EXIST_TASK = "存在正在进行的上传任务，请等待上一个任务完成";

    static final String SYSTEM_ERROR = "文件上传失败！";

    static final String EMPTY_FILE = "文件为空";

    public static final Integer LOCK_TIME = 1800;

    public static final String DATA_ERROR = "数据或格式不正确，请检查该行内容";

    public static final String UPLOAD_TYPE = "import";
    /**
     * 加锁
     * @param eid
     * @param key
     * @return
     */
    public boolean lock(String eid, String key) {
        return redis.setNxExpire(String.format(key, eid), UUIDUtils.get8UUID(), LOCK_TIME);
    }

    /**
     * 解锁
     * @param eid
     * @param key
     */
    public void unlock(String eid, String key) {
        redis.delKey(String.format(key, eid));
    }

    public <T> List<T> getImportList(MultipartFile file, Class<T> clazz) {
        long startTime = System.currentTimeMillis();
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        List<T> importList;
        try {
            importList = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
            long endTime = System.currentTimeMillis();
            log.info("文件解析时间：" + (endTime - startTime) + "ms");
            // 大数据量（超过5w）用此方法解析，但是会出现空字段会被后面的字段覆盖的bug
//            ExcelImportUtil.importExcelBySax(file.getInputStream(), clazz, params, new IReadHandler<T>() {
//                @Override
//                public void handler(T t) {
//                    importList.add(t);
//                }
//
//                @Override
//                public void doAfterAll() {
//                    log.info("解析数据完必！");
//                }
//            });
            if (CollUtil.isEmpty(importList)) {
                throw new ServiceException(500001, "文件内容为空");
            }
        } catch (Exception e) {
            log.error("文件解析失败", e);
            throw new ServiceException(500001, "文件解析失败！");
        }
        return importList;
    }
}
