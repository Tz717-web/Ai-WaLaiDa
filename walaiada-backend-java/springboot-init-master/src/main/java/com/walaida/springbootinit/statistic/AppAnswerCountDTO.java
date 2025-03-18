package com.walaida.springbootinit.statistic;

import lombok.Data;

/**
 * 应用答题统计
 */
@Data
public class AppAnswerCountDTO {
    /**
     * appid
     */
    private  Long appId;
    /**
     * 提交的答案数
     */
    private  Long answerCount;

}
