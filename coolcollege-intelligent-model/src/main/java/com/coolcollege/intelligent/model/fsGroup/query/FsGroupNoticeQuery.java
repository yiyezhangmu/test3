package com.coolcollege.intelligent.model.fsGroup.query;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群公告表(FsGroupNotice)实体类
 *
 * @author CFJ
 * @since 2024-05-06 11:32:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupNoticeQuery extends PageBaseRequest {

    /**
     * 公告名称
     */    
    @ApiModelProperty("公告名称")
    private String name;

    @ApiModelProperty("'0:未发送1:已发送'")
    private String hasSend;


}

