package com.coolcollege.intelligent.util.i18n;

import com.coolcollege.intelligent.common.constant.i18n.I18nLangTypeEnum;
import com.coolcollege.intelligent.common.constant.i18n.I18nMessageKeyEnum;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;

public class I18nUtil {

    private static final String I18N_PATH = "i18n.messages";

    /**
     * 根据语言环境和配置文件key以及变量获取配置项被替换后的内容
     *
     * @param lang
     * @param key
     * @param params
     * @return
     */
    public static String getValueByLangAndKeyAndParams(String lang, I18nMessageKeyEnum key, String[] params) {
        if (Objects.isNull(key)) {
            return "";
        }
        return MessageFormat.format(getValueByLangAndKey(lang, key.getValue()), params);
    }

    /**
     * 根据语言环境和配置文件key以及变量获取配置项被替换后的内容
     *
     * @param lang
     * @param keyParamsMap
     * @return
     */
    public static Map<I18nMessageKeyEnum, String> getValuesByLangAndKeysAndParams(String lang, Map<I18nMessageKeyEnum, String[]> keyParamsMap) {
        if (Objects.isNull(keyParamsMap)) {
            return Collections.EMPTY_MAP;
        }
        Map<I18nMessageKeyEnum, String> result = Maps.newHashMapWithExpectedSize(keyParamsMap.size());
        ResourceBundle bundle = getResourceBundle(getLocaleByLang(lang));
        keyParamsMap.forEach((k, v) -> {
            result.put(k, MessageFormat.format(bundle.getString(k.getValue()), v));
        });
        return result;
    }

    /**
     * 根据语言环境和配置文件key获取配置项内容
     *
     * @param lang
     * @param key
     * @return
     */
    public static String getValueByLangAndKey(String lang, String key) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        ResourceBundle bundle = getResourceBundle(getLocaleByLang(lang));
        return bundle.getString(key);
    }

    public static String getDefaultValue(String key) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        ResourceBundle bundle = getResourceBundle(getLocaleByLang("zh_cn"));
        return bundle.getString(key);
    }

    /**
     * 根据语言返回当前语言环境
     *
     * @param lang
     * @return
     */
    public static Locale getLocaleByLang(String lang) {
        Locale locale = Locale.getDefault();
        I18nLangTypeEnum i18nLangTypeEnum = I18nLangTypeEnum.parseValue(lang);
        if (Objects.nonNull(i18nLangTypeEnum)) {
            String language = lang.split("_")[0];
            String country = lang.split("_")[1];
            locale = new Locale(language, country);
        }
        return locale;
    }

    /**
     * 根据语言环境找到配置信息
     *
     * @param locale
     * @return
     */
    private static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(I18N_PATH, locale);
    }

    /**
     * 根据语言环境和配置文件keys获取配置项内容
     *
     * @param lang
     * @param keys
     * @return 返回键值对
     */
    public static Map<String, String> getMapBuyLangAndKeys(String lang, List<String> keys) {
        if (CollectionUtils.isNotEmpty(keys)) {
            ResourceBundle bundle = getResourceBundle(getLocaleByLang(lang));
            Map<String, String> result = Maps.newHashMap();
            keys.forEach(s -> {
                result.put(s, bundle.getString(s));
            });
            return result;
        }
        return Collections.EMPTY_MAP;
    }
}
