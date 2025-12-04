package com.coolcollege.intelligent.service.metatable;

import com.coolcollege.intelligent.model.metatable.*;
import com.coolcollege.intelligent.model.metatable.dto.*;
import com.coolcollege.intelligent.model.metatable.request.*;
import com.coolcollege.intelligent.model.metatable.response.MetaTableMetaColumnResp;
import com.coolcollege.intelligent.model.metatable.vo.*;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableDTO;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableQuery;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskShowVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.UnifyTbDisplayTableDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import javax.validation.Valid;
import java.util.List;

public interface TbMetaTableService {
    /**
     * 检查表列表
     * @param enterpriseId
     * @param name
     * @param tableType
     * @param user
     * @param pageNumber
     * @param pageSize
     * @param tableIdList
     * @param isAll
     * @param isResultPerson
     * @param bothPerson
     * @Param queryCondition 使用中using   归档pigeonhole  已删除 deleted
     * @return
     */
    PageInfo getList(String enterpriseId, String name, String tableType, CurrentUser user, List<Long>tableIdList, Boolean isAll,String tableProperty);


    PageInfo getListV2(String enterpriseId, CurrentUser user, TablePageRequest request);


    PageInfo getTableListByResultViewV2(String enterpriseId, CurrentUser user, TablePageRequest request);

    /**
     * 检查表详情页查询 自定义检查表与标准检查表通用接口
     *
     * @param enterpriseId
     * @param id
     * @return
     */
    TbDetailStaVO getDetailById(String enterpriseId, Long id);

    MetaStaTableVO getMetaTableDetail(String enterpriseId, Long id, String userId, Boolean isFilterFreezeColumn);

    /**
     * 标准检查表新增
     *
     * @param enterpriseId
     * @param user
     *            当前用户
     * @param metaStaTableDTO
     *            入参
     * @return
     */
    TbMetaTableDO saveSta(String enterpriseId, CurrentUser user, TbMetaStaTableDTO metaStaTableDTO);

    /**
     * 标准检查表编辑 1.只有未锁定的检查表可以编辑 2.未锁定的检查表修改、检查项可以删除重新建 3，保留旧的创建时间
     *
     * @param enterpriseId
     * @param user
     * @param metaStaTableDTO
     * @return
     */
    TbMetaTableDO updateSta(String enterpriseId, CurrentUser user, TbMetaStaTableDTO metaStaTableDTO);

    /**
     * 检查表复制
     *
     * @param enterpriseId
     * @param user
     * @param metaTableId
     * @return
     */
    TbMetaTableDO copySta(String enterpriseId, CurrentUser user, Long metaTableId);

    /**
     * 批量删除 标准检查表信息
     *
     * @param enterpriseId
     * @param user
     * @param metaTableIds
     * @return
     */
    boolean delSta(String enterpriseId, CurrentUser user, List<Long> metaTableIds);

    /**
     * 移动端获取检查表列表
     *
     * @param enterpriseId
     * @param user
     * @param tableType
     * @param limitNum
     * @return
     */
    List<TbMetaTableDO> getSimpleMetaTableList(String enterpriseId, CurrentUser user, String tableType,
        Integer limitNum);

    /**
     * 通过子任务id获取检查表样式详情
     * @param enterpriseId 企业id
     * @param subTaskIdList 子任务id列表
     * @return
     */
    List<TbMetaTableRecordVO> getTableListBySubTaskId(String enterpriseId, Long subTaskIdList);

    /**
     * 判断表是不是标准检查表
     *
     * @param enterpriseId
     * @param metaTableId
     * @return
     */
    Boolean isStaTable(String enterpriseId, Long metaTableId);

    /**
     * 判断是创建者或者管理员
     *
     * @param enterpriseId
     * @param userId
     * @return
     */
    boolean isCreatorOrAdmin(String enterpriseId, String userId, List<Long> metaTableIds);

    /**
     * 判断是否是共同编辑人
     *
     * @param enterpriseId
     * @param userId
     * @return
     */
    boolean isCommonEditUser(String enterpriseId, String userId, Long metaTableId);

    /**
     * 配置自定义检查表
     *
     * @param enterpriseId
     * @param user
     * @param param
     * @return
     */
    Long configMetaDefTable(String enterpriseId, CurrentUser user, ConfigMetaDefTableParam param);

    /**
     * 获取自定义检查表信息
     *
     * @param enterpriseId
     * @param metaTableId
     * @return
     */
    TbMetaDefTableVO getMetaDefTable(String enterpriseId, Long metaTableId, String userId);

    /**
     * 复制自定义检查表
     */
    Long copyMetaDefTable(String enterpriseId, CurrentUser user, Long metaTableId);

    /** 删除自定义检查表 */
    boolean delMetaDefTable(String enterpriseId, CurrentUser user, List<Long> metaTableIds);

    List<ColumnCategoryDTO> getTableColumnCategory(String enterpriseId, Long tableId);

    TbDetailStaVO getTableDetailByIdAndCategory(String enterpriseId, Long tableId, String category);

    MetaTableMetaColumnResp getMetaTableMetaColumn(String enterpriseId, List<Long> tableIdList);

    List<MetaStaColumnVO> getStaColumnTailById(String enterpriseId, List<Long> idList);

    List<ColumnCategoryVO> getColumnGroupByCategory(String enterpriseId, Long tableId);

    /**
     * 设置名字数据
     */
    void setNameData(String enterpriseId, List<TbMetaStaTableColumnDO> metaStaColumnList);

    TbMetaTableDO addOrUpdateDisplayMetaTable(String enterpriseId, CurrentUser user, TbDisplayTableDTO displayTableDTO);

    /**
     * 批量删除 标准检查表信息
     *
     * @param enterpriseId
     * @param user
     * @param metaTableIds
     * @return
     */
    boolean delTbDisplay(String enterpriseId, CurrentUser user, List<Long> metaTableIds);

    UnifyTbDisplayTableDTO displayMetaTableDetailList(String enterpriseId, TbDisplayTableQuery query);

    TbDisplayTaskShowVO getQuickColumnIdListByMetaTableId(String enterpriseId, Long metaTableId);

    int updateLockedByIds( String enterpriseId,  List<Long> metaTableIds);

    /**
     * 判断是否拥有结果可见权限
     * @param enterpriseId 企业id
     * @param metaTableId 检查表id
     * @param userId 用户id
     * @return
     */
    Boolean hasResultAuth(String enterpriseId, Long metaTableId, String userId);

    /**
     * 升级高级检查表校验
     * @param enterpriseId
     * @param tableId
     * @return
     */
    Boolean raiseCheck(String enterpriseId, Long tableId);

    /**
     * 检查表升级为高级检查表
     * @param enterpriseId
     * @param id
     * @param user
     */
    void raiseStaTable(String enterpriseId, Long id, CurrentUser user);

    /**
     * 获得所有的检查表
     * @param enterpriseId
     * @param tableType
     * @param metaTableIds
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.metatable.TbMetaTableDO>
     * @date: 2022/3/4 17:46
     */
    List<TbMetaTableDO> getAll(String enterpriseId, String tableType, List<Long> metaTableIds);

    /**
     * 获得所有的检查表
     * @param enterpriseId
     * @param metaTableId
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.metatable.TbMetaTableDO>
     * @date: 2022/3/4 17:46
     */
    List<MetaTableColumnSimpleVO> getAllColumnByTableId(String enterpriseId, Long metaTableId);

    MetaStaTableVO addStaMetaTableByTemplate(String enterpriseId, Long metaTableTemplateId, CurrentUser user);

    List<TbMetaColumnResultDTO> getMetaColumnResultList(String enterpriseId, List<TbMetaColumnResultDO> columnResultDOList);

    /**
     * 更新表user相关字段
     * @param enterpriseId
     */
    void updateMetaTableUser(String enterpriseId, List<Long> metaTableIds);


    List<SopDTO> sopTreeList(String enterpriseId, CurrentUser user, SopTreeListRequest param);

    /**
     * 新建sop分组节点
     * @param enterpriseId
     * @param user
     * @param param
     * @return
     */
    boolean addSopNode(String enterpriseId, CurrentUser user, AddSopNodeRequest param);

    String updateSopNode(String enterpriseId, CurrentUser user, UpdateSopNodeRequest param);

    SopGroupDTO sopNodeDetail(String enterpriseId, Long id);

    boolean deleteSopNode(String enterpriseId, CurrentUser user, Long id);

    boolean moveSopNode(String enterpriseId, CurrentUser user, List<Long> ids, long pid);

    TbMetaQuickColumnResultDO findColumnManAndMin(String enterpriseId, Long columnId);

    /**
     * 获取使用人包含用户id的陈列检查表
     * @param enterpriseId 企业id
     * @param userId 用户id
     * @param name 陈列表名
     * @param startTime 开始时间，yyyy-MM-dd，左闭右开
     * @param endTime 结束时间，yyyy-MM-dd
     * @return java.util.List<com.coolcollege.intelligent.model.metatable.TbMetaTableDO>
     */
    List<TbMetaTableDO> getDisplayTableAndUsedUserContainUserId(String enterpriseId, String userId, String name, String startTime, String endTime);

    /**
     * 更新创建者
     * @param enterpriseId
     * @param request
     * @return
     */
    Boolean updateTableCreateUser(String enterpriseId, UpdateTableCreateUserRequest request);
}
