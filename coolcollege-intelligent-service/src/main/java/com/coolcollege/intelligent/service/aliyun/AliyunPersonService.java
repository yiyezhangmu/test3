package com.coolcollege.intelligent.service.aliyun;

import com.coolcollege.intelligent.common.util.ListPageInfo;
import com.coolcollege.intelligent.model.aliyun.request.AliyunStaticPersonAddRequest;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonUpdateRequest;
import com.coolcollege.intelligent.model.aliyun.request.DynamicPersonBindRequest;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunPersonVO;

import java.text.ParseException;
import java.util.List;

public interface AliyunPersonService {

    /**
     * 添加静态人员
     * @param enterpriseId
     * @param request
     * @return
     */
    Boolean addStaticAliyunPerson(String enterpriseId, AliyunStaticPersonAddRequest request);

    /**
     * 更新阿里云人员信息
     * @param enterpriseId
     * @param request
     * @return
     */
    Boolean updateAliyunPerson(String enterpriseId, AliyunPersonUpdateRequest request);

    Boolean deleteAliyunPerson(String enterpriseId,String customerId);

    /**
     * 人员信息详情
     * @param enterpriseId
     * @param customerId
     * @return
     */
    AliyunPersonVO getAliyunPerson(String enterpriseId, String customerId);

    /**
     * 根据分组拿到人员信息
     * @param enterpriseId
     * @param groupId
     * @param pageSize
     * @param pageNumber
     * @return
     */
    Object listAliyunPerson(String enterpriseId, String groupId, Integer pageSize, Integer pageNumber,String keywords);


    /**
     * 绑定动态人员列表
     * @param enterpriseId
     * @param pageSize
     * @param pageNumber
     * @return
     */
    Object bindAliyunDynamicPersonList(String enterpriseId,Integer pageSize,Integer pageNumber,String keywords);

    Object searchStatic(String enterpriseId,Integer pageSize,Integer pageNumber);





}
