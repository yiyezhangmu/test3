package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.query.TaskFinishStorePageRequest;
import com.coolcollege.intelligent.model.unifytask.query.TaskReportQuery;
import com.coolcollege.intelligent.model.unifytask.vo.TaskFinishStoreVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportExportBaseVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportExportVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportVO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wxp
 * @date 2021/6/22
 */
public interface UnifyTaskReportService {

    /**
     * 巡店任务报表
     */
    PageInfo listTaskReport(String enterpriseId, TaskReportQuery query);

    /**
     * 巡店任务报表导出
     */
    ImportTaskDO taskReportExport(String enterpriseId, TaskReportQuery query);

    List<TaskReportExportVO> translateToExportVO(List<TaskReportVO> taskReportVOList);

    List<TaskReportExportBaseVO> translateToExportBaseVO(List<TaskReportVO> taskReportVOList);

    Map<Long, List<GeneralDTO>> getTaskStoreRange(String enterpriseId, List<Long> taskIdList);

    /**
     * 获取任务 门店列表
     * @param enterpriseId
     * @param query
     * @return
     */
    PageInfo<TaskFinishStoreVO> getTaskFinishStorePage(String enterpriseId, TaskFinishStorePageRequest query);
}
