package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordInfoDO;
import com.coolcollege.intelligent.model.patrolstore.statistics.SafetyCheckScoreUserDTO;
import com.coolcollege.intelligent.model.safetycheck.vo.ScSafetyCheckCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface TbPatrolStoreRecordInfoMapper {

    /**
     * 保存巡店记录额外信息
     *
     * @param eid
     * @param tbPatrolStoreRecordInfoDO
     * @return
     */
    void saveTbPatrolStoreRecordInfo(@Param("eid") String eid,
                                     @Param("tbPatrolStoreRecordInfo") TbPatrolStoreRecordInfoDO tbPatrolStoreRecordInfoDO);

    List<TbPatrolStoreRecordInfoDO> selectTbPatrolStoreRecordInfoList(@Param("eid") String eid,
                                                                      @Param("idList") List<Long> idList);

    TbPatrolStoreRecordInfoDO selectTbPatrolStoreRecordInfo(@Param("eid") String eid,
                                                            @Param("id") Long id);

    void deleteTbPatrolStoreRecordInfo(@Param("eid") String eid,
                                       @Param("id") Long id);

    /**
     * 更新审批人信息
     */
    int updateAuditInfo(@Param("enterpriseId") String enterpriseId
            ,@Param("id") Long id, @Param("auditUserId") String auditUserId, @Param("auditUserName") String auditUserName
            ,@Param("auditPicture") String auditPicture, @Param("auditOpinion") String auditOpinion
            ,@Param("auditRemark") String auditRemark);

    /**
     * 更新门店伙伴签字信息
     */
    int updateStorePartnerSignatureInfo(@Param("enterpriseId") String enterpriseId
            ,@Param("id") Long id, @Param("signatureUrl") String signatureUrl
            , @Param("signatureResult") String signatureResult,@Param("signatureRemark") String signatureRemark
            , @Param("signatureUserId") String signatureUserId);

    /**
     * 更新稽核完成时间
     */
    int updatesafetyCheckFinishTime(@Param("enterpriseId") String enterpriseId
            ,@Param("id") Long id, @Param("signEndTime") Date signEndTime);

    List<ScSafetyCheckCountVO> statisticsSafetyCheckUser(@Param("enterpriseId") String enterpriseId,
                                                         @Param("userIdList") List<String> userIdList, @Param("beginDate") String beginDate,
                                                         @Param("endDate") String endDate);


    int updateSafetyCheckAppealPassNum(@Param("enterpriseId") String enterpriseId
            ,@Param("id") Long id);

    int updateSafetyCheckAppealRejectNum(@Param("enterpriseId") String enterpriseId
            ,@Param("id") Long id);

    int updateSafetyCheckAuditRejectNum(@Param("enterpriseId") String enterpriseId
            ,@Param("id") Long id);

    int updateDeleteUserInfo(@Param("enterpriseId") String enterpriseId
            ,@Param("id") Long id, @Param("deleteUserId") String deleteUserId
            , @Param("deleteUserName") String deleteUserName);

}