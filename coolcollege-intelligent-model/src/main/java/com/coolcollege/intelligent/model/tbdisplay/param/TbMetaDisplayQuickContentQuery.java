package com.coolcollege.intelligent.model.tbdisplay.param;

import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayQuickColumnDO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/9/26 19:11
 * @Version 1.0
 */
@Data
public class TbMetaDisplayQuickContentQuery {

    /**
     * 创建者
     */
    private String createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新人姓名
     */
    private String editUserName;

    /**
     * 更新人id
     */
    private String editUserId;
    /**
     * 检查类型
     */
    private Integer checkType;

    /**
     * 检查内容列表
     */
    private List<TbMetaDisplayQuickColumnDO> contentList;

}
