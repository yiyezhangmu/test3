package com.coolcollege.intelligent.model.achievement.qyy.vo.ak;

import lombok.Data;

import java.text.NumberFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class SendRecordInfoVO {


    /**
     * 自增id
     */
    private Long id;

    /**
     * 场景卡片名称
     */
    private String sceneCardName;

    /**
     * 群名称
     */
    private String conversationTitle;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 发送状态
     */
    private String sendStatus;

    /**
     * 接收人数
     */
    private Integer receiveNum;

    /**
     * 已读人数
     */
    private Integer readNum;

    /**
     * 群人数
     */
    private Integer memberNum;

    /**
     * 已读占比
     */
    private String readPercent;

    public String getReadPercent() {
        if (receiveNum <= 0) {
            return "100%";
        }
        return NumberFormat.getPercentInstance().format((readNum * 1d) / receiveNum);
    }

    private List<CardDataDetailVO> detailList;

    @Data
    public static class CardDataDetailVO {

        /**
         * 员工姓名
         */
        private String userName;
        /**
         * 部门
         */
        private String deptName;

        /**
         * 角色
         */
        private String roleName;

        /**
         * 阅读状态
         */
        private String readStatusStr;

        /**
         * 阅读时间
         */
        private Date readTime;

        public static Map<String, String> getExportHeaderAlias() {
            Map<String, String> headerAlias = new LinkedHashMap<>(16);
            headerAlias.put("userName", "员工姓名");
            headerAlias.put("deptName", "部门");
            headerAlias.put("roleName", "角色");
            headerAlias.put("readStatusStr", "阅读状态");
            return headerAlias;
        }
    }


}
