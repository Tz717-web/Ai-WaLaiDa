package com.walaida.springbootinit.scoring;

import com.walaida.springbootinit.common.ErrorCode;
import com.walaida.springbootinit.exception.BusinessException;
import com.walaida.springbootinit.model.entity.App;
import com.walaida.springbootinit.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ScoringStrategyExecutor {

    // 策略列表
    @Resource
    private List<ScoringStrategy> scoringStrategyList;


    /**
     * 评分
     *
     * @param choiceList
     * @param app
     * @return
     * @throws Exception
     */
    public UserAnswer doScore(List<String> choiceList, App app) throws Exception {
        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();
        if (appType == null || appScoringStrategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }
        // 根据注解获取策略
        // 遍历scoringStrategyList中的每一个ScoringStrategy对象
        for (ScoringStrategy strategy : scoringStrategyList) {
            // 检查当前策略类是否被ScoringStrategyConfig注解标记
            if (strategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)) {
                // 获取当前策略类上的ScoringStrategyConfig注解实例
                ScoringStrategyConfig scoringStrategyConfig = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                // 检查注解中的appType和scoringStrategy是否与传入的appType和appScoringStrategy匹配
                if (scoringStrategyConfig.appType() == appType && scoringStrategyConfig.scoringStrategy() == appScoringStrategy) {
                    // 如果匹配，则执行当前策略的doScore方法并返回结果
                    return strategy.doScore(choiceList, app);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
