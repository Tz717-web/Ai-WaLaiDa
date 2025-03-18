package com.walaida.springbootinit;

import com.walaida.springbootinit.controller.QuestionController;
import com.walaida.springbootinit.model.dto.question.AiGenerateQuestionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class QuestionControllerTest {
    @Resource
    private QuestionController questionController;
    @Test
    void aiGenerateQuestionSSETest(){
        //模拟调用
        AiGenerateQuestionRequest  aiGenerateQuestionRequest= new AiGenerateQuestionRequest();
        aiGenerateQuestionRequest.setAppId(3L);
        aiGenerateQuestionRequest.setQuestionNumber(10);
        aiGenerateQuestionRequest.setOptionNumber(2);
        //模拟普通用户
        questionController.aiGenerateQuestionSSETest(aiGenerateQuestionRequest, false);
        questionController.aiGenerateQuestionSSETest(aiGenerateQuestionRequest, false);
        questionController.aiGenerateQuestionSSETest(aiGenerateQuestionRequest, true);//vip用户
        //模拟主线程一直启动，便于观察
        try {
            Thread.sleep(1000000000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
