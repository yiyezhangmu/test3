package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.unifytask.query.TaskQuestionQuery;
import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 */
@Data
public class ExportTaskQuestionRequest extends ExportBaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    TaskQuestionQuery request;
}
