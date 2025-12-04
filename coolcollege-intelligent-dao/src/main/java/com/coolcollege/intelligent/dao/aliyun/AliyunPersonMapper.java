package com.coolcollege.intelligent.dao.aliyun;

import com.coolcollege.intelligent.model.aliyun.AliyunPersonDO;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunPersonDTO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunPersonVO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsPersonHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/24
 */
@Mapper
public interface AliyunPersonMapper {

    int insertAliyunPerson(@Param("eid") String enterpriseId,
                           @Param("aliyunPersonDO") AliyunPersonDO aliyunPersonDO);

    int updateAliyunPerson(@Param("eid") String enterpriseId,
                           @Param("aliyunPersonDO") AliyunPersonDO aliyunPersonDO);

    int updateAliyunPersonByTaskId(@Param("eid") String enterpriseId,
                                   @Param("taskId") String taskId,
                                   @Param("customerId") String customerId);

    int updateAliyunPersonWebHook(@Param("eid") String enterpriseId,
                                  @Param("aliyunPersonDO") AliyunPersonDO aliyunPersonDO);


    int deleteAliyunPerson(@Param("eid") String enterpriseId,
                           @Param("customerIdList") List<String> customerId);

    List<AliyunPersonDO> listAliyunPerson(@Param("eid") String enterpriseId,
                                          @Param("customerIdList") List<String> customerIdList);

    AliyunPersonDO getAliyunPersonByCustomer(@Param("eid") String enterpriseId,
                                             @Param("customerId") String customerId);

    /**
     * 返回人员信息中不包含分组
     * @param enterpriseId
     * @param customerIdList
     * @param isDelete
     * @return
     */
    List<AliyunVdsPersonHistoryVO> getAliyunPersonByCustomerList(@Param("eid") String enterpriseId,
                                                                 @Param("customerIdList") List<String> customerIdList,
                                                                 @Param("isDelete")Boolean isDelete);

    AliyunPersonDO getAliyunPersonByFaceId(@Param("eid") String enterpriseId,
                                           @Param("faceId") String faceId);

    AliyunPersonDO getAliyunPersonByPic(@Param("eid") String enterpriseId,
                                        @Param("picUrl") String picUrl);


    List<AliyunPersonDTO> listAliyunPersonDTO(@Param("eid") String enterpriseId,
                                              @Param("groupId") String groupId,
                                              @Param("keywords") String keywords);


    /**
     *
     * @param enterpriseId
     * @param customerIdList
     * @return
     */
    List<AliyunPersonDTO> listAliyunPersonDTOByCustomerId(@Param("eid") String enterpriseId,
                                                          @Param("customerIdList") List<String> customerIdList);

    /**
     * 返回分组信息（只包含分组信息）
     * @param enterpriseId
     * @param customerIdList
     * @return
     */
    List<AliyunPersonDTO> listAliyunPersonDTOByCustomerIdAndGroup(@Param("eid") String enterpriseId,
                                                          @Param("customerIdList") List<String> customerIdList);

    List<AliyunPersonDTO> listAliyunPersonDTOByVipAndUnbind(@Param("eid") String enterpriseId,
                                                            @Param("keywords") String keywords);


}
