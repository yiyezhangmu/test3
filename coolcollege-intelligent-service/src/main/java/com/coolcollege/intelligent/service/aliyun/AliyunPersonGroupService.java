package com.coolcollege.intelligent.service.aliyun;

import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupDO;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonGroupAddRequest;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonGroupUpdateRequest;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunGroupVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliyunPersonGroupService {

    /**
     * 人员标签分组列表全量
     * @param enterpriseId
     * @return
     */
    List<AliyunGroupVO> listAliyunPersonGroup(String enterpriseId);

    /**
     * 添加人员分组列表
     * @param eid
     * @param request
     * @return
     */
    Boolean addAliyunPersonGroup(String eid, AliyunPersonGroupAddRequest request);

    /**
     * 更新分组
     * @param eid
     * @param request
     * @return
     */
    Boolean updateAliyunPersonGroup(String eid, AliyunPersonGroupUpdateRequest request);

    /**
     * 删除分组
     * @param eid
     * @param groupId
     * @return
     */
    Boolean deleteAliyunPersonGroup(String eid,String groupId);

    /**
     * 初始化分组
     * @param enterpriseId
     * @return
     */
    Boolean initGroup(String enterpriseId);

}
