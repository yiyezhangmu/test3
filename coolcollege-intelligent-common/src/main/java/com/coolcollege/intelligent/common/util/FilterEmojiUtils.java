package com.coolcollege.intelligent.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenyupeng
 * @since 2021/12/17
 */
@Slf4j
public class FilterEmojiUtils {
    /**
     * 正则
     */
    public static String REGEX = "(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)";
    /**
     * 过滤emoji 或者 其他非文字类型的字符
     * @author chenyupeng
     * @date 2021/12/17
     * @param source
     * @return java.lang.String
     */
    public static String filterEmoji(String source) {
        try {
            if (StringUtils.isBlank(source)) {
                return source;
            }
            //第一层过滤正则匹配 把表情替换为空
            source = replaceEmoji(source);
            StringBuilder buf = null;
            int len = source.length();
            for (int i = 0; i < len; i++) {
                char codePoint = source.charAt(i);
                if (isEmojiCharacter(codePoint)) {
                    if (buf == null) {
                        buf = new StringBuilder(source.length());
                    }
                    buf.append(codePoint);
                }
            }
            if (buf == null) {
                return source;
            } else {
                if (buf.length() == len) {
                    return source;
                } else {
                    return buf.toString();
                }
            }
        }catch (Exception e){
            log.error("filterEmoji has exception", e);
            return "";
        }

    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 正则匹配 查找表情字符进行替换
     * @param source
     * @return
     */
    public static String replaceEmoji(String source) {
        Pattern emoji = Pattern.compile(REGEX,Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE );
        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
            source = emojiMatcher.replaceAll("");
        }
        return source;
    }

    public static void main(String[] args) {
//        System.out.println(filterEmoji("\uD83D\uDC4D"));
        System.out.println(filterEmoji("hello\uD83D\uDC4Dhello\uD83D\uDC4D"));
        System.out.println(filterEmoji("\uD83E\uDD0E"));
        System.out.println(filterEmoji("\uD83D\uDE00"));
        System.out.println(filterEmoji("hello"));
        System.out.println(filterEmoji("{}}"));
        System.out.println(filterEmoji(",./'l;';plp'/.,。、。？··~"));
//        System.out.println(f("\uD83D\uDC4D"));
    }
}
