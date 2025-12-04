package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseCluesDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseCluesDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseCluesRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2021/11/23
 */
@Mapper
public interface EnterpriseCluesMapper {

    void save(@Param("entity") EnterpriseCluesDO entity);

    Integer batchSave(@Param("list") List<EnterpriseCluesDO> list);

    void update(@Param("entity") EnterpriseCluesDO entity);

    void batchUpdate(@Param("list") List<EnterpriseCluesDO> list);

    void deleteById(@Param("id") Long id);

    List<EnterpriseCluesDTO> list(@Param("request") EnterpriseCluesRequest request);

    Long count(@Param("request") EnterpriseCluesRequest request);
}
