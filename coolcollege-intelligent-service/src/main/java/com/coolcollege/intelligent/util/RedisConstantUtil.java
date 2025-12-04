package com.coolcollege.intelligent.util;

import com.coolcollege.intelligent.common.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * redis常量工具类
 *
 * @author byd
 */
@Component
@Slf4j
public class RedisConstantUtil {

    @Value("${spring.profiles.active}")
    private String active;

    /**
     * 获取钉钉同步区域key
     *
     * @param eid
     * @return
     */
    public String getSyncRegionKey(String eid) {

        return active + "_" + RedisConstant.REGION_SYNC_ALL + eid;
    }

    /**
     * 获取门店同步key
     *
     * @param eid
     * @return
     */
    public String getSyncStoreKey(String eid) {

        return active + "_" + RedisConstant.STORE_SYNC_ALL + eid;
    }

    public String getSyncLockKey(String eid) {

        return active + "_" + RedisConstant.REGION_SYNC_LOCK + eid;
    }

    public String getSyncEidEffectiveKey(String eid) {

        return active + "_" + RedisConstant.EID_SYNC_EFFECTIVE + eid;
    }

    public String getTaskStageNoticeKey(String key) {

        return active + "_" + RedisConstant.TASK_STAGE_NOTICE + key;
    }

    public String getTaskDelFlagKey(String key) {

        return active + "_" + RedisConstant.TASK_DEL_FLAG + key;
    }

    public String getCapturePicture(String key) {

        return active + "_" + RedisConstant.CAPTURE_PICTURE_STATUS_PREFIX + key;
    }


    public String getQuestionTaskLockKey(String eid, String key) {
        return active + "_" + RedisConstant.QUESTION_TASK_LOCK + "_" + eid + "_" + key;
    }

    public  String getStoreWorkQuestionTaskLockKey(String eid, String key) {
        return active + "_" + RedisConstant.STORE_WORK_QUESTION_TASK_LOCK + "_" + eid + "_" + key;
    }

    public String getShowStoreAuthKey() {
        return active + "_" + RedisConstant.SHOW_STORE_AUTH;
    }

    public String getRegionNameKey(String eid, String regionId) {
        return active + "_" + RedisConstant.REGION_ALL_NAME_CACHE + eid + ":" + regionId;
    }

    public String getGuideInfoKey(String eid, Long menuId) {
        return active + "_" + RedisConstant.GUIDE_INFO + eid + ":" + menuId;
    }

    public String getRegionNameListKey(String eid, String regionId) {
        return active + "_" + RedisConstant.REGION_ALL_NAME_CACHE + eid + ":" + regionId;
    }
}
