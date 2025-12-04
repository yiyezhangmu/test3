package com.coolcollege.intelligent.common.util;

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
    public static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(I18N_PATH, locale);
    }

}
