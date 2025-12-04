package com.coolcollege.intelligent.dao.storework;

import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkSearchRequest;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
public interface SwStoreWorkMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    int insertSelective(@Param("record")SwStoreWorkDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    SwStoreWorkDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id")Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    int updateByPrimaryKeySelective(@Param("record")SwStoreWorkDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 查询当前时间范围内的任务
     * @param enterpriseId
     * @param currentDate
     * @param workCycle
     * @return
     */
    List<SwStoreWorkDO> selectByTime(@Param("enterpriseId") String enterpriseId,
                                     @Param("currentDate") Date currentDate,
                                     @Param("storeWorkId") Long storeWorkId,
                                     @Param("workCycle") String workCycle);

    /**
     *
     * 查询列表
     */
    List<SwStoreWorkDO> list(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkSearchRequest request);

    void updateStatusByStoreWorkId(@Param("enterpriseId") String enterpriseId, @Param("workStatus") String workStatus, @Param("storeWorkId") Long storeWorkId);

    List<SwStoreWorkDO> listBystoreWorkIds(@Param("enterpriseId") String enterpriseId, @Param("storeWorkIdList") List<Long> storeWorkIdList);

    List<SwStoreWorkDO> selectAllPersonInfo(@Param("enterpriseId")String enterpriseId);

    List<SwStoreWorkDO> selectListByWorkCycle(@Param("enterpriseId") String enterpriseId,
                                              @Param("workCycle")String workCycle,
                                              @Param("swWorkId") String swWorkId);
}