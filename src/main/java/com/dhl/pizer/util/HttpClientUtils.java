package com.dhl.pizer.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;

public class HttpClientUtils {

    /**
     * GET提交数据,并返回JSON格式的结果数据
     *
     * @return JSONObject or null if error or no response
     */
    public static JSONObject getForJsonResult(String reqUrl) {

        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet();

        JSONObject response = null;
        try {
            HttpResponse res = httpclient.execute(get);
            if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                if (StringUtils.isNoneBlank(result)) {
                    System.out.println("发送任务返回结果:" + result);
                } else {
                    System.out.println("调用接口的返回值为空！！！！");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    /**
     * post请求
     *
     * @param url
     * @param json
     * @return
     */
    public static JSONObject doPost(String url, JSONObject json) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        JSONObject response = null;
        try {
            StringEntity s = new StringEntity(json.toString());
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
        } catch (Exception e) {
            throw new RuntimeException("参数转换异常:" + e);
        }
        try {
            HttpResponse res = httpclient.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                if (StringUtils.isNoneBlank(result)) {
                    System.out.println("发送任务返回结果:" + result);
                } else {
                    System.out.println("调用接口的返回值为空！！！！");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

}
