package com.candidxd.justchat.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yzk
 * @Title: IpUtil
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/204:58 PM
 */
public class IpUtil {

    public static String getIp(HttpServletRequest request) throws Exception {
        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    public static JSONObject baiduIpApi(String ip) {
        String ak = "8z9P0OiTMjRogOt55g6dGVhpqWLV2HI0";
        String url = "http://api.map.baidu.com/location/ip?ip=" + ip + "&ak=" + ak + "&coor=bd09ll";
        System.out.println(url);
        HttpMethod httpMethod = HttpMethod.GET;
        MultiValueMap<String, String> parms = new LinkedMultiValueMap<>();
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> responseEntity = template.getForEntity(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
        JSONObject jsonObject1 = jsonObject.getJSONObject("content");
        JSONObject jsonObject2 = jsonObject1.getJSONObject("address_detail");
        return jsonObject2;
    }
}
