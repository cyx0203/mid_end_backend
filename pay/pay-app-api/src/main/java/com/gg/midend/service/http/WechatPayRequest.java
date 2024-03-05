package com.gg.midend.service.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class WechatPayRequest {
	
	public Map<String, Object> dopost(String url, String data) {
		HttpClient client;
		Map<String, Object> strResult = new HashMap<String, Object>();
		PostMethod post = new PostMethod(url.toString());
		try {
			SimpleHttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager(true);
			HttpConnectionManagerParams params = httpConnectionManager.getParams();
			params.setConnectionTimeout(5000);
			params.setSoTimeout(20000);
			params.setDefaultMaxConnectionsPerHost(1000);
			params.setMaxTotalConnections(1000);
			client = new HttpClient(httpConnectionManager);
			client.getParams().setContentCharset("utf-8");
			client.getParams().setHttpElementCharset("utf-8");
			RequestEntity requestEntity = new StringRequestEntity(data, "application/json", "utf-8");
			post.setRequestEntity(requestEntity);
			post.getParams().setContentCharset("utf-8");
			client.executeMethod(post);
			if (post.getStatusCode() == 200) {
				strResult.put("statuscode", post.getStatusCode() + "");
				InputStream inputStream = post.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				StringBuffer stringBuffer = new StringBuffer();
				String str = "";
				while ((str = br.readLine()) != null) {
					stringBuffer.append(str);
				}
				strResult.put("conResult", stringBuffer.toString()+"");
			}else {
				strResult.put("statuscode", post.getStatusCode() + "");
			}
		} catch (Exception e) {
			strResult.put("statuscode", "999");
		} finally {
			post.releaseConnection();
		}
		return strResult;
	}
}
