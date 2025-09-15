package com.xiaobaitiao.springbootinit;

import cn.hutool.core.date.StopWatch;
import com.xiaobaitiao.springbootinit.common.BaseResponse;
import com.xiaobaitiao.springbootinit.common.ResultUtils;
import com.xiaobaitiao.springbootinit.manager.SparkClient;
import com.xiaobaitiao.springbootinit.manager.model.SparkMessage;
import com.xiaobaitiao.springbootinit.manager.model.SparkSyncChatResponse;
import com.xiaobaitiao.springbootinit.manager.model.request.SparkRequest;
import com.xiaobaitiao.springbootinit.manager.model.response.SparkTextUsage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 程序员小白条
 * @date 2025/2/7 18:42
 * @gitee https://gitee.com/falle22222n-leaves/vue_-book-manage-system
 */
@SpringBootTest
public class SparkAITest {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    SparkClient sparkClient = new SparkClient();
    {
        sparkClient.appid = "xxxxxxxxxxxx";
        sparkClient.apiKey = "xxxxxxxxxxxx";
        sparkClient.apiSecret = "xxxxxxxxxxxx";
    }
    @Test
    public void test()  {
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.userContent("怎么学习 Java?"));
        String response;
        int timeout = 35; // 超时时间，单位为秒
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                .messages(messages)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                .build();
        Future<String> future = threadPoolExecutor.submit(() -> {
            try {
                // 同步调用
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
                SparkTextUsage textUsage = chatResponse.getTextUsage();
                stopWatch.stop();
                long total = stopWatch.getTotal(TimeUnit.SECONDS);
                System.out.println("本次接口调用耗时:" + total + "秒");
                System.out.println("\n回答：" + chatResponse.getContent());
                System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
                        + "，回答tokens：" + textUsage.getCompletionTokens()
                        + "，总消耗tokens：" + textUsage.getTotalTokens());
                return chatResponse.getContent();
//                return AlibabaAIModel.doChatWithHistory(stringBuilder.toString(),recentHistory);
            } catch (Exception exception) {
                throw new RuntimeException("遇到异常");
            }
        });
        try {
            response = future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response);
    }

}
