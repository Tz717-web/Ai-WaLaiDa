package com.walaida.springbootinit.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.walaida.springbootinit.aop.LogInterceptor;
import com.walaida.springbootinit.model.dto.question.QustionContentDTO;
import com.walaida.springbootinit.model.dto.scoringresult.ScoringResultQueryRequest;
import com.walaida.springbootinit.model.entity.App;
import com.walaida.springbootinit.model.entity.Question;
import com.walaida.springbootinit.model.entity.ScoringResult;
import com.walaida.springbootinit.model.entity.UserAnswer;
import com.walaida.springbootinit.model.vo.QuestionVO;
import com.walaida.springbootinit.service.QuestionService;
import com.walaida.springbootinit.service.ScoringResultService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//测评类应用
@ScoringStrategyConfig(appType = 1,scoringStrategy = 0)

public class CustomTestScoringStrategy implements ScoringStrategy {
    // 注入QuestionService用于获取题目信息
    @Resource
    private QuestionService questionService;

    // 注入ScoringResultService用于获取评分结果信息
    @Resource
    private ScoringResultService scoringResultService;
    @Autowired
    private LogInterceptor logInterceptor;

    // 实现ScoringStrategy接口的doScore方法，用于计算用户得分
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        //根据用户id查询题目和题目结果
        Long appId = app.getId();
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, app.getId()));

// 定义一个名为scoringResultList的List，用于存储ScoringResult类型的对象
        List<ScoringResult> scoringResultList = scoringResultService.list(
// 使用Wrappers类的lambdaQuery方法创建一个针对ScoringResult类的查询条件构造器
// Wrappers.lambdaQuery(ScoringResult.class) 创建了一个LambdaQueryWrapper对象，用于构建查询条件
// ScoringResult.class 指定了查询的对象类型为ScoringResult
// .eq(ScoringResult::getAppId, appId) 添加了一个等于条件，用于筛选ScoringResult对象中appId字段等于给定appId值的记录
// ScoringResult::getAppId 是一个方法引用，指向ScoringResult类的getAppId方法
// appId 是一个变量，表示要匹配的应用ID值
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, appId));
         //2. 统计用户每个选择对应的的属性个数，如 A =10个，E=5个。
                Map<String, Integer> optionCount = new HashMap<>();
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QustionContentDTO> questionContent = questionVO.getQuestionContent();
        int i=0;
//遍历题目列表
        for (QustionContentDTO questionContentDTO : questionContent) {
//遍历答案列表
            String answer = choices.get(i++);
//遍历题目中的选项
            for (QustionContentDTO.Option option : questionContentDTO.getOptions()) {
//如果答案和选项的key匹配
                if (option.getKey().equals(answer)) {
//获取选项的result属性
                    String result = option.getResult();

//如果result属性不在optionCount中，初始化为0
                    if (!optionCount.containsKey(result)) {
                        optionCount.put(result, 0);
                    }

//在optionCount中增加计数
                    optionCount.put(result, optionCount.get(result) + 1);
                }
            }
        }
        //3. 根据属性个数，计算用户得分并判断哪个评分更高
        ScoringResult maxScoringResult = scoringResultList.get(0);
        int maxScore =0;

        // 检查scoringResultList是否不为null且不为空
        if (scoringResultList != null && !scoringResultList.isEmpty()) {
            // 遍历scoringResultList中的每一个ScoringResult对象
            for (ScoringResult scoringResult : scoringResultList) {
                // 将scoringResult的resultProp属性转换为List<String>类型
                List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
                // 计算resultProp中每个元素在optionCount中的对应值之和
                int score = resultProp.stream()
                        // 将resultProp中的每个元素映射为optionCount中对应的值，如果不存在则默认为0
                        .mapToInt(prop -> optionCount.getOrDefault(prop, 0))
                        // 计算所有映射值的总和
                        .sum();
                // 如果当前计算的score大于maxScore，则更新maxScore和maxScoringResult
                if (score > maxScore) {
                    maxScore = score;
                    maxScoringResult = scoringResult;

                }
            }

        }
        //打印分数
        System.out.println("用户得分：" + maxScore);

        if (maxScoringResult == null) {
            throw new Exception("No valid scoring result found");
        }
        //4. 根据用户得分，判断用户属于哪个评分区间，并返回对应的评分结果。
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;
    }
}
