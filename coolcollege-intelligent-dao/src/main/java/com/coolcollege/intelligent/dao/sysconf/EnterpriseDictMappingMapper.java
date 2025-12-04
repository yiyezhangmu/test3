package com.coolcollege.intelligent.dao.sysconf;

import com.coolcollege.intelligent.model.system.sysconf.EnterpriseDictMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/3/25 20:44
 */
@Mapper
public interface EnterpriseDictMappingMapper {
    /**
     * 批量新增
     * @param mappingList
     * @return
     */
    void batchInsert(@Param("mappingList") List<EnterpriseDictMappingDO> mappingList);


    /**
     * 根据dingCoprId或enterpriseId查询企业字典配置
     * @param enterpriseId
     * @param dingCorpId
     * @param dictKey
     * @param businessType
     * @return
     */
    List<EnterpriseDictMappingDO> listMappingByKeyAndType(@Param("enterpriseId") String enterpriseId ,
                                                          @Param("dingCorpId") String dingCorpId,
                                                          @Param("dictKey") String dictKey,
                                                          @Param("businessType") String businessType);

    /**
     * 删除配置项
     * @param enterpriseId
     * @param dingCorpId
     * @param dictKey
     * @param businessType
     * @return
     */
    int deleteKeyAndType(@Param("enterpriseId") String enterpriseId,
                         @Param("dingCorpId")String dingCorpId,
                         @Param("dictKey") String dictKey,
                         @Param("businessType") String businessType);

    /**
     *更新value值
     * @param dictValue
     * @param dictKey
     * @param businessType
     * @return
     */
    int updateValueByKeyAndType(@Param("dictValue") String dictValue,
                                @Param("id") Long id,
                                @Param("enterpriseId") String enterpriseId,
                                @Param("dingCorpId") String dingCorpId);

    void deleteByDictId(@Param("dictId") Long id);

    void deleteById(@Param("id") Long id);
}
