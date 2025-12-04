package com.coolcollege.intelligent.service.sop;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.model.sop.TaskSopDO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopClassifyDTO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopClassifySelectDTO;
import com.coolcollege.intelligent.model.sop.query.TaskSopQuery;
import com.coolcollege.intelligent.model.sop.vo.TaskSopListVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/2/20 16:56
 */
public interface TaskSopService {

    /**
     * 新增sop
     * @param eid
     * @param sop
     * @return
     */
    @Deprecated
    Boolean insertSop(String eid, TaskSopVO sop);

    /**
     * 新增sop
     * @param eid
     * @param sop
     * @return
     */
    TaskSopDO insertSopInfo(String eid, TaskSopVO sop);

    /**
     * 批量添加sop文档
     * @param eid 企业id
     * @param sop 文档信息
     * @param user 当前用户
     * @author: xugangkun
     * @return java.lang.Boolean
     * @date: 2021/9/17 15:50
     */
    Boolean batchInsertSop(String eid, TaskSopListVO sop, CurrentUser user);

    /**
     * 更新文档可见人权限
     * @param eid 企业id
     * @param sop 文档信息
     * @param user 当前用户
     * @author: xugangkun
     * @return java.lang.Boolean
     * @date: 2021/9/17 15:50
     */
    Boolean updateSopVisibleUser(String eid, TaskSopListVO sop, CurrentUser user);

    /**
     * 获取sop列表
     * @param eid
     * @param query
     * @return
     */
    PageVO selectTaskSopList(String eid, TaskSopQuery query, CurrentUser user);

    List<TaskSopVO> listByIdList(String enterpriseId, List<Long> sopIdList);

    TaskSopVO getSopById(String enterpriseId, Long id);

    void batchDeleteSop(String enterpriseId, List<Long> sopIdList, CurrentUser user);
    /**
     * 新增sop分类
     * @param eid
     * @param classifyName
     * @return
     */
    Boolean addSopClassify(String eid, String classifyName);

    /**
     * 修改sop分类
     * @param eid
     * @param classify
     * @return
     */
    Boolean updateSopClassify(String eid, TaskSopClassifyDTO classify);

    /**
     * 获取sop分类列表
     * @param eid
     * @return
     */
    List<TaskSopClassifySelectDTO> selectSopClassifyList(String eid);

    List<String> selectAllCategory(String enterpriseId);


    /**
     * 添加督导助手文档
     * @param eid
     * @param sop
     * @param user
     * @return
     */
    List<TaskSopDO> batchInsertSupervisionSop(String eid, TaskSopListVO sop, CurrentUser user);

    /**
     * ds 初始化后更新定制sop文件url
     * @param enterpriseId
     * @param sops
     */
    Boolean updateSopUrl(String enterpriseId,  List<TaskSopDO> sops);

    /**
     * 查询使用人包含用户id的陈列SOP
     * @param enterpriseId 企业id
     * @param userId 用户id
     * @param name sop名称
     * @param startTime 开始时间，yyyy-MM-dd，左闭右开
     * @param endTime 结束时间，yyyy-MM-dd
     * @return java.util.List<com.coolcollege.intelligent.model.sop.TaskSopDO>
     */
    List<TaskSopDO> getDisplaySopAndUsedUserContainUserId(String enterpriseId, String userId, String name, String startTime, String endTime);
}
