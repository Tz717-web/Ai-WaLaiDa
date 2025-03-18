package com.walaida.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walaida.springbootinit.common.ErrorCode;
import com.walaida.springbootinit.constant.CommonConstant;
import com.walaida.springbootinit.exception.ThrowUtils;
import com.walaida.springbootinit.model.dto.scoringresult.ScoringResultQueryRequest;
import com.walaida.springbootinit.model.entity.ScoringResult;
import com.walaida.springbootinit.model.entity.User;
import com.walaida.springbootinit.model.entity.mapper.ScoringResultMapper;
import com.walaida.springbootinit.model.vo.ScoringResultVO;
import com.walaida.springbootinit.model.vo.UserVO;
import com.walaida.springbootinit.service.AppService;
import com.walaida.springbootinit.service.ScoringResultService;
import com.walaida.springbootinit.service.UserService;
import com.walaida.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 评分结果服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResult> implements ScoringResultService {

    @Resource
    private UserService userService;
    @Resource
    private AppService appService;

    /**
     * 校验数据
     *
     * @param scoringResult
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validScoringResult(ScoringResult scoringResult, boolean add) {
        ThrowUtils.throwIf(scoringResult == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String ScoringName = scoringResult.getResultName();
        Long appId = scoringResult.getAppId();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(ScoringName), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId非法");

        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(ScoringName)) {
            ThrowUtils.throwIf(ScoringName.length() > 128, ErrorCode.PARAMS_ERROR, "结果名称不能超过128");
        }
    }

    /**
     * 获取查询条件
     *
     * @param scoringResultQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ScoringResult> getQueryWrapper(ScoringResultQueryRequest scoringResultQueryRequest) {
        QueryWrapper<ScoringResult> queryWrapper = new QueryWrapper<>();
        if (scoringResultQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        // 从scoringResultQueryRequest对象中获取评分结果ID
        Long id = scoringResultQueryRequest.getId();
        // 从scoringResultQueryRequest对象中获取评分结果名称
        String resultName = scoringResultQueryRequest.getResultName();
        // 从scoringResultQueryRequest对象中获取评分结果描述
        String resultDesc = scoringResultQueryRequest.getResultDesc();
        // 从scoringResultQueryRequest对象中获取评分结果图片URL
        String resultPicture = scoringResultQueryRequest.getResultPicture();
        // 从scoringResultQueryRequest对象中获取评分结果属性
        String resultProp = String.valueOf(scoringResultQueryRequest.getResultProp());
        // 从scoringResultQueryRequest对象中获取评分结果分数范围
        Integer resultScoreRange = scoringResultQueryRequest.getResultScoreRange();
        // 从scoringResultQueryRequest对象中获取应用ID
        Long appId = scoringResultQueryRequest.getAppId();
        // 从scoringResultQueryRequest对象中获取用户ID
        Long userId = scoringResultQueryRequest.getUserId();

        // 从scoringResultQueryRequest对象中获取搜索文本
        String searchText = scoringResultQueryRequest.getSearchText();

        // 从scoringResultQueryRequest对象中获取排序字段
        String sortField = scoringResultQueryRequest.getSortField();
        // 从scoringResultQueryRequest对象中获取排序顺序
        String sortOrder = scoringResultQueryRequest.getSortOrder();
        //  补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(resultName), "resultName", resultName);
        queryWrapper.ne(ObjectUtils.isNotEmpty(resultDesc), "resultDesc", resultDesc);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取评分结果封装
     *
     * @param scoringResult
     * @param request
     * @return
     */
    @Override
    public ScoringResultVO getScoringResultVO(ScoringResult scoringResult, HttpServletRequest request) {
        // 对象转封装类
        ScoringResultVO scoringResultVO = ScoringResultVO.objToVo(scoringResult);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = scoringResult.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        scoringResultVO.setUser(userVO);
        return scoringResultVO;
    }

    /**
     * 分页获取评分结果封装
     *
     * @param scoringResultPage
     * @param request
     * @return
     */
    @Override
    public Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringResultPage, HttpServletRequest request) {
        List<ScoringResult> scoringResultList = scoringResultPage.getRecords();
        Page<ScoringResultVO> scoringResultVOPage = new Page<>(scoringResultPage.getCurrent(), scoringResultPage.getSize(), scoringResultPage.getTotal());
        if (CollUtil.isEmpty(scoringResultList)) {
            return scoringResultVOPage;
        }
        // 对象列表 => 封装对象列表
        List<ScoringResultVO> scoringResultVOList = scoringResultList.stream().map(scoringResult -> {
            return ScoringResultVO.objToVo(scoringResult);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = scoringResultList.stream().map(ScoringResult::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        scoringResultVOPage.setRecords(scoringResultVOList);
        return scoringResultVOPage;
    }

}
