package com.walaida.springbootinit.controller;

import com.walaida.springbootinit.common.BaseResponse;
import com.walaida.springbootinit.common.ErrorCode;
import com.walaida.springbootinit.common.ResultUtils;
import com.walaida.springbootinit.exception.ThrowUtils;
import com.walaida.springbootinit.model.entity.mapper.UserAnswerMapper;
import com.walaida.springbootinit.model.enums.FileUploadBizEnum;
import com.walaida.springbootinit.service.UserService;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.walaida.springbootinit.statistic.AppAnswerCountDTO;
import com.walaida.springbootinit.statistic.AppAnswerResultCountDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * App 统计分析接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class StatisicController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    /**
     * 统计前十应用数
     * @return
     */
    @GetMapping("/answerCount")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    /**
     * 统计用户回答数
     * @param appId
     * @return
     */
    @GetMapping("/answerResultCount")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR);

        return ResultUtils.success(userAnswerMapper.doAppAnswerCountByAppId(appId));
    }
}
