package com.walaida.springbootinit.scoring;

import com.walaida.springbootinit.model.entity.App;
import com.walaida.springbootinit.model.entity.UserAnswer;

import java.util.List;

public interface ScoringStrategy {
    /**
     * 评分策略
     *
     * @param choices
     * @param app
     * @return
     * @throws Exception
     */


    UserAnswer doScore(List<String> choices, App app) throws Exception;
}
