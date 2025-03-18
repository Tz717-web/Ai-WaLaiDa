package com.walaida.springbootinit.statistic;

import lombok.Data;

@Data
public class AppAnswerResultCountDTO {
    /**
     * 结果名称
     */
    private String resultName;
    /**
     * 结果数量
     */
    private String resultCount;

}
