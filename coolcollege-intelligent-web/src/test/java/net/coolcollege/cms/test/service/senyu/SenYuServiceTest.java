//package net.coolcollege.cms.test.service.senyu;
//
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
//import com.coolcollege.intelligent.facade.dto.RegionDTO;
//import com.coolcollege.intelligent.model.region.dto.AsyncDingRequestDTO;
//import com.coolcollege.intelligent.model.senyu.request.SenYuEmployeeInfoRequest;
//import com.coolcollege.intelligent.model.senyu.request.SenYuStoreRequest;
//import com.coolcollege.intelligent.model.senyu.response.*;
//import com.coolcollege.intelligent.service.jms.JmsMessageSendService;
//import com.coolcollege.intelligent.service.senyu.SenYuService;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.annotation.Resource;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//public class SenYuServiceTest extends IntelligentMainTest {
//
//    @Autowired
//    private SenYuService senYuService;
//    @Resource
//    private JmsMessageSendService jmsMessageSendService;
//
//    @Test
//    public void tetx() {
//        SenYuEmployeeInfoRequest request = new SenYuEmployeeInfoRequest();
//        request.setIdCard("230321197705190418");
//        log.info(JSONUtil.toJsonStr(senYuService.getEmployeeInfoByIdCard(request)));
//    }
//
//    @Test
//    public void send() {
//        AsyncDingRequestDTO asyncDingRequestDTO = new AsyncDingRequestDTO();
//        asyncDingRequestDTO.setDingCorpId("enterpriseConfigDO.getDingCorpId()");
//        asyncDingRequestDTO.setEid("b58cb9a235d0498aa6c91a44c1c2ca88");
//        asyncDingRequestDTO.setDbName("enterpriseConfigDO.getDbName()");
//        asyncDingRequestDTO.setUserName("userName");
//        asyncDingRequestDTO.setUserId("userId");
//
////        jmsMessageSendService.sendMessage(IntelligentFacadeConstants.MQ_DING_SYNC_ALL_DATA_OA_QUEUE, JSONObject.parseObject(JSONObject.toJSONString(asyncDingRequestDTO)));
//    }
//    @Test
//    public void listAllStore() {
//
//        String parentCode = "";
//        List<RegionDTO> deptList = new ArrayList<>();
//        SenYuStoreRequest senYuStoreRequest = new SenYuStoreRequest();
//
//        //抽出来 分页查询所有门店
//        int pageSize = senYuStoreRequest.getPageSize();
//        int maxSize = 100000;
//        long pages = (maxSize + pageSize - 1) / pageSize;
//
//        List<SenYuStoreResponse> resultList = new ArrayList<>();
//
//        for (int curPage = 1; curPage <= pages; curPage++) {
//            senYuStoreRequest.setPage(curPage);
//            SenYuBaseResponse<SenYuStorePageResponse> directEmployeesResponse = senYuService.listAllStoreByPage(senYuStoreRequest);
//            List<SenYuStoreResponse> response = directEmployeesResponse.getData().getList();
//            //没有下一页，终止循环
//            if (CollectionUtils.isEmpty(response)) {
//                break;
//            }
//            resultList.addAll(response);
//        }
//
//        if (CollectionUtils.isNotEmpty(resultList)) {
//            for (SenYuStoreResponse senYuStoreResponse : resultList) {
//                RegionDTO regionDTO = new RegionDTO();
//                regionDTO.setSynDingDeptId(senYuStoreResponse.getKehbm());
//                regionDTO.setName(senYuStoreResponse.getKehmc());
//                regionDTO.setParentId(StringUtils.isBlank(senYuStoreResponse.getYingyzzbm()) ? parentCode : senYuStoreResponse.getYingyzzbm());
//                regionDTO.setStoreCode(senYuStoreResponse.getKehbm());
//                regionDTO.setStoreRange(true);
//                deptList.add(regionDTO);
//            }
//        }
//
//        //去重，防止脏数据
//        deptList = deptList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
//                new TreeSet<>(Comparator.comparing(RegionDTO::getSynDingDeptId))), ArrayList::new));
//
//        System.out.println("门店总计多少条==="+deptList.size());
//    }
//
//    @Test
//    public void recursionListAllEmployees() {
//        List<SenYuEmployeeInfoResponse> resultList = new ArrayList<>();
//        //查询全部员工
//       //  recursionListAllEmployees(null, resultList);
//        listEmployeesByRoldIds(resultList);
//        listPromotions(resultList);
//        resultList = resultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
//                new TreeSet<>(Comparator.comparing(SenYuEmployeeInfoResponse::getKehbm))), ArrayList::new));
//
//        System.out.println("全部员工多少条==="+resultList.size());
//    }
//
//    public void recursionListAllEmployees(String parentCode, List<SenYuEmployeeInfoResponse> resultList) {
//        SenYuEmployeeInfoRequest senYuEmployeeInfoRequest = new SenYuEmployeeInfoRequest();
//        senYuEmployeeInfoRequest.setParentCode(parentCode);
//        SenYuBaseListResponse<SenYuEmployeeInfoResponse> directEmployeesResponse = senYuService.listDirectEmployees(senYuEmployeeInfoRequest);
//
//        if (directEmployeesResponse != null && CollectionUtils.isNotEmpty(directEmployeesResponse.getData())) {
//            List<SenYuEmployeeInfoResponse> subResultList = directEmployeesResponse.getData();
//            resultList.addAll(subResultList);
//            for (SenYuEmployeeInfoResponse response : subResultList) {
//                recursionListAllEmployees(response.getKehbm(), resultList);
//            }
//        }
//    }
//
//    // 根据职位查询员工
//    private void listEmployeesByRoldIds(List<SenYuEmployeeInfoResponse> resultList) {
//        SenYuEmployeeInfoRequest senYuEmployeeInfoRequest = new SenYuEmployeeInfoRequest();
//        String roleIds = "1,2,3,5,6";
//        senYuEmployeeInfoRequest.setRoleIds(roleIds);
//        SenYuBaseListResponse<SenYuEmployeeInfoResponse>  employeesResponse = senYuService.listEmployeesByRoldIds(senYuEmployeeInfoRequest);
//
//        if (employeesResponse != null && CollectionUtils.isNotEmpty(employeesResponse.getData())) {
//            List<SenYuEmployeeInfoResponse> resultListByRolds = employeesResponse.getData();
//            resultList.addAll(resultListByRolds);
//        }
//    }
//
//    /**
//     * 查询促销员
//     * @return
//     */
//    private void listPromotions(List<SenYuEmployeeInfoResponse> resultList) {
//
//        SenYuEmployeeInfoRequest senYuEmployeeInfoRequest = new SenYuEmployeeInfoRequest();
//        senYuEmployeeInfoRequest.setRoleIds("7");
//
//        //抽出来 分页查询所有人员
//        int pageSize = senYuEmployeeInfoRequest.getPageSize();
//        int maxSize = 100000;
//        long pages = (maxSize + pageSize - 1) / pageSize;
//
//        for (int curPage = 1; curPage <= pages; curPage++) {
//            senYuEmployeeInfoRequest.setPage(curPage);
//            SenYuBaseListResponse<SenYuEmployeeInfoResponse> employeesResponse = senYuService.listEmployeesByRoldIds(senYuEmployeeInfoRequest);
//            List<SenYuEmployeeInfoResponse> resultListByRolds = employeesResponse.getData();
//            //没有下一页，终止循环
//            if (CollectionUtils.isEmpty(resultListByRolds)) {
//                break;
//            }
//            resultList.addAll(resultListByRolds);
//        }
//    }
//
//
//}