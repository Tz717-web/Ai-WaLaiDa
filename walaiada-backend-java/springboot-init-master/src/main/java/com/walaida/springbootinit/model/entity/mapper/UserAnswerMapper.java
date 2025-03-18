package com.walaida.springbootinit.model.entity.mapper;

import com.walaida.springbootinit.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walaida.springbootinit.statistic.AppAnswerCountDTO;
import com.walaida.springbootinit.statistic.AppAnswerResultCountDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
* @createDate 2025-02-28 17:49:30
* @Entity com.walaida.springbootinit.model.entity.UserAnswer
*/
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {
   //查询每个应用的用户答题数前10
    @Select("select  appId,count( userId) as answerCount from user_answer\n" +
            "   group by appId order by answerCount desc limit 10;")
    List<AppAnswerCountDTO> doAppAnswerCount();
    //查询每个应用的用户答案数
    @Select("select  resultName,count( resultName) as resultCount from user_answer\n" +
            "     where  appId = #{appId}  group by resultName order by resultCount desc;")
    List<AppAnswerResultCountDTO> doAppAnswerCountByAppId(Long appId);
}




