package com.walaida.springbootinit.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.walaida.springbootinit.model.dto.question.QustionContentDTO;
import com.walaida.springbootinit.model.entity.App;
import com.walaida.springbootinit.model.entity.Question;
import com.walaida.springbootinit.model.entity.ScoringResult;
import com.walaida.springbootinit.model.entity.UserAnswer;
import com.walaida.springbootinit.model.vo.QuestionVO;
import com.walaida.springbootinit.service.QuestionService;
import com.walaida.springbootinit.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//得分类应用
// 标记该类为评分策略配置，指定应用类型和评分策略类型
@ScoringStrategyConfig(appType = 0,scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {
    // 注入QuestionService用于查询题目信息
    @Resource
    private QuestionService questionService;

    // 注入ScoringResultService用于查询评分结果信息
    @Resource
    private ScoringResultService scoringResultService;

    // 实现ScoringStrategy接口的doScore方法，进行评分
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {

        //根据id查询用户题目和结果信息 ，按照降序的顺序排列
        Long appId = app.getId();
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, app.getId()));

        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)); // 按照降序排列
        //统计用户总得分
        // 初始化总分数为0
        int totalScore = 0;
        // 将传入的question对象转换为QuestionVO对象
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 获取QuestionVO对象中的问题内容列表
        List<QustionContentDTO> questionContent = questionVO.getQuestionContent();

          int i= 0;
        // 遍历问题内容列表
        for (QustionContentDTO  questionContentDTO: questionContent) {
            // 遍历用户选择的答案列表
            String answer = choices.get(i++);
                // 获取当前问题内容中的选项列表
                for (QustionContentDTO.Option option : questionContentDTO.getOptions()) {
                    // 如果选项的键与用户选择的答案匹配
                    if (option.getKey().equals(answer)) {
                        // 获取该选项的分数，如果分数为空则默认为0
                        int score = Optional.of(option.getScore()).orElse(0);
                        // 将该选项的分数累加到总分数中
                          totalScore += score;

                    }
                }

        }





        //遍历用户总得分，找到用户对应的结果区间，作为总结果
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
        if (totalScore >= scoringResult.getResultScoreRange()) {
            maxScoringResult = scoringResult;
            break;
           }
        }
        //构造放回值，填充用户答案属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
