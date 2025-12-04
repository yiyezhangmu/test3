package com.coolcollege.intelligent.dao.sop;

import com.coolcollege.intelligent.model.sop.TaskSopClassifyDO;
import com.coolcollege.intelligent.model.sop.TaskSopDO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopClassifyDTO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopClassifySelectDTO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopDTO;
import com.coolcollege.intelligent.model.sop.query.TaskSopQuery;
import com.coolcollege.intelligent.model.sop.vo.TaskSopListVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/2/20 16:22
 */
@Mapper
public interface TaskSopMapper {

    /**
     * 新增sop
     * @param eid
     * @param sop
     */
    void addSopFile(@Param("eid") String eid, @Param("sop")TaskSopDO sop);

    /**
     * 批量添加企业id
     * @param eid 企业id
     * @param sops sop文档列表
     * @author: xugangkun
     * @return void
     * @date: 2021/9/17 16:17
     */
    void batchInsertSop(@Param("eid") String eid, @Param("sops")List<TaskSopDO> sops);

    /**
     * 修改sop文档的可见范围
     * @param eid 企业id
     * @param ids sop文档id列表
     * @param sop sop文档信息
     * @author: xugangkun
     * @return void
     * @date: 2021/9/22 10:58
     */
    void updateSopVisibleUser(@Param("eid") String eid, @Param("ids")List<Long> ids, @Param("sop")TaskSopListVO sop);

    /**
     * 获取sop列表
     * @param eid
     * @param query
     * @return
     */
    List<TaskSopDTO> selectTaskSopList(@Param("eid") String eid, @Param("query")TaskSopQuery query);

    /**
     * 获取所有sop文档
     * @param eid
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.sop.dto.TaskSopDTO>
     * @date: 2021/9/24 16:29
     */
    List<TaskSopDTO> selectAllTaskSop(@Param("eid") String eid, @Param("query")TaskSopQuery query);

    List<TaskSopVO> listByIdList(@Param("enterpriseId") String enterpriseId, @Param("sopIdList") List<Long> sopIdList);

    TaskSopVO getSopById(@Param("enterpriseId") String enterpriseId,
                         @Param("id") Long id);

    /**
     * 批量删除Sop
     * @param enterpriseId
     * @param sopIdList
     */
    void batchDeleteSop(@Param("eId") String enterpriseId, @Param("sopIdList") List<Long> sopIdList);

    /**
     * 新增sop分类
     * @param eid
     * @param classify
     */
    void addSopClassify(@Param("eid") String eid, @Param("classify") TaskSopClassifyDO classify);

    /**
     * 更新sop分类
     * @param eid
     * @param classify
     */
    void updateSopClassify(@Param("eid") String eid, @Param("classify") TaskSopClassifyDTO classify);

    /**
     * 获取sop分类列表
     * @param eid
     * @return
     */
    List<TaskSopClassifySelectDTO> selectSopClassifyList(@Param("eid") String eid);

    List<String> selectAllCategory(@Param("enterpriseId") String enterpriseId);

    /**
     * 查询sop数量
     * @param enterpriseId
     * @return
     */
    Integer count(@Param("enterpriseId") String enterpriseId);

    List<TaskSopDO> getAllSop(@Param("eid") String eid);

    /**
     *
     * @param eid
     * @param sops
     * @return
     */
    Integer copySop(@Param("eid") String eid, @Param("sops")List<TaskSopDO> sops);

    Integer deleteAllSop(@Param("eid") String eid);

    void batchUpdateVideoUrl(@Param("enterpriseId") String enterpriseId, @Param("sops") List<TaskSopDO> sops);

    /**
     * ds 初始化后更新定制sop文件url
     * @param  enterpriseId
     * @param sops
     */
    void updateSopUrl(@Param("enterpriseId") String enterpriseId, @Param("sops") List<TaskSopDO> sops);

    /**
     * 查询使用人包含用户id的陈列SOP
     * @param enterpriseId 企业id
     * @param userId 用户id
     * @param name sop名称
     * @param startTime 开始时间，yyyy-MM-dd，左闭右开
     * @param endTime 结束时间，yyyy-MM-dd
     * @return java.util.List<com.coolcollege.intelligent.model.sop.TaskSopDO>
     */
    List<TaskSopDO> selectDisplaySopAndUsedUserContainUserId(
            @Param("enterpriseId") String enterpriseId,
            @Param("userId") String userId,
            @Param("name") String name,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
}
