package com.walaida.springbootinit.manager;

import com.walaida.springbootinit.common.ErrorCode;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用Ai请求管理器
 */
@Component
public class AiManager {
    @Resource
    private ClientV4 clientV4;
    private static  final float STABLE_TEMPERATURE = 0.03f;
    private static  final float UNSTABLE_TEMPERATURE = 0.99f;

    /**
     * 不稳定型通用性同步请求（答案比较随机）
     * @param systemMessages
     * @param userMessages
     * @return
     */

    public  String doStableRequest(String systemMessages,String userMessages ){

        return doSyncRequest(systemMessages, userMessages,UNSTABLE_TEMPERATURE);
    }

    /**
     * 稳定型通用性同步请求（答案较准确）
     * @param systemMessages
     * @param userMessages
     * @return
     */
    public  String doUnStableRequest(String systemMessages,String userMessages){
        return doSyncRequest( systemMessages, userMessages,STABLE_TEMPERATURE);
    }
    /**
     * 简化型通用性同步请求
     * @param systemMessages
     * @param userMessages
     * @param temperature
     * @return
     */

    public  String doSyncRequest(String systemMessages,String userMessages ,Float temperature){

        return doMessageRequest(systemMessages, userMessages,Boolean.FALSE,temperature);
    }
    /**
     * 简化型通用请求
     * @param userMessages
     * @param  systemMessages
     * @param stream
     * @param temperature
     * @return
     */
    public  String doMessageRequest(String systemMessages,String userMessages ,Boolean stream,Float temperature){
   List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessages = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessages);
        messages.add(systemChatMessages);
        ChatMessage userChatMessages = new ChatMessage(ChatMessageRole.USER.value(), userMessages);
        messages.add(userChatMessages);
        return doRequest(messages,stream,temperature);
    }

    /**
     * 通用请求
     * @param messages
     * @param stream
     * @param temperature
     * @return
     */
    public  String doRequest(List<ChatMessage> messages,Boolean stream,Float temperature){
        String requestId = String.valueOf(System.currentTimeMillis());
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();

            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            ChatMessage result = invokeModelApiResp.getData().getChoices().get(0).getMessage();
            return result.getContent().toString();

    }


    /**
     * 简化型流式通用请求
     * @param userMessages
     * @param  systemMessages
     * @param temperature
     * @return
     */
    public  Flowable<ModelData> doStreamMessageRequest(String systemMessages,String userMessages ,Float temperature){
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessages = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessages);
        messages.add(systemChatMessages);
        ChatMessage userChatMessages = new ChatMessage(ChatMessageRole.USER.value(), userMessages);
        messages.add(userChatMessages);
        return doStreamRequest(messages,temperature);
    }



    /**
     * 通用流式请求
     * @param messages
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> messages, Float temperature){
       //构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();

        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);

        return invokeModelApiResp.getFlowable();
    }





}
