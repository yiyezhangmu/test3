package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.unifytask.query.TaskQuestionQuery;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author suzhuhong
 * @Date 2022/9/6 16:34
 * @Version 1.0
 */
@Data
public class ExportPatrolStoreDetailRequest extends ExportBaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    PatrolStoreDetailRequest request;
}
