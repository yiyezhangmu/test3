package com.coolcollege.intelligent.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WxUtil {

	public static final String GET_AUTH_TOKEN_URL="https://api.weixin.qq.com/sns/oauth2/access_token";

	public static final String GET_USER_INFORMATION_URL="https://api.weixin.qq.com/sns/userinfo";

	public  static final Logger logger = LoggerFactory.getLogger(WxUtil.class);
	/**
	 * 微信auth2.0授权， 支持APP和H5JSSDK
	 * @param code
	 * @param appid
	 * @param secret
	 * @return   {"access_token":"ACCESS_TOKEN","expires_in":7200,"refresh_token":"REFRESH_TOKEN","openid":"OPENID", "scope":"SCOPE" }
	 */
	public static JSONObject getAccessAuthToken(String code,String appid,String secret) {
		String param = "appid=" + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
		String result= HttpRequest.sendGet(GET_AUTH_TOKEN_URL, param);
		logger.error("微信auth2.0token："+result);
		JSONObject json=JSON.parseObject(result);
		return json;
	}

	/**
	 * 
	 * {   
  "openid":" OPENID",
  "nickname": NICKNAME,
  "sex":"1",
  "province":"PROVINCE",
  "city":"CITY",
  "country":"COUNTRY",
  "headimgurl":       "http://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
  "privilege":[ "PRIVILEGE1" "PRIVILEGE2"     ],
  "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
}
	 * 获取微信用户信息
	 * @param token
	 * @param openId
	 * @param type
	 * @return
	 */
//	public static WxMemberH5VO getUserInfo(String token, String openId ) {
//		Map<String,String> map=new HashMap<String,String>();
//		map.put("access_token",token);
//		map.put("openid",openId);
//		map.put("lang", "zh_CN");
//		String result=HttpRequest.sendPost(GET_USER_INFORMATION_URL,map);
//		logger.error("获取微信用户信息："+result);
//		JSONObject json=JSONObject.parseObject(result);
//
//		if(json==null){
//			throw new BizException(CommonError.PARAM_ERROR.getCode(),"微信授权出错："+result);
//		}
//		WxMemberH5VO vo =	JSONObject.toJavaObject(json, WxMemberH5VO.class);
//		vo.setAccessToken(token);
//		return vo;
//	}

	/**
	 * 微信小程序，登楼授权
	 * @param code
	 * @param appid
	 * @param secret
	 * @return   {openid:xxx, session_key:xxxx,unionid:xxx}
	 */
//	public static JSONObject getAppletToken(String code,String appid,String secret){
//		Map<String,String> map=new HashMap<String,String>();
//		map.put("js_code",code);
//		map.put("grant_type","authorization_code");
//		map.put("appid", appid);
//		map.put("secret",secret);
//		String result=HttpRequest.sendGet(WxConstant.WX_APPLET_URL, map);
//		logger.error("微信小程序："+result);
//		JSONObject json=JSON.parseObject(result);
//		return json;
//	}
//
//	private static String getSex(String value) {
//		String sex="未知";
//		if(WxConstant.WX_SEX_MAN.equals(value)) {
//			return "男";
//		}else if(WxConstant.WX_SEX_WOMAN.equals(value)) {
//			return "女";
//		}
//		return sex;
//	}

}
