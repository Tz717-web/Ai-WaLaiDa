package com.walaida.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walaida.springbootinit.common.ErrorCode;
import com.walaida.springbootinit.constant.CommonConstant;
import com.walaida.springbootinit.exception.ThrowUtils;

import com.walaida.springbootinit.model.dto.question. QuestionQueryRequest;
import com.walaida.springbootinit.model.entity.App;
import com.walaida.springbootinit.model.entity. Question;


import com.walaida.springbootinit.model.entity.User;
import com.walaida.springbootinit.model.entity.mapper.QuestionMapper;
import com.walaida.springbootinit.model.vo. QuestionVO;
import com.walaida.springbootinit.model.vo.UserVO;
import com.walaida.springbootinit.service.AppService;
import com.walaida.springbootinit.service. QuestionService;
import com.walaida.springbootinit.service.UserService;
import com.walaida.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题目服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j

public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    /**
     * 校验数据
     *
     * @param question
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String questionContent = question.getQuestionContent();
        Long appId = question.getAppId();
        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(questionContent), ErrorCode.PARAMS_ERROR, "题目内容不能为空");
            ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId非法");
        }
        // 修改数据时，有参数则校验
        // 补充校验规则

        if (appId != null) {
            App app = appService.getById(appId);
            ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        }
    }



/**
 * 获取查询条件
 *
 * @param questionQueryRequest
 * @return
 */
@Override
public QueryWrapper< Question> getQueryWrapper( QuestionQueryRequest questionQueryRequest) {
    QueryWrapper< Question> queryWrapper = new QueryWrapper<>();
    if (questionQueryRequest == null) {
        return queryWrapper;
    }
    // todo 从对象中取值
    Long id = questionQueryRequest.getId();
    String questionContent = questionQueryRequest.getQuestionContent();
    Long appId = questionQueryRequest.getAppId();
    Long userId = questionQueryRequest.getUserId();
    String content = questionQueryRequest.getContent();
    String sortField = questionQueryRequest.getSortField();
    String sortOrder = questionQueryRequest.getSortOrder();
    List<String> tagList = questionQueryRequest.getTags();

    // todo 补充需要的查询条件

    // 模糊查询
    queryWrapper.like(StringUtils.isNotBlank(questionContent), "questionContent", questionContent);
    queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
    // JSON 数组查询
    if (CollUtil.isNotEmpty(tagList)) {
        for (String tag : tagList) {
            queryWrapper.like("tags", "\"" + tag + "\"");
        }
    }
    // 精确查询

    queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
    queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
    queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);

    // 排序规则
    queryWrapper.orderBy(SqlUtils.validSortField(sortField),
            sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
            sortField);
    return queryWrapper;
}

/**
 * 获取题目封装
 *
 * @param question
 * @param request
 * @return
 */
@Override
public  QuestionVO getQuestionVO( Question question, HttpServletRequest request) {
    // 对象转封装类
    QuestionVO questionVO =  QuestionVO.objToVo(question);

    // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
    // region 可选
    // 1. 关联查询用户信息
    Long userId = question.getUserId();
    User user = null;
    if (userId != null && userId > 0) {
        user = userService.getById(userId);
    }
    UserVO userVO = userService.getUserVO(user);
    questionVO.setUserVO(userVO);


    return questionVO;
}

/**
 * 分页获取题目封装
 *
 * @param questionPage
 * @param request
 * @return
 */
@Override
public Page< QuestionVO> getQuestionVOPage(Page< Question> questionPage, HttpServletRequest request) {
    List<Question> questionList = questionPage.getRecords();
    Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
    if (CollUtil.isEmpty(questionList)) {
        return questionVOPage;
    }
    // 对象列表 => 封装对象列表
    List<QuestionVO> questionVOList = questionList.stream().map(question -> {
        return QuestionVO.objToVo(question);
    }).collect(Collectors.toList());

    // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
    // region 可选
    // 1. 关联查询用户信息
    Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
    Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
            .collect(Collectors.groupingBy(User::getId));


    questionVOPage.setRecords(questionVOList);
    return questionVOPage;
}
}

