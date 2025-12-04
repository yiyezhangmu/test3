package com.coolcollege.intelligent.service.supervison;

import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionDefDataColumnDTO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionDataColumnVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionStoreTaskDetailVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/28 15:35
 * @Version 1.0
 */
public interface SupervisionDefDataColumnService {


    /**
     * 根据子任务ID或者门店任务ID 与 类型查询数据表列表
     * @param enterpriseId
     * @param taskIds
     * @param type
     * @return
     */
    List<SupervisionDefDataColumnDTO> getDataColumnListByTaskIdAndType(String enterpriseId, List<Long> taskIds, String type);


    /**
     * 查询表单模板与数据项
     * @param enterpriseId
     * @param taskId
     * @param formId
     * @param type
     * @return
     */
    SupervisionDataColumnVO getSupervisionDataColumn(String enterpriseId,Long taskId,String formId,String type);

}
