package com.dhl.pizer.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import java.nio.charset.Charset;

@Slf4j
public class HttpClientUtils {

    /**
     * GET提交数据,并返回JSON格式的结果数据
     *
     * @return JSONObject or null if error or no response
     */
    public static JSONObject getForJsonResult(String reqUrl) {

        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(reqUrl);
        log.info("请求url: " + reqUrl);

        JSONObject response = null;
        try {
            HttpResponse res = httpclient.execute(get);
            if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                if (StringUtils.isNoneBlank(result)) {
                    System.out.println("发送任务返回结果:" + result);
                    return JSON.parseObject(result);
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
            StringEntity entity = new StringEntity(json.toString(), Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);
        } catch (Exception e) {
            throw new RuntimeException("参数转换异常:" + e);
        }

        log.info("req json = " + JSON.toJSON(json));

        try {
            HttpResponse res = httpclient.execute(post);
            log.info("res code = " + JSON.toJSON(res.getStatusLine().getStatusCode()));
            log.info("res message = " + EntityUtils.toString(res.getEntity()));

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
