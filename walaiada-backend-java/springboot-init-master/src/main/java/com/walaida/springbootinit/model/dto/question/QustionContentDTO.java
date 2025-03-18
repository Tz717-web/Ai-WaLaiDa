package com.walaida.springbootinit.model.dto.question;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QustionContentDTO {
    //题目标题
    private String title;
    //题目选项列表
    private List<Option> options;
//    题目选项
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private String result;
        private int score;
        private String value;
        private String key;
    }
}
