package com.walaida.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.walaida.springbootinit.model.dto.question.QustionContentDTO;
import com.walaida.springbootinit.model.entity. Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class  QuestionVO implements Serializable {
    /**
     * id
     */

    private Long id;

    /**
     * 题目内容（json格式）
     */
    private List<QustionContentDTO> questionContent;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private UserVO userVO;

    /**
     * 封装类转对象
     *
     * @param questionVO
     * @return
     */
    public static  Question voToObj( QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
         Question question = new  Question();
        List<QustionContentDTO> questionContentDTO = questionVO.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContentDTO));
        BeanUtils.copyProperties(questionVO, question);

        return question;
    }

    /**
     * 对象转封装类
     *
     * @param question
     * @return
     */
    public static  QuestionVO objToVo( Question question) {
        if (question == null) {
            return null;
        }
         QuestionVO questionVO = new  QuestionVO();
        BeanUtils.copyProperties(question, questionVO);

        questionVO.setQuestionContent(JSONUtil.toList(question.getQuestionContent(), QustionContentDTO.class));
        return questionVO;
    }
}
