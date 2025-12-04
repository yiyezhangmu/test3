package com.coolcollege.intelligent.service.achievement.qyy.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ConversationTypeEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.Base64Utils;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.mapper.achieve.qyy.QyyRecommendStyleDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.AddRecommendStyleDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.ConversationSplitDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.UpdateRecommendStyleDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import com.coolcollege.intelligent.service.achievement.qyy.RecommendStyleService;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.service.achievement.qyy.open.AoKangOpenApiService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: RecommendStyleServiceImpl
 * @Description: 主推款
 * @date 2023-04-11 15:29
 */
@Service
@Slf4j
public class RecommendStyleServiceImpl implements RecommendStyleService {

    @Resource
    private QyyRecommendStyleDAO qyyRecommendStyleDAO;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private SendCardService sendCardService;
    @Resource
    private AoKangOpenApiService aoKangOpenApiService;

    @Override
    public List<H5RecommendStyleListVO> getH5RecommendStyleList(String enterpriseId, String conversationId, ConversationTypeEnum conversationType) {
        List<QyyRecommendStyleDO> recommendStyleList = qyyRecommendStyleDAO.getRecommendStyleByConversationId(enterpriseId, conversationId, conversationType);
        return H5RecommendStyleListVO.convert(recommendStyleList);
    }

    @Override
    public H5RecommendStyleDetailVO getRecommendStyleDetail(String enterpriseId, Long id) {
        QyyRecommendStyleDO recommendStyle = qyyRecommendStyleDAO.getRecommendStyleDetail(enterpriseId, id);
        if(Objects.isNull(recommendStyle)){
            return null;
        }
        H5RecommendStyleDetailVO result = H5RecommendStyleDetailVO.convert(recommendStyle);
        result.setGoodsList(searchGoods(enterpriseId, recommendStyle.getGoodsIds()));
        return result;
    }

    @Override
    public PageInfo<PCRecommendStyleListVO> getPCRecommendStylePage(String enterpriseId, String name, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<QyyRecommendStyleDO> page = qyyRecommendStyleDAO.getPCRecommendStylePage(enterpriseId, name);
        List<PCRecommendStyleListVO> resultList = null;
        if(CollectionUtils.isNotEmpty(page)){
            List<String> userIds = page.stream().map(QyyRecommendStyleDO::getCreateUserId).distinct().collect(Collectors.toList());
            Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
            resultList = new ArrayList<>();
            for (QyyRecommendStyleDO qyyRecommendStyle : page) {
                PCRecommendStyleListVO convert = PCRecommendStyleListVO.convert(qyyRecommendStyle);
                convert.setCreateUsername(userNameMap.get(qyyRecommendStyle.getCreateUserId()));
                resultList.add(convert);
            }
        }
        PageInfo result = new PageInfo<>(page);
        result.setList(resultList);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public PCRecommendStyleDetailVO getPCRecommendStyleDetail(String enterpriseId, Long id) {
        QyyRecommendStyleDO recommendStyle = qyyRecommendStyleDAO.getRecommendStyleDetail(enterpriseId, id);
        if(Objects.isNull(recommendStyle)){
            return null;
        }
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(enterpriseId, recommendStyle.getCreateUserId());
        PCRecommendStyleDetailVO result = PCRecommendStyleDetailVO.convert(recommendStyle);
        result.setCreateUsername(Optional.ofNullable(enterpriseUser).map(EnterpriseUserDO::getName).orElse(null));
        List<RecommendStyleGoodsVO> recommendStyleGoodsVOS = searchGoods(enterpriseId, recommendStyle.getGoodsIds());
        List<String> goodsIds =Arrays.asList(recommendStyle.getGoodsIds().split(","));
        Collections.sort(recommendStyleGoodsVOS, Comparator.comparing(p -> goodsIds.indexOf(p.getGoodsId())));
        result.setGoodsList(recommendStyleGoodsVOS);
        return result;
    }

    @Override
    public Boolean addRecommendStyle(String enterpriseId, String createUserId, String createUsername, AddRecommendStyleDTO param) {
        checkRequestParam(param);
        QyyRecommendStyleDO insert = convertDO(param);
        insert.setCreateUserId(createUserId);
        insert.setCreateUserName(createUsername);
        qyyRecommendStyleDAO.addRecommendStyle(enterpriseId, insert);
        if(param.getSendType().equals(Constants.ZERO)){
            //即时发送
            sendCardService.sendRecommendStyle(enterpriseId, insert);
            insert.setSendTime(new Date());
            insert.setSendStatus(Constants.INDEX_ONE);
            qyyRecommendStyleDAO.updateRecommendStyle(enterpriseId, insert);
        }
        return true;
    }

    @Override
    public Boolean updateRecommendStyle(String enterpriseId, String updateUserId, String updateUsername, UpdateRecommendStyleDTO param) {
        checkRequestParam(param);
        QyyRecommendStyleDO update = convertDO(param);
        update.setId(param.getId());
        update.setUpdateUserId(updateUserId);
        update.setUpdateUserName(updateUsername);
        return qyyRecommendStyleDAO.updateRecommendStyle(enterpriseId, update);
    }

    @Override
    public Boolean deleteRecommendStyle(String enterpriseId, Long id) {
        return qyyRecommendStyleDAO.deleteRecommendStyle(enterpriseId, id);
    }

    @Override
    public List<RecommendStyleGoodsVO> searchGoods(String enterpriseId, String goodsIds) {
        return aoKangOpenApiService.searchGoods(enterpriseId, goodsIds);
    }



    public void checkRequestParam(AddRecommendStyleDTO param){
        log.info("主推款参数：{}", JSONObject.toJSONString(param));
        if(StringUtils.isBlank(param.getName()) || param.getName().length() > Constants.ONE_HUNDRED){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(CollectionUtils.isEmpty(param.getGoodsIdsList()) || param.getGoodsIdsList().size() < Constants.INDEX_THREE || param.getGoodsIdsList().size() > Constants.FIFTY_INT){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(StringUtils.isBlank(param.getConversationInfo())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(Objects.isNull(param.getSendType())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(Constants.INDEX_ONE.equals(param.getSendType()) && Objects.isNull(param.getSendTime())){
            //定时发送  且 时间为空
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
    }

    /**
     * model转换
     * @param param
     * @return
     */
    public QyyRecommendStyleDO convertDO(AddRecommendStyleDTO param){
        QyyRecommendStyleDO result = new QyyRecommendStyleDO();
        result.setName(param.getName());
        result.setGoodsNum(param.getGoodsIdsList().size());
        result.setGoodsIds(String.join(Constants.COMMA, param.getGoodsIdsList()));
        result.setCourseInfo(param.getCourseInfo());
        result.setSendTime(param.getSendTime());
        result.setSendType(param.getSendType());
        result.setConversationInfo(param.getConversationInfo());
        JSONObject jsonObject = JSONObject.parseObject(param.getConversationInfo());
        String storeConversation = jsonObject.getString("storeConversation");
        String compConversation = jsonObject.getString("compConversation");
        String otherConversation = jsonObject.getString("otherConversation");

        if(StringUtils.isNotBlank(compConversation) && !"ALL".equals(compConversation)){
            List<ConversationSplitDTO.ConversationInfo> corpConversations = JSONObject.parseArray(compConversation, ConversationSplitDTO.ConversationInfo.class);
            List<String> conversationIds= corpConversations.stream().map(ConversationSplitDTO.ConversationInfo::getId).collect(Collectors.toList());
            //一方群id，请勿在酷店掌业务中使用
            List<String> openConversationIds = corpConversations.stream().map(ConversationSplitDTO.ConversationInfo::getOpenConversationId).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(conversationIds)){
                compConversation = "," + String.join(",", conversationIds) + ",";
            }
            if(CollectionUtils.isNotEmpty(openConversationIds)){
                result.setCompConversationId("," + String.join(",", openConversationIds) + ",");
            }
        }
        if(StringUtils.isNotBlank(otherConversation) && !"ALL".equals(otherConversation)){
            List<ConversationSplitDTO.ConversationInfo> otherConversations = JSONObject.parseArray(otherConversation, ConversationSplitDTO.ConversationInfo.class);
            List<String> conversationIds= otherConversations.stream().map(ConversationSplitDTO.ConversationInfo::getId).collect(Collectors.toList());
            //一方群id，请勿在酷店掌业务中使用
            List<String> openConversationIds = otherConversations.stream().map(ConversationSplitDTO.ConversationInfo::getOpenConversationId).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(conversationIds)){
                otherConversation = "," + String.join(",", conversationIds) + ",";
            }
            if(CollectionUtils.isNotEmpty(openConversationIds)){
                result.setOtherConversationId("," + String.join(",", openConversationIds) + ",");
            }
        }
        result.setStoreConversation(storeConversation);
        result.setCompConversation(compConversation);
        result.setOtherConversation(otherConversation);
        return result;
    }
}
