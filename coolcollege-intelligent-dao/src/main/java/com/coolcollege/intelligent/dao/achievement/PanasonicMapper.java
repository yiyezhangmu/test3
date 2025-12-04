package com.coolcollege.intelligent.dao.achievement;


import com.coolcollege.intelligent.model.achievement.dto.PanasonicFindResponse;
import com.coolcollege.intelligent.model.achievement.entity.ManageStoreCategoryCodeDO;
import com.coolcollege.intelligent.model.achievement.entity.PanasonicTempDO;
import com.coolcollege.intelligent.model.achievement.entity.TaskModelsMappingDO;
import com.coolcollege.intelligent.model.achievement.request.PanasonicAddRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PanasonicMapper {


    void panasonicAdd(@Param("enterpriseId") String enterpriseId,
                      @Param("list") List<PanasonicAddRequest.innerClass> data);

    List<PanasonicFindResponse> panasonicFind(@Param("enterpriseId") String enterpriseId,
                                              @Param("storeId") String storeId,
                                              @Param("category") String category);

    List<PanasonicFindResponse> panasonicFind2(@Param("enterpriseId") String enterpriseId,
                                               @Param("storeId") String storeId,
                                               @Param("name") String name,
                                               @Param("middleName") String middleName);

    List<PanasonicTempDO> getStructure(@Param("enterpriseId")String enterpriseId);

    List<PanasonicTempDO> queryList(@Param("enterpriseId")String enterpriseId,@Param("query")PanasonicTempDO query);

    Long panasonicFindByType(@Param("enterpriseId") String enterpriseId,
                                              @Param("storeId") String storeId,
                                              @Param("type") String type);

    int updatePanasonicByTypeId(@Param("enterpriseId") String enterpriseId,
                             @Param("goodNum") Integer goodNum,
                             @Param("id") Long id);

    int updatePanasonicStatusByTypeId(@Param("enterpriseId") String enterpriseId,
                                @Param("id") Long id);

    int updatePanasonicOnStatusByTypeId(@Param("enterpriseId") String enterpriseId,
                                      @Param("id") Long id);

    List<String> getAllCategory(@Param("enterpriseId") String enterpriseId);

    List<String> getMiddleClassByCategory(@Param("enterpriseId")String enterpriseId, @Param("category") String category);

    List<String> getTypeByCategoryAndMiddleClass(@Param("enterpriseId")String enterpriseId,@Param("category") String category,@Param("middleClass") String middleClass);

    List<String> getMiddleClassByMainClass(@Param("enterpriseId")String enterpriseId,@Param("mainClass") String mainClass);

    List<String> getMarketStoreList(@Param("enterpriseId")String enterpriseId,@Param("type") String type);

    Long getStoreNum(@Param("enterpriseId")String enterpriseId,@Param("mainClass") String mainClass, @Param("regionId") String regionId);


    List<String> selectStoreByCategoryCode(@Param("storeId")String storeId,@Param("categoryCode") String categoryCode);

    Integer selectGoodsNumStoreSampleExtraction(@Param("storeId") String storeId,@Param("productModel")String productModel);


    List<ManageStoreCategoryCodeDO> selectManageStoreCategoryCode(@Param("storeId")String storeId, @Param("categoryCode") String categoryCode);

    List<String> selectStoreSampleExtraction(@Param("storeId") String storeId,@Param("productModels") List<String> productModels);

    Integer batchInsertTaskModelsMapping(@Param("dos") List<TaskModelsMappingDO> dos);

    List<TaskModelsMappingDO> selectTaskModelsMapping(@Param("taskStoreId")Long taskStoreId);

    Integer updateTaskModelsMappingById(@Param("taskModelsMappingDO") TaskModelsMappingDO taskModelsMappingDO);

    List<TaskModelsMappingDO> selectTaskModelsMappingByIds(@Param("ids")List<Long> ids);

    String selectPhysicalNum(@Param("storeId") String storeId);

    Integer selectStoreByStoreId(@Param("storeId")String storeId);
}
