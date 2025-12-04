package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/28 21:03
 */
public enum UnifyNodeEnum  {

    /**
     * 任务类型
     */

    CC("cc", "抄送"),
    ZERO_NODE("0", "创建"),
    FIRST_NODE("1", "待整改"),
    SECOND_NODE("2", "待审批"),
    THIRD_NODE("3", "待复核"),
    FOUR_NODE("4", "审批节点"),
    FIVE_NODE("5", "审批节点"),
    SIX_NODE("6", "审批节点"),
    END_NODE("endNode", "已完成"),


    NOTICE("notice", "报告通知人"),
    ;

    private static final Map<String, UnifyNodeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UnifyNodeEnum::getCode, Function.identity()));


    private final String code;
    private final String desc;

    UnifyNodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UnifyNodeEnum getByCode(String code) {
        return map.get(code);
    }

    public static boolean isApproveNode(String nodeNo){
        if(SECOND_NODE.code.equals(nodeNo) || THIRD_NODE.code.equals(nodeNo)
                || FOUR_NODE.code.equals(nodeNo) || FIVE_NODE.code.equals(nodeNo) || SIX_NODE.code.equals(nodeNo)){
            return true;
        }
        return false;
    }

    public static List<String> getApproveNoList(){
        return Arrays.asList(SECOND_NODE.getCode(), THIRD_NODE.getCode(), FOUR_NODE.getCode(), FIVE_NODE.getCode(), SIX_NODE.getCode());
    }

    public static List<String> isHandleNodeList(){
        return Arrays.asList(FIRST_NODE.getCode(), SECOND_NODE.getCode(), THIRD_NODE.getCode(), FOUR_NODE.getCode(), FIVE_NODE.getCode(), SIX_NODE.getCode());
    }

    public static boolean isHandleNode(String nodeNo) {
        return (FIRST_NODE.code.equals(nodeNo)
                || SECOND_NODE.code.equals(nodeNo)
                || THIRD_NODE.code.equals(nodeNo)
                || FOUR_NODE.code.equals(nodeNo)
                || FIVE_NODE.code.equals(nodeNo)
                || SIX_NODE.code.equals(nodeNo));
    }

    public static String getNodeName(String nodeNo){
        if(FIRST_NODE.code.equals(nodeNo)){
            return "待处理";
        }
        if(SECOND_NODE.code.equals(nodeNo)){
            return "待审批(一级审批)";
        }
        if(THIRD_NODE.code.equals(nodeNo)){
            return "待审批(二级审批)";
        }
        if(FOUR_NODE.code.equals(nodeNo)){
            return "待审批(三级审批)";
        }
        if(FIVE_NODE.code.equals(nodeNo)){
            return "待审批(四级审批)";
        }
        if(SIX_NODE.code.equals(nodeNo)){
            return "待审批(五级审批)";
        }
        if(END_NODE.code.equals(nodeNo)){
            return "已完成";
        }
        return "";
    }
}

