package com.coolcollege.intelligent.common.enums.Aliyun;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/23
 */
public enum  PersonGroupEnum {
    /**
     * 企业会员
     */
    VIP("1000000", "企业会员"),

    CLERK("2000000", "企业店员"),
    BLACKLIST("3000000", "黑名单"),
    TICKET_MONGER("4000000", "黄牛"),
    THIEF("5000000", "惯偷");


    private String groupId;
    private String msg;

    protected static final Map<String, PersonGroupEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(PersonGroupEnum::getGroupId, Function.identity()));

    PersonGroupEnum(String groupId,String msg){
        this.groupId=groupId;
        this.msg=msg;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getMsg() {
        return msg;
    }
    public static PersonGroupEnum getByCode(String groupId) {
        return map.get(groupId);
    }
}
