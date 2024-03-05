package com.gg.midend.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

@Component
public class HttpUtils {

    /* HIS 接口地址 */
    private static String hisUrl;

    @Value("${his-config.HIS_URL}")
    public void setUrl(String hisUrl) {
        HttpUtils.hisUrl = hisUrl;
    }
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private final Random RANDOM = new Random();
    public MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public JSONObject sendRequest(Map<String, Object> hisBodyMap) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String json = buildJsonRequest(
                hisBodyMap.get("IdCardNo").toString(),
                (Integer) hisBodyMap.get("Number"),
                hisBodyMap.get("Punter").toString(),
                hisBodyMap.get("inpatient_seq").toString(),
                hisBodyMap.get("pat_name").toString());

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(hisUrl)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        JSONObject result = new JSONObject();

        if (response.isSuccessful()) {
            // 请求成功
            String responseBody = response.body().string();

            JSONObject jsonRsp = JSONObject.parseObject(responseBody);

            String exeStatus = jsonRsp.getJSONObject("data").getJSONObject("input").getJSONObject("ack_info").getString("exe_status");
            JSONObject reqInfo = jsonRsp.getJSONObject("data").getJSONObject("input").getJSONArray("req_info").getJSONObject(0);
            result.put("exeStatus", exeStatus);
            result.put("pid", reqInfo.getString("pid"));
            result.put("No", reqInfo.getString("No"));
        } else {
            result.put("exeStatus", "error");
        }

        return result;
    }

    private String buildJsonRequest(String idCardNo, Integer number, String punter, String inpatientSeq, String patName) {
        return "{\n" +
                "  \"tradeParam\": {\n" +
                "    \"input\": {\n" +
                "      \"head\": {\n" +
                "        \"action_no\": \"" + generateActionNo() + "\",\n" +
                "        \"bizno\": \"2129\",\n" +
                "        \"sysno\": \"GG\",\n" +
                "        \"tarno\": \"HIS\",\n" +
                "        \"time\": \"" + generateActionNo() + "\"\n" +
                "      },\n" +
                "      \"req_info\": [\n" +
                "        {\n" +
                "          \"IdCardNo\": \"" + idCardNo + "\",\n" +
                "          \"Number\": \"" + number + "\",\n" +
                "          \"Punter\": \"" + punter + "\",\n" +
                "          \"inpatient_seq\": \"" + inpatientSeq + "\",\n" +
                "          \"pat_name\": \"" + patName + "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"tradeType\": \"common\"\n" +
                "}";
    }

    private String generateActionNo() {
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(FORMATTER);
        int randomNum = RANDOM.nextInt(100_000_000);
        return formattedTime + String.format("%08d", randomNum);
    }
}
