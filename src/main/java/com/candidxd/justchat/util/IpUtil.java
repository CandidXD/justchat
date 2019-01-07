package com.candidxd.justchat.util;

import com.alibaba.fastjson.JSONObject;

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
        if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1")) {
            ip = "60.176.144.88";
        }
        return ip;
    }

    public static JSONObject baiduIpApi(String ip) {
        // System.out.println(ip);
        // String ak = "8z9P0OiTMjRogOt55g6dGVhpqWLV2HI0";
        // String url = "http://api.map.baidu.com/location/ip?ip=" + ip + "&ak=" + ak + "&coor=bd09ll";
        // System.out.println(url);
        // HttpMethod httpMethod = HttpMethod.GET;
        // MultiValueMap<String, String> parms = new LinkedMultiValueMap<>();
        // RestTemplate template = new RestTemplate();
        // ResponseEntity<String> responseEntity = template.getForEntity(url, String.class);
        // JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
        // System.out.println(jsonObject.toJSONString());
        // JSONObject jsonObject1 = jsonObject.getJSONObject("content");
        // System.out.println(jsonObject1.toJSONString());
        JSONObject jsonObject1 = JSONObject.parseObject("{\"address_detail\":{\"province\":\"浙江省\",\"city\":\"杭州市\",\"street\":\"\",\"district\":\"\",\"street_number\":\"\",\"city_code\":179},\"address\":\"浙江省杭州市\",\"point\":{\"x\":\"120.21937542\",\"y\":\"30.25924446\"}}");
        JSONObject jsonObject2 = jsonObject1.getJSONObject("address_detail");
        return jsonObject2;
    }
}
