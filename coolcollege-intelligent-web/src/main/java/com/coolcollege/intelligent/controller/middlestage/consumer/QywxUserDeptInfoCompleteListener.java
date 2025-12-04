package com.coolcollege.intelligent.controller.middlestage.consumer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.QyNameReplaceEnum;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.qywx.impl.FetchIdAndNameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 企业微信用户部门信息补全
 * @author wxp
 * @date 2021-09-02 11:38
 */
@Service
@Slf4j
public class QywxUserDeptInfoCompleteListener {

    @Autowired
    private FetchIdAndNameService fetchIdAndNameService;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private ChatService chatService;


//    /**
//     * 企业微信用户部门信息补全消息监听 qywx_userdeptinfo_complete_queue
//     * @param
//     */
//    @JmsListener(destination = "${qywx.userdeptinfocomplete.queue:qywx_userdeptinfo_complete_queue}", containerFactory = "isvFactory")
//    public void qywxUserDeptInfoComplete(String text) {
//        MDCUtils.put(Constants.REQUEST_ID);
//        log.info("企业微信用户部门信息补全qywxUserDeptInfoComplete：{}", text);
//        JSONObject jsonObject = JSONObject.parseObject(text);
//        String enterpriseId = jsonObject.getString("enterpriseId");
//        String corpId = jsonObject.getString("corpId");
//        String dataType = jsonObject.getString("dataType");
//        JSONArray userIdList = jsonObject.getJSONArray("userIdList");
//        //1.获取待开发accessToken
//        String dkfAccessToken = chatService.getDkfAccessToken(corpId);
//        if (StringUtils.isBlank(dkfAccessToken)) {
//            log.info("企业微信待开发token不存在，先扫码授权待开发应用模板corpId：{}", corpId);
//            return;
//        }
//        JSONArray idAndNameArray = fetchIdAndNameService.fetchIdAndNameByCorpIdAndCorpSecret(corpId, dataType, userIdList, dkfAccessToken);
//        DataSourceHelper.reset();
//        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
//        //切换企业库
//        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
//        // dataType 是用户  是部门
//        try {
//            if (dataType.equals(QyNameReplaceEnum.USER.getValue())) {
//                List<EnterpriseUserDO> userDOList = new ArrayList<>();
//                idAndNameArray.forEach(idAndName -> {
//                    JSONObject idAndNameObj = (JSONObject) idAndName;
//                    String userId = idAndNameObj.getString("id");
//                    String name = idAndNameObj.getString("name");
//                    EnterpriseUserDO userDO = new EnterpriseUserDO();
//                    userDO.setUserId(userId);
//                    userDO.setName(name);
//                    userDOList.add(userDO);
//                });
//                if(CollectionUtils.isNotEmpty(userDOList)){
//                    // 更新企业库用户名
//                    enterpriseUserService.batchUpdateUserName(enterpriseId, userDOList);
//                    DataSourceHelper.reset();
//                    // 更新平台库用户名
//                    enterpriseUserService.batchUpdatePlatformUserName(userDOList);
//                }
//            } else if (dataType.equals(QyNameReplaceEnum.DEPARTMENT.getValue())) {
//                List<SysDepartmentDO> depts = new ArrayList<>();
//                idAndNameArray.forEach(idAndName -> {
//                    JSONObject idAndNameObj = (JSONObject) idAndName;
//                    Long deptId = idAndNameObj.getLong("id");
//                    String name = idAndNameObj.getString("name");
//                    SysDepartmentDO departmentDO = new SysDepartmentDO();
//                    departmentDO.setId(deptId);
//                    departmentDO.setName(name);
//                    depts.add(departmentDO);
//                });
//                if(CollectionUtils.isNotEmpty(depts)){
//                    sysDepartmentService.batchUpdateDeptName(enterpriseId, depts);
//                }
//            }
//        } catch (Exception e) {
//            log.error("企业微信用户部门信息补全消息异常", e);
//        }
//    }

}
