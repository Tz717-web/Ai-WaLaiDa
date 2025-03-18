package com.walaida.springbootinit.scoring;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.walaida.springbootinit.aop.LogInterceptor;
import com.walaida.springbootinit.common.ResultUtils;
import com.walaida.springbootinit.manager.AiManager;
import com.walaida.springbootinit.model.dto.question.QuestionAnswerAiDTO;
import com.walaida.springbootinit.model.dto.question.QustionContentDTO;
import com.walaida.springbootinit.model.dto.scoringresult.ScoringResultQueryRequest;
import com.walaida.springbootinit.model.entity.App;
import com.walaida.springbootinit.model.entity.Question;
import com.walaida.springbootinit.model.entity.ScoringResult;
import com.walaida.springbootinit.model.entity.UserAnswer;
import com.walaida.springbootinit.model.vo.QuestionVO;
import com.walaida.springbootinit.service.QuestionService;
import com.walaida.springbootinit.service.ScoringResultService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Ai测试测评策略
 */
@ScoringStrategyConfig(appType = 1,scoringStrategy = 1)

public class AiTestScoringStrategy implements ScoringStrategy {
    // 注入QuestionService用于获取题目信息
    @Resource
    private QuestionService questionService;
    @Resource
    private AiManager aiManager;
    @Resource
    private RedissonClient redissonClient;
    //AI分布式锁的key
    private  static  final  String  AI_ANSWER_LOCK = "AI_ANSWER_LOCK";
    /**
     * Ai评分结果本地缓存
     */
    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder().initialCapacity(1024)
                    // 缓存5分钟移除
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .build();





    //系统prompt
    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（后面加上评价简述尽量不超过20个字，评价名称尽量简洁）和评价描述（尽量详细，并且要积极简单易懂并且不同的用户答案要有区分度，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";

    /**
     * ai评分系统消息
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @return
      */
    private String getAiTestScoringUserMessage(App app, List<QustionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerAiDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerAiDTO questionAnswerDTO = new QuestionAnswerAiDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    /**
     * 构建缓存key
     * @param appId
     * @param choices
     * @return
     */
    private  String buildCacheKey(Long appId, String choices) {
      return DigestUtil.md5Hex(appId+":"+choices);
    }



    /**
     * ai 评分结果封装
     * @param choices
     * @param app
     * @return
     * @throws Exception
     */
    // 实现ScoringStrategy接口的doScore方法，用于计算用户得分
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        //根据用户id查询题目信息
        Long appId = app.getId();
        //转换格式
        String jsonStr =  JSONUtil.toJsonStr(choices);
        //构造缓存
      String cacheKey =  buildCacheKey(appId,jsonStr);
      String answerJson = answerCacheMap.getIfPresent(cacheKey);
      if (StrUtil.isNotBlank(answerJson)){
          //如果有缓存，直接构造返回值
          UserAnswer userAnswer = JSONUtil.toBean(answerJson, UserAnswer.class);
          userAnswer.setAppId(appId);
          userAnswer.setAppType(app.getAppType());
          userAnswer.setScoringStrategy(app.getScoringStrategy());
          userAnswer.setChoices(jsonStr);
          return  userAnswer;
      }
      //定义锁
     RLock lock = redissonClient.getLock(AI_ANSWER_LOCK+cacheKey);
     try {
         //竞争锁
       boolean res =  lock.tryLock(3,15, TimeUnit.SECONDS);
       if (!res) {//没有获取到锁
           return null;
       }
         Question question = questionService.getOne(
                 Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, app.getId()));
         //获取题目列表
         Map<String, Integer> optionCount = new HashMap<>();
         QuestionVO questionVO = QuestionVO.objToVo(question);
         List<QustionContentDTO> questionContent = questionVO.getQuestionContent();
         //2.调用Ai 获取结果
         //封装prompt
         String userMessage = getAiTestScoringUserMessage(app, questionContent, choices);
         //AI生成结果
         String result = aiManager.doUnStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
         //截取需要的ai信息
         int startIndex = result.indexOf("{");
         int end = result.lastIndexOf("}");
         String json = result.substring(startIndex, end + 1);
         //如果没有缓存则缓存结果
         answerCacheMap.put(cacheKey, json);

         //4.根据结果计算得分
         //解析json
         UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
         userAnswer.setAppId(appId);
         userAnswer.setAppType(app.getAppType());
         userAnswer.setScoringStrategy(app.getScoringStrategy());
         userAnswer.setChoices(JSONUtil.toJsonStr(choices));
         return userAnswer;
     }//释放锁
     finally {
         if(lock != null && lock.isLocked()){
             if(lock.isHeldByCurrentThread()){
                 lock.unlock();
             }
         }
     }
    }
}
