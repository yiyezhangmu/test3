package com.coolcollege.intelligent.facade.open.api.question;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.openApi.CreateQuestionOrderDTO;
import com.coolcollege.intelligent.facade.dto.openApi.QuestionDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.dto.BuildQuestionDTO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.question.QuestionParentInfoService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/7/15 13:56
 * @Version 1.0
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = QuestionApi.class,bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class QuestionApiImpl implements QuestionApi{

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Autowired
    private QuestionRecordService questionRecordService;
    @Autowired
    private QuestionParentInfoService questionParentInfoService;

    @Override
    @ShenyuSofaClient(path = "/question/list")
    public OpenApiResponseVO QuestionList(QuestionDTO questionDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(questionRecordService.questionList(enterpriseId,questionDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }
    }

    @Override
    @ShenyuSofaClient(path = "/question/detail")
    public OpenApiResponseVO QuestionDetail(QuestionDTO questionDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(questionRecordService.questionDetail(enterpriseId,questionDTO.getQuestionId()));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }
    }

    @Override
    @ShenyuSofaClient(path = "/createQuestionOrder")
    public OpenApiResponseVO createQuestionOrder(CreateQuestionOrderDTO param) {
        String enterpriseId = "007bc3c221294bdbb772dc430b9dfcb4";//RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            if(StringUtils.isBlank(param.getParentQuestionName())){
                return OpenApiResponseVO.fail(400000,"父工单名称不能为空");
            }
            if(StringUtils.isBlank(param.getCreateUserId())){
                return OpenApiResponseVO.fail(400000,"工单创建人id不能为空");
            }
            BuildQuestionRequest request = new BuildQuestionRequest();
            request.setTaskName(param.getParentQuestionName());
            request.setQuestionType(QuestionTypeEnum.COMMON.getCode());
            List<CreateQuestionOrderDTO.QuestionDetail> requestQuestionList = param.getQuestionList();
            if(CollectionUtils.isEmpty(requestQuestionList)){
                return OpenApiResponseVO.fail(400000,"子工单列表不能为空");
            }
            List<BuildQuestionDTO> questionList = new ArrayList<>();
            for (CreateQuestionOrderDTO.QuestionDetail questionDetail : requestQuestionList) {
                BuildQuestionDTO buildQuestionDTO = new BuildQuestionDTO();
                buildQuestionDTO.setTaskName(questionDetail.getQuestionName());
                buildQuestionDTO.setEndTime(questionDetail.getEndTime());
                buildQuestionDTO.setStoreId(questionDetail.getStoreId());
                buildQuestionDTO.setTaskDesc(questionDetail.getTaskDesc());
                List<CreateQuestionOrderDTO.ProcessInfoDTO> requestProcessList = questionDetail.getProcessList();
                if(CollectionUtils.isEmpty(requestProcessList)){
                    return OpenApiResponseVO.fail(400000,"工单处理节点不能为空");
                }
                List<TaskProcessDTO> processList = new ArrayList<>();
                for (CreateQuestionOrderDTO.ProcessInfoDTO processInfoDTO : requestProcessList) {
                    TaskProcessDTO process = new TaskProcessDTO();
                    process.setNodeNo(processInfoDTO.getNodeNo());
                    List<CreateQuestionOrderDTO.ProcessUser> userList = processInfoDTO.getUser();
                    if(CollectionUtils.isEmpty(userList)){
                        return OpenApiResponseVO.fail(400000,"工单处理节点人员不能为空");
                    }
                    List<GeneralDTO> users = new ArrayList<>();
                    for (CreateQuestionOrderDTO.ProcessUser processUser : userList) {
                        GeneralDTO generalDTO = new GeneralDTO();
                        generalDTO.setType(processUser.getType());
                        generalDTO.setValue(processUser.getValue());
                        generalDTO.setName(processUser.getName());
                        users.add(generalDTO);
                    }
                    process.setUser(users);
                    processList.add(process);
                }
                buildQuestionDTO.setProcess(processList);
                CreateQuestionOrderDTO.TaskInfoDTO taskInfo = questionDetail.getTaskInfo();
                if(Objects.isNull(taskInfo) || Objects.isNull(taskInfo.getMetaColumnId())){
                    return OpenApiResponseVO.fail(400000,"工单项不能为空");
                }
                QuestionTaskInfoDTO taskInfoDTO = new QuestionTaskInfoDTO();
                taskInfoDTO.setPhotos(taskInfo.getPhotos());
                if(Objects.isNull(taskInfo.getMetaColumnId())){
                    return OpenApiResponseVO.fail(400000,"工单问题项不能为空");
                }
                taskInfoDTO.setMetaColumnId(taskInfo.getMetaColumnId());
                buildQuestionDTO.setTaskInfo(taskInfoDTO);
                questionList.add(buildQuestionDTO);
            }
            request.setQuestionList(questionList);
            Long taskId = questionParentInfoService.buildQuestion(enterpriseId, request, param.getCreateUserId(), Boolean.FALSE, null);
            List<TbQuestionRecordDO> subQuestionList = questionRecordService.getSubQuestionByParentUnifyTaskId(enterpriseId, taskId);
            Map<String, Object> result = new HashMap<>();
            result.put("parentQuestionId",taskId);
            result.put("subQuestionIds", ListUtils.emptyIfNull(subQuestionList).stream().map(TbQuestionRecordDO::getId).collect(Collectors.toList()));
            return OpenApiResponseVO.success(result);
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }
    }
}
