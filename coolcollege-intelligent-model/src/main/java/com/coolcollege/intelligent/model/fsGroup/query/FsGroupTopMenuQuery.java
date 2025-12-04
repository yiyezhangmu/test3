package com.coolcollege.intelligent.model.fsGroup.query;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书群置顶表(FsGroupTopMsg)实体类
 *
 * @author CFJ
 * @since 2024-05-06 10:59:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupTopMenuQuery extends PageBaseRequest {


    private String topName;
}

