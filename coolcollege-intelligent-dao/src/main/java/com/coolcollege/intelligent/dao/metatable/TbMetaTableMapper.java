package com.coolcollege.intelligent.dao.metatable;

import java.util.*;

import com.coolcollege.intelligent.model.metatable.dto.SopDTO;
import com.coolcollege.intelligent.model.metatable.request.AddSopNodeRequest;
import com.coolcollege.intelligent.model.metatable.request.TablePageRequest;
import com.github.pagehelper.Page;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsMetaTableQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * PatrolMetaTableMapper继承基类
 */
@Mapper
public interface TbMetaTableMapper {


    int insertSelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaTableDO record);

    /**
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-04-14 08:34
     */
    TbMetaTableDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-04-14 08:34
     */
    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaTableDO record);


    int updateLockedByIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> ids);

    int updateDelByIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> ids);

    List<TbMetaTableDO> selectByIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> ids);

    List<TbMetaTableDO> selectList(@Param("enterpriseId") String enterpriseId,
                                   @Param("name") String name,
                                   @Param("tableTypeList") List<String> tableTypeList,
                                   @Param("userId") String userId,
                                   @Param("adminIs") Boolean adminIs,
                                   @Param("tableIdList") List<Long> tableIdList,
                                   @Param("isAll") Boolean isAll,
                                   @Param("tablePropertyList") List<String> tablePropertyList,
                                   @Param("statusFilterCondition") String statusFilterCondition);


    List<TbMetaTableDO> selectListV2(@Param("enterpriseId") String enterpriseId, @Param("param") TablePageRequest param);


    Integer isStaTable(@Param("enterpriseId") String enterpriseId, @Param("metaTableId") Long metaTableId);

    /**
     * 判断是不是创建人
     *
     * @param enterpriseId
     * @param metaTableIds
     * @param userId
     * @return
     */
    Integer isCreator(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> metaTableIds,
                      @Param("userId") String userId);

    int insertTable(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaTableDO record);

    int update(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaTableDO record);

    TbMetaTableDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);


    List<TbMetaTableDO> selectByIdsAndType(@Param("enterpriseId") String enterpriseId, @Param("tableIdList") List<Long> tableIdList, @Param("tableType") String tableType);

    /**
     * 物理删除
     *
     * @param enterpriseId
     * @param deleteIdList
     */
    void deleteByIdList(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> deleteIdList);

    /**
     * 通过创建人id获取列表
     *
     * @param enterpriseId
     * @param userIdList
     * @param beginDate
     * @param endDate
     * @return
     */
    List<TbMetaTableDO> getTableByCreateUserId(@Param("enterpriseId") String enterpriseId, @Param("userIdList") List<String> userIdList, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 检查表数量
     */
    Integer count(@Param("enterpriseId") String enterpriseId, @Param("tableType") String tableType, @Param("status") Integer status);

    List<TbMetaTableDO> getAll(@Param("enterpriseId") String enterpriseId, @Param("tableType") String tableType, @Param("metaTableIds") List<Long> metaTableIds);

    TbMetaTableDO getInitTable(@Param("enterpriseId") String enterpriseId, @Param("tableType") String tableType);

    List<TbMetaTableDO> getDefaultMetaTable(@Param("enterpriseId") String enterpriseId, @Param("limit") int limit);

    Long countExport(@Param("enterpriseId") String enterpriseId, @Param("type") String type, @Param("tableIds") List<Long> tableIds);

    List<TbMetaTableDO> selectByMetaTableIdListAll(@Param("enterpriseId") String enterpriseId, @Param("tableType") String tableType, @Param("metaTableIds") List<Long> metaTableIds);

    /**
     * 标准检查表升级为高级检查表
     *
     * @param enterpriseId
     * @param id
     */
    void raiseStaTable(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 更新检查表置顶或者是否归档
     *
     * @param enterpriseId
     * @param id
     * @param topStatus
     * @param topTime
     * @param pigeonholeStatus
     */
    void updateTopOrPigeonhole(@Param("enterpriseId") String enterpriseId,
                               @Param("id") Long id,
                               @Param("topStatus") Boolean topStatus,
                               @Param("topTime") Date topTime,
                               @Param("pigeonholeStatus") Integer pigeonholeStatus);

    void pigeonholeMany(@Param("enterpriseId") String enterpriseId,
                        @Param("id") List<Long> id);

    /**
     * 更新检查表的排序
     *
     * @param enterpriseId
     * @param recordList
     */
    void batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("recordList") List<TbMetaTableDO> recordList);


    /**
     * 置顶条数
     *
     * @param enterpriseId
     * @return
     */
    Integer countTop(@Param("enterpriseId") String enterpriseId);


    /**
     * 分页获取
     *
     * @param enterpriseId
     * @return
     */
    Page<TbMetaTableDO> selectPage(@Param("enterpriseId") String enterpriseId);

    /**
     * 检查表名称查询
     *
     * @param enterpriseId
     * @return
     */
    Integer countCheckTableByName(@Param("enterpriseId") String enterpriseId, @Param("tableName") String tableName);

    /**
     * 获取所有表
     *
     * @param enterpriseId
     * @return
     */
    List<TbMetaTableDO> getAllMetaTable(@Param("enterpriseId") String enterpriseId, @Param("tableIds")List<Long> tableIds);

    List<TbMetaTableDO> getEditCommonUserInfo(@Param("enterpriseId") String enterpriseId, @Param("tableIds")List<Long> tableIds);

    Integer copyMetaTable(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaTableDO record);

    Integer deleteAllTable(@Param("enterpriseId") String enterpriseId);

    boolean addSopNode(@Param("enterpriseId") String enterpriseId,
                       @Param("record") TbMetaTableDO param);

    String judgeName(@Param("enterpriseId") String enterpriseId,
                     @Param("nodeName") String nodeName,
                     @Param("id") Long id);

    List<SopDTO> selectBySopType(@Param("enterpriseId") String enterpriseId,
                                 @Param("leaf") String leaf,
                                 @Param("path")String path,
                                 @Param("tableIds") List<String> tableIds,
                                 @Param("userId") String userId);

    List<SopDTO> selectByNodePathList(@Param("enterpriseId") String enterpriseId,
                                      @Param("nodePathList") List<String> nodePathList,
                                      @Param("tableIds") List<String> tableIds,
                                      @Param("userId") String userId);


    void batchUpdatePath(@Param("enterpriseId") String enterpriseId,
                         @Param("path") String path,
                         @Param("ids") List<Long> ids);

    void batchUserIdByUpdatePath(@Param("enterpriseId") String enterpriseId,
                                 @Param("path") String path,
                                 @Param("usePersonInfo") String usePersonInfo,
                                 @Param("useRange") String useRange,
                                 @Param("resultViewPersonInfo") String resultViewPersonInfo,
                                 @Param("resultViewRange") String resultViewRange,
                                 @Param("commonEditPersonInfo") String commonEditPersonInfo
    );

    List<SopDTO> selectByName(@Param("enterpriseId") String enterpriseId,
                              @Param("leaf") String leaf,
                              @Param("name") String name);


    void moveDefaultGroup(@Param("enterpriseId") String enterpriseId,
                          @Param("childPath") String childPath,
                          @Param("child") String child);

    TbMetaTableDO selectListByTableId(@Param("enterpriseId")String enterpriseId,@Param("id") Long metaTableId);

    TbMetaTableDO selectByDefault(@Param("enterpriseId") String enterpriseId);

    /**
     * 查询使用人包含用户id的陈列检查表
     * @param enterpriseId 企业id
     * @param metaTableIds 表id
     * @param name 陈列表名
     * @param startTime 开始时间，yyyy-MM-dd，左闭右开
     * @param endTime 结束时间，yyyy-MM-dd
     * @return java.util.List<com.coolcollege.intelligent.model.metatable.TbMetaTableDO>
     */
    List<TbMetaTableDO> selectDisplayTableAndUsedUserContainUserId(
            @Param("enterpriseId") String enterpriseId,
            @Param("metaTableIds") List<Long> metaTableIds,
            @Param("name") String name,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
}
