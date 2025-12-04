package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseFollowRecordsDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseFollowRecordsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 线索跟进表
 *
 * @author chenyupeng
 * @since 2021/11/24
 */
@Mapper
public interface EnterpriseFollowRecordsMapper {
    void save(@Param("entity") EnterpriseFollowRecordsDO entity);

    void update(@Param("entity") EnterpriseFollowRecordsDO entity);

    void deleteById(@Param("id") Long id);

    List<EnterpriseFollowRecordsDTO> list(@Param("cluesId") Long cluesId);
}
