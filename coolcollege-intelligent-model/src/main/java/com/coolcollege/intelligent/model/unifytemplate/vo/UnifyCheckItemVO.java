package com.coolcollege.intelligent.model.unifytemplate.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : lz
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/18 16:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyCheckItemVO {
    private Long snapshotId;
    private Long id;
    private String name;

    /**
     * 图片链接
     */
    private String picUrl;
    private String description;
    /**
     * 上传图片的url
     */
    private String editPicUrl;
    private Long createTime;
    private String createUserId;
    private Long updateTime;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;
    private Integer deleteIs;

}
