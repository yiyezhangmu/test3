package com.coolcollege.intelligent.model.qywx.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 模板消息实体
 * @author ：xugangkun
 * @date ：2021/12/7 10:01
 */
@Data
public class TemplateMsgDTO {

    /**
     * 企业id
     */
    @NotBlank(message = "企业id不能为空")
    private String eid;

    /**
     * 用户ticket
     */
    @NotNull(message = "用户ticket不能为空")
    private List<String> selectedTicketList;

    /**
     * 用户列表
     */
    private List<String> userList;

    /**
     * 企业类型
     */
    private String appType;

    /**
     * 企业corpId
     */
    private String corpId;

}
