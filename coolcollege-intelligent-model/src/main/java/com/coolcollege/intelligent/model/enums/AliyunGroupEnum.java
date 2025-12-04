package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/8/22 11:18
 */
public enum AliyunGroupEnum {

    // 租户分组统计
    DAY_ALL("tag_corpgroup_day_personnumber", "某品牌时间段内每天人流量 "),
    // 租户统计
    CORP_DAY_ALL("tag_corp_day_personnumber", "某品牌时间段内每天人流量 "),
    HOUR_ALL("tag_corpgroup_hour_personnumber", "某品牌时间段内每小时人流量（分组） "),
    CORP_HOUR_ALL("tag_corp_hour_personnumber", "某品牌时间段内每小时人流量 "),
    PERSON_DAY("tag_corpgroup_day_personvisits", "某品牌时间段内每天每个客户访问次数"),
    CROP_PERSON_DAY("tag_corp_day_personvisits", "某品牌时间段内每天每个客户访问次数"),
    PERSON_HOUR("tag_corpgroup_hour_personvisits", "某品牌时间段内每小时每个客户访问次数"),
    CORP_PERSON_HOUR("tag_corp_hour_personvisits", "某品牌时间段内每小时每个客户访问次数"),
    PERSON_DAY_DST("tag_corpgroup_day_personvisits_distribution", "某品牌时间段内每天人次分布"),
    PERSON_HOUR_DST("tag_corpgroup_hour_personvisits_distribution", "某品牌时间段内每小时每个客户访问次数分布"),
    CORP_PERSON_DAY_DST("tag_corp_day_personvisits_distribution", "某品牌时间段内每天人次分布"),
    CROP_PERSON_HOUR_DST("tag_corp_hour_personvisits_distribution", "某品牌时间段内每小时每个客户访问次数分布"),
    // 基于用户上传用户和用户分组信息，比如会员，店员，黄牛，商城工作人员等，在统计客流时，分别对不同用户分组的用户进行统计，如果不在任何分组，则返回为默认分组。
    USER_DAY_COUNT("tag_corpgroup_day_usergroup_personcount", "用户分组客流统计（每天）"),
    CORP_USER_DAY_COUNT("tag_corp_day_usergroup_personcount", "租户用户分组客流统计（每天）"),
    // 基于用户上传用户和用户分组信息，比如会员，店员，黄牛，商城工作人员等，在统计客流时，分别对不同用户分组的用户进行统计，如果不在任何分组，则返回为默认分组。
    USER_HOUR_COUNT("tag_corpgroup_hour_usergroup_personcount", "用户分组客流统计（小时）"),
    CORP_USER_HOUR_COUNT("tag_corp_hour_usergroup_personcount", "用户租户客流统计（小时）"),
    // 基于租户分组统计客流的性别分布
    GENDER("tag_corpgroup_day_gender_distribution", "每天性别分布"),
    CORP_GENDER("tag_corp_day_gender_distribution", "每天性别分布"),
    AGE("tag_corpgroup_day_age_distribution", "用户年龄分布"),
    CORP_AGE("tag_corp_day_age_distribution", "用户年龄分布"),
    ;

    private static final Map<String, AliyunGroupEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(AliyunGroupEnum::getCode, Function.identity()));

    AliyunGroupEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AliyunGroupEnum getByCode(String code) {
        return map.get(code);
    }
}
