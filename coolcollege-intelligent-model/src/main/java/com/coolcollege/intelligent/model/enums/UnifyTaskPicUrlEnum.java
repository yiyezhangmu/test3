package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/21 10:36
 */
public enum UnifyTaskPicUrlEnum {

    QUESTION_ORDER("QUESTION_ORDER","https://oss-cool.coolstore.cn/notice_pic/53c11d2f4ec94d4fa7edd7a96e40e2d6.png"),
    DISPLAY_TASK("DISPLAY_TASK","https://oss-cool.coolstore.cn/notice_pic/cfa059a851a84c788134f533b3d5e279.jpg"),
    TB_DISPLAY_TASK("TB_DISPLAY_TASK","https://oss-cool.coolstore.cn/notice_pic/cfa059a851a84c788134f533b3d5e279.jpg"),
    PATROL_STORE_INFORMATION("PATROL_STORE_INFORMATION","https://oss-cool.coolstore.cn/notice_pic/46bde08f49614d8ba1fa238c07dde2d0.png"),
    PATROL_STORE_OFFLINE("PATROL_STORE_OFFLINE","https://oss-cool.coolstore.cn/notice_pic/9a359fcac8114575a75d042a9de218b9.png"),
    PATROL_STORE_ONLINE("PATROL_STORE_ONLINE","https://oss-cool.coolstore.cn/notice_pic/9a359fcac8114575a75d042a9de218b9.png"),
    PATROL_STORE_PICTURE_ONLINE("PATROL_STORE_PICTURE_ONLINE","https://oss-cool.coolstore.cn/notice_pic/9a359fcac8114575a75d042a9de218b9.png"),
    PATROL_STORE_AI("PATROL_STORE_AI","https://oss-cool.coolstore.cn/notice_pic/9a359fcac8114575a75d042a9de218b9.png"),
    PATROL_STORE_PLAN("PATROL_STORE_PLAN","https://oss-cool.coolstore.cn/notice_pic/9a359fcac8114575a75d042a9de218b9.png"),
    STORE_WORK_DAY("STORE_WORK_DAY","https://oss-cool.coolstore.cn/notice_pic/42b69c2f8037464c8fb4df8b3ffe6317.png"),
    STORE_WORK_WEEK("STORE_WORK_WEEK","https://oss-cool.coolstore.cn/notice_pic/89df9fb2f6e14fcd97dd1501555369f0.png"),
    STORE_WORK_MONTH("STORE_WORK_MONTH","https://oss-cool.coolstore.cn/notice_pic/275cd9d2095a44d58c5f1939acdb5b07.png"),
    SUPERVISION("SUPERVISION","https://oss-cool.coolstore.cn/notice_pic/18ac15eacef0aaf6374e7554920cd699.png"),
    PATROL_STORE_REPORT("PATROL_STORE_REPORT","https://oss-cool.coolstore.cn/notice_pic/f1091d82e953472c9138eaf139b327aa.png"),
    PATROL_STORE_SAFETY_CHECK("PATROL_STORE_SAFETY_CHECK","https://oss-cool.coolstore.cn/notice_pic/a729a5c125f941cdad28fe029a628229.png"),
    SELF_PATROL_STORE("SELF_PATROL_STORE","https://oss-cool.coolstore.cn/notice_pic/b46178d60e27428482c53ddc0e006150.png"),
    ACHIEVEMENT_NEW_RELEASE("ACHIEVEMENT_NEW_RELEASE","https://oss-cool.coolstore.cn/notice_pic/1aae0c02a39c4db2a16aa7fbbc0cbdf2.png"),
    ACHIEVEMENT_OLD_PRODUCTS_OFF("ACHIEVEMENT_OLD_PRODUCTS_OFF","https://oss-cool.coolstore.cn/notice_pic/cancelsample.png"),
    AI_ANALYSIS_REPORT("AI_ANALYSIS_REPORT","https://oss-cool.coolstore.cn/notice_pic/aireport.png"),



    ;

    private static final Map<String, String> map = Arrays.stream(values()).collect(
            Collectors.toMap(UnifyTaskPicUrlEnum::getCode, UnifyTaskPicUrlEnum::getDesc));


    private String code;
    private String desc;

    UnifyTaskPicUrlEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getByCode(String code) {
        return map.get(code);
    }

}
