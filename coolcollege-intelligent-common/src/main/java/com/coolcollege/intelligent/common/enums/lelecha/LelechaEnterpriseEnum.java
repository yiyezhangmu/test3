package com.coolcollege.intelligent.common.enums.lelecha;

import org.apache.commons.lang3.StringUtils;

/**
 * @author wxp
 * @FileName: LelechaEnterpriseEnum
 * @Description: 乐乐茶企业
 */
@Deprecated
public enum LelechaEnterpriseEnum {


    LELECHA("b21f26992b3748138fa16afcb1923e8b","上海吉茶餐饮管理有限公司"),
    XUJI("54f95eacda80412c97e541b4983343bc","徐记"),
    XICHAO("34a822e60ce8467a96e4d0de2f0da8d4","栖巢咖啡馆"),
    YUSU("ff75d089540e432c9d444776de48ab3c","宇宿酒店管理有限公司"),
    SHUDUFENG("0a988fce73294e71ac9f381c901a3a25","蜀都丰"),
    SIPAKE("ddd25aa1e6264227b2eab05e8bbe7dbf","厦门思帕克餐饮管理有限公司"),
    ZHANFU("01292016b32a4805ad7c8c1c56256353","南昌炙烧战斧餐饮管理有限公司"),
    SHENGSHI("f266f8518abb4f85b6c498e1b4f10091","西安盛世景程餐饮管理有限公司"),
    GUANGRUI("f49ccbf0a0394372982ba619fbb9e817","广瑞医疗"),
    NEWFIND_1("cf629c955fbf44689ee2c304282ad1e5","新发现餐饮"),
    NEWFIND_2("f90a36df7ad24961865e1a3fed96e8f3","新发现餐饮"),
    JP("c96a3b12035f4df59058826bc29bf1ba","深圳鸟鹏居酒屋餐饮服务有限公司"),
    XJX("0a91292089d14e1ea02425ec5e2b5c1d","小江溪"),
    HANHONGSHIBANROU("56567b456f744c179517a5b0e9444ff6","石家庄市裕华区韩红石板肉槐北店"),
    TEST("83bad0f3225a4de3b9feb2056403d52e","店务体验"),
    HYYM("432fb7aabfe741b1b4fabe2512a21261", "和颜一美"),
    SPK("ddd25aa1e6264227b2eab05e8bbe7dbf", "厦门思帕克餐饮"),
    MUTONGJI("bdfed0f3bac74480bd764a621e000aa6", "合肥木桶记餐饮管理有限公司"),
    SYYJCY("8cbdcd1fc0644268bfe38b473b799583", "沈阳壹玖餐饮管理有限公司"),
    XZTT("aaa5fbd5ec9a4c10a7c12dd4557d0680", "小资太太餐饮有限公司"),
    XZTT_DINGDING("63a84be68bbc46ee9277796549ac20e7", "小资太太餐饮有限公司"),
    FUBUFU("df94970f82364249bc797f8a85645989", "北京福不福管理咨询有限公司"),
    JIHAIYICHUANG("2a41e55577d347da8da4b2e0fac272c9", "甘肃集海宜创品牌管理有限公司"),
    ;

    private String code;

    private String message;

    LelechaEnterpriseEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
