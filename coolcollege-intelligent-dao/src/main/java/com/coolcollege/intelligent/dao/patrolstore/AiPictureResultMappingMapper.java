package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.AiPictureResultMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2022/4/1
 */
@Mapper
public interface AiPictureResultMappingMapper {
    /**
     * 插入
     */
    Integer insert(@Param("enterpriseId") String enterpriseId, @Param("entity") AiPictureResultMappingDO entity);

    /**
     * 批量插入
     */
    Integer batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<AiPictureResultMappingDO> list);

    /**
     * 批量插入
     */
    List<AiPictureResultMappingDO> selectByPictureIdList(@Param("enterpriseId") String enterpriseId, @Param("pictureIdList") List<Long> pictureIdList);
}
