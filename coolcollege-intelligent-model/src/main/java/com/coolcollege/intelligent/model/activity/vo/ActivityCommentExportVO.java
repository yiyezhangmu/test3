package com.coolcollege.intelligent.model.activity.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2023/7/10 14:39
 * @Version 1.0
 */
@Data
public class ActivityCommentExportVO {

    @Excel(name = "活动主题",orderNum = "1")
    private String activityTitle;
    @Excel(name = "评论人名称",orderNum = "2")
    private String commentName;
    @Excel(name = "员工工号",orderNum = "3")
    private String jobNumber;
    @Excel(name = "评论内容",orderNum = "4")
    private String content;
    @Excel(name = "图片/视频",orderNum = "5")
    private String picOrVideo;
    @Excel(name = "楼层",orderNum = "6")
    private Integer floor;
    @Excel(name = "评论时间",orderNum = "7" , format = "yyyy-MM-dd HH:mm:ss")
    private Date commentTime;
    @Excel(name = "所属部门",orderNum = "7")
    private String fullRegionPathName;
    @Excel(name = "所属分公司",orderNum = "8")
    private String thirdDept;
    @Excel(name = "直属部门/门店",orderNum = "9")
    private String deptName;
    @Excel(name = "门店编码",orderNum = "10")
    private String storeNum;
    @Excel(name = "点赞数量",orderNum = "11")
    private Integer likeCount;
    @Excel(name = "回复数量",orderNum = "12")
    private Integer replyCount;
    @Excel(name = "回复内容（取第一条回复内容）",orderNum = "13")
    private String replyContent;
    @Excel(name = "回复人姓名",orderNum = "14")
    private String replyUserName;
    @Excel(name = "回复人工号",orderNum = "15")
    private String replyJobNumber;
    @Excel(name = "回复时间",orderNum = "16", format = "yyyy-MM-dd HH:mm:ss")
    private Date replyTime;
    @Excel(name = "回复人部门",orderNum = "17")
    private String replyDept;

}
