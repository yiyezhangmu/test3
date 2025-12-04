package com.coolcollege.intelligent.model.metatable.vo;

import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import lombok.Data;

@Data
public class TbMetaQuickColumnVO extends TbMetaQuickColumnDO {

    private TaskSopVO taskSopVO;

    private CoolCourseVO coolCourseVO;

    private CoolCourseVO freeCourseVO;

}
