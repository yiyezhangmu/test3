package com.coolcollege.intelligent.service.jms;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.constant.i18n.I18nMessageKeyEnum;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.jms.vo.JmsContentParamsVo;
import com.coolcollege.intelligent.service.jms.vo.JmsSendMessageVo;
import com.coolcollege.intelligent.util.i18n.I18nUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by ydw on 2019/7/17.
 */
@Service
@Slf4j
public class JmsSendMessageLogicService {

    @Lazy
    @Autowired
    private JmsSendMessageInfoHelperService jmsSendMessageInfoHelperService;

    /**
     * 发送文本消息
     *
     * @param contentVo        内容的国际化
     * @param userList         发送的人员ID集合
     * @param jmsSendMessageVo 消息信息设置
     */
    public void sendTextMessageLogic(JmsContentParamsVo contentVo,
                                     List<EnterpriseUserDO> userList,
                                     JmsSendMessageVo jmsSendMessageVo) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }
        Map<String, List<String>> languageUsersMap = this.buildLanguageUsersMap(userList);
        if (StringUtils.isBlank(jmsSendMessageVo.getDingCorpId())) {
            jmsSendMessageVo.setDingCorpId(UserHolder.getUser().getDingCorpId());
        }
        languageUsersMap.forEach((k, v) -> {
            jmsSendMessageVo.setUserIds(v);
            if (StringUtils.isNotBlank(contentVo.getContent())) {
                jmsSendMessageVo.setContent(contentVo.getContent());
            } else {
                jmsSendMessageVo.setContent(I18nUtil.getValueByLangAndKeyAndParams(k, contentVo.getMessageKey(),
                        contentVo.getParams()));
            }
            jmsSendMessageInfoHelperService.sendTextMessage(jmsSendMessageVo, false);
        });
    }
    /**
     * 发送动态OA消息
     *
     * @param titleVos         标题的国际化集合,不允许为NULL
     * @param contentVos       内容的国际化集合,不允许为NULL
     * @param userList         发送的人员ID集合,不允许为empty
     * @param jmsSendMessageVo 消息信息设置,不允许为NULL
     */
    public void sendOAMessageLogic(List<JmsContentParamsVo> titleVos,
                                   List<JmsContentParamsVo> contentVos,
                                   List<EnterpriseUserDO> userList,
                                   JmsSendMessageVo jmsSendMessageVo) {
        if (Objects.isNull(titleVos)
                || Objects.isNull(contentVos)
                || Objects.isNull(jmsSendMessageVo)
                || CollectionUtils.isEmpty(userList)
                || (CollectionUtils.isEmpty(titleVos) && CollectionUtils.isEmpty(contentVos))) {
            return;
        }
        Map<String, List<String>> languageUsersMap = this.buildLanguageUsersMap(userList);
        if (StringUtils.isBlank(jmsSendMessageVo.getDingCorpId())) {
            jmsSendMessageVo.setDingCorpId(UserHolder.getUser().getDingCorpId());
        }
        languageUsersMap.forEach((k, v) -> {
            // 标题
            StringBuilder titleBuilder = new StringBuilder("");
            Map<I18nMessageKeyEnum, String> titleMap = I18nUtil.getValuesByLangAndKeysAndParams(k,
                    titleVos.stream().filter(s -> StringUtils.isBlank(s.getContent()))
                            .collect(Collectors.toMap(JmsContentParamsVo::getMessageKey,
                                    JmsContentParamsVo::getParams, (a, b) -> a)));
            for (JmsContentParamsVo title : titleVos) {
                if (StringUtils.isNotBlank(title.getContent())) {
                    titleBuilder.append(title.getContent());
                } else {
                    titleBuilder.append(titleMap.getOrDefault(title.getMessageKey(), ""));
                }
            }
            // 内容
            StringBuilder contentBuilder = new StringBuilder("");
            Map<I18nMessageKeyEnum, String> contentMap = I18nUtil.getValuesByLangAndKeysAndParams(k,
                    contentVos.stream().filter(s -> StringUtils.isBlank(s.getContent()))
                            .collect(Collectors.toMap(JmsContentParamsVo::getMessageKey,
                                    JmsContentParamsVo::getParams, (a, b) -> a)));
            for (JmsContentParamsVo content : contentVos) {
                if (StringUtils.isNotBlank(content.getContent())) {
                    contentBuilder.append(content.getContent());
                } else {
                    contentBuilder.append(contentMap.getOrDefault(content.getMessageKey(), ""));
                }
            }
            if (StrUtil.isNotBlank(titleBuilder.toString())) {
                jmsSendMessageVo.setTitle(titleBuilder.toString());
            }
            jmsSendMessageVo.setContent(contentBuilder.toString());
            jmsSendMessageVo.setUserIds(v);
            jmsSendMessageInfoHelperService.sendOAMessageLogicDynamic(jmsSendMessageVo, false,true);
        });
    }

    /**
     * 构建语言环境人员分组映射
     *
     * @param userList
     * @return
     */
    private Map<String, List<String>> buildLanguageUsersMap(List<EnterpriseUserDO> userList) {
        return userList.stream().collect(Collectors.groupingBy(EnterpriseUserDO::getLanguage, Collectors.mapping(
                EnterpriseUserDO::getUserId, Collectors.toList())));
    }
}
