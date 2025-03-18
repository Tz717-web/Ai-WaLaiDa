package com.walaida.springbootinit.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walaida.springbootinit.annotation.AuthCheck;
import com.walaida.springbootinit.common.BaseResponse;
import com.walaida.springbootinit.common.DeleteRequest;
import com.walaida.springbootinit.common.ErrorCode;
import com.walaida.springbootinit.common.ResultUtils;
import com.walaida.springbootinit.constant.UserConstant;
import com.walaida.springbootinit.exception.BusinessException;
import com.walaida.springbootinit.exception.ThrowUtils;
import com.walaida.springbootinit.manager.AiManager;
import com.walaida.springbootinit.model.dto.question.*;
import com.walaida.springbootinit.model.entity.App;
import com.walaida.springbootinit.model.entity. Question;
import com.walaida.springbootinit.model.entity.User;
import com.walaida.springbootinit.model.enums.AppTypeEnum;
import com.walaida.springbootinit.model.vo. QuestionVO;
import com.walaida.springbootinit.service.AppService;
import com.walaida.springbootinit.service. QuestionService;
import com.walaida.springbootinit.service.UserService;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 题目接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class  QuestionController {

    @Resource
    private  QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    @Resource
    private AiManager aiManager;
    @Resource
    private Scheduler  vipScheduler;

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody  QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new  Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<QustionContentDTO> qustionContentDTO=questionAddRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(qustionContentDTO));
        // 数据校验
        questionService.validQuestion(question, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody  QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new  Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<QustionContentDTO> qustionContentDTO=questionUpdateRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(qustionContentDTO));
        // 数据校验
        questionService.validQuestion(question, false);
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse< QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取题目列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page< Question>> listQuestionByPage(@RequestBody  QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 查询数据库
        Page< Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取题目列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page< QuestionVO>> listQuestionVOByPage(@RequestBody  QuestionQueryRequest questionQueryRequest,
                                                                HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page< Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page< QuestionVO>> listMyQuestionVOByPage(@RequestBody  QuestionQueryRequest questionQueryRequest,
                                                                  HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page< Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑题目（给用户使用）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody  QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new  Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<QustionContentDTO> qustionContentDTO=questionEditRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(qustionContentDTO));
        // 数据校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    // region Ai生成功能
    //系统预设Ai角色
    private static final String GENERATE_QUESTION_SYSTEM_MESSAGE = "你是一位严谨的出题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "应用类别，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复\n" +
            "2. 严格按照下面的 json 格式输出题目和选项\n" +
            "```\n" +
            "[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\"},{\"value\":\"选项内容\",\"key\":\"B\"}],\"title\":\"题目标题\"}]\n" +
            "```\n" +
            "title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容,生成的每个value都是通过它的title提问来生成的逻辑内容并且内容不能为空\n" +
            "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
            "4. 返回的题目列表格式必须为 JSON 数组";


    /**
     * 生成题目的用户消息
     * @param app
     * @param questionNumber
     * @param optionNumber
     * @return
     */
    private String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append(AppTypeEnum.getEnumByValue(app.getAppType()).getText() + "类").append("\n");
        userMessage.append(questionNumber).append("\n");
        userMessage.append(optionNumber);
        return userMessage.toString();
    }
@PostMapping ("/ai_generate")
    public BaseResponse<List<QustionContentDTO>> aiGenerateQuestion(@RequestBody AiGenerateQuestionRequest aiGenerateQuestionRequest) {
          ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        //获取参数
    Long appId = aiGenerateQuestionRequest.getAppId();
    int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
    int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
    //获取应用信息
    App app = appService.getById(appId);
    ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
    //封装Prompt
    String userMessage =getGenerateQuestionUserMessage(app,questionNumber,optionNumber);
    //Ai生成(稳定型同步Ai)
    String result=  aiManager.doSyncRequest(GENERATE_QUESTION_SYSTEM_MESSAGE,userMessage,null);
    //截取需要的ai信息
    int startIndex = result.indexOf("[");
    int end=result.lastIndexOf("]");
   String json=result.substring(startIndex,end+1);
    //解析json
    List<QustionContentDTO> qustionContentDTOList = JSONUtil.toList(json,QustionContentDTO.class);
    return ResultUtils.success(qustionContentDTOList) ;
}


    @GetMapping ("/ai_generate/sse")
    public SseEmitter aiGenerateQuestionSSE( AiGenerateQuestionRequest aiGenerateQuestionRequest ,HttpServletRequest request) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        //获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        //获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        //封装Prompt
        String userMessage =getGenerateQuestionUserMessage(app,questionNumber,optionNumber);
        //建立sse 连接对象 ，0表示永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        //Ai生成(稳定型同步Ai),sse流式返回
        Flowable<ModelData> modelDataFlowable=  aiManager.doStreamMessageRequest(GENERATE_QUESTION_SYSTEM_MESSAGE,userMessage,null);

        // 左括号计数器，除了默认值外，当回归为 0 时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger(0);
// 拼接完整题目
        StringBuilder stringBuilder = new StringBuilder();
        //默认非vip线程池
        Scheduler scheduler = Schedulers.io();
        User loginUser = userService.getLoginUser(request);
      //  如果是vip或者管理员就用VIP线程池

        if("vip".equals(loginUser.getUserRole()) ||"admin".equals(loginUser.getUserRole())){
            scheduler = vipScheduler;
        }

        modelDataFlowable
                // 异步线程池执行
                .observeOn(scheduler)
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                .map(message -> message.replaceAll("\\s", ""))
                .filter(StrUtil::isNotBlank)
                .flatMap(message -> {
                    // 将字符串转换为 List<Character>
                    List<Character> charList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        charList.add(c);
                    }
                    return Flowable.fromIterable(charList);
                })
                .doOnNext(c -> {
                    {
                        // 识别第一个 [ 表示开始 AI 传输 json 数据，打开 flag 开始拼接 json 数组
                        if (c == '{') {
                            counter.addAndGet(1);
                        }
                        if (counter.get() > 0) {
                            stringBuilder.append(c);
                        }
                        if (c == '}') {
                            counter.addAndGet(-1);
                            if (counter.get() == 0) {
                                // 累积单套题目满足 json 格式后，sse 推送至前端
                                // sse 需要压缩成当行 json，sse 无法识别换行
                                sseEmitter.send(JSONUtil.toJsonStr(stringBuilder.toString()));
                                // 清空 StringBuilder
                                stringBuilder.setLength(0);
                            }
                        }
                    }
                })
                .doOnError( (e) -> log.error("ai生成题目出错",e))//                .doOnComplete(() -> sseEmitter.complete());
                .doOnComplete( sseEmitter::complete)
                .subscribe();


                return sseEmitter;
    }

    /**
     * 流式多线程vip接口
     * @param aiGenerateQuestionRequest
     * @return
     */
    @GetMapping ("/ai_generate/sse/test")
    public SseEmitter aiGenerateQuestionSSETest( AiGenerateQuestionRequest aiGenerateQuestionRequest, boolean isVIP) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        //获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        //获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        //封装Prompt
        String userMessage =getGenerateQuestionUserMessage(app,questionNumber,optionNumber);
        //建立sse 连接对象 ，0表示永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        //Ai生成(稳定型同步Ai),sse流式返回
        Flowable<ModelData> modelDataFlowable=  aiManager.doStreamMessageRequest(GENERATE_QUESTION_SYSTEM_MESSAGE,userMessage,null);

        // 左括号计数器，除了默认值外，当回归为 0 时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger(0);
// 拼接完整题目
        StringBuilder stringBuilder = new StringBuilder();
        //默认非vip线程池
        Scheduler scheduler = Schedulers.io();
        if(isVIP){
            scheduler = vipScheduler;
        }
//        User loginUser = userService.getLoginUser(request);
        //如果是vip或者管理员就用VIP线程池

//        if("vip".equals(loginUser.getUserRole()) ||"admin".equals(loginUser.getUserRole())){
//            scheduler = vipScheduler;
//        }
        modelDataFlowable
                .observeOn(scheduler)
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                .map(message -> message.replaceAll("\\s", ""))
                .filter(StrUtil::isNotBlank)
                .flatMap(message -> {
                    // 将字符串转换为 List<Character>
                    List<Character> charList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        charList.add(c);
                    }
                    return Flowable.fromIterable(charList);
                })
                .doOnNext(c -> {
                    {
                        // 识别第一个 [ 表示开始 AI 传输 json 数据，打开 flag 开始拼接 json 数组
                        if (c == '{') {
                            counter.addAndGet(1);
                        }
                        if (counter.get() > 0) {
                            stringBuilder.append(c);
                        }
                        if (c == '}') {
                            counter.addAndGet(-1);
                            if (counter.get() == 0) {
                                System.out.println(Thread.currentThread().getName());
//                                if("user".equals(loginUser.getUserRole())){
//                                    Thread.sleep(10000L);
//                                }
                                if(!isVIP){
                                    Thread.sleep(10000L);
                                }
                                // 累积单套题目满足 json 格式后，sse 推送至前端
                                // sse 需要压缩成当行 json，sse 无法识别换行
                                sseEmitter.send(JSONUtil.toJsonStr(stringBuilder.toString()));
                                // 清空 StringBuilder
                                stringBuilder.setLength(0);
                            }
                        }
                    }
                })
                .doOnError( (e) -> log.error("ai生成题目出错",e))//                .doOnComplete(() -> sseEmitter.complete());
                .doOnComplete( sseEmitter::complete)
                .subscribe();


        return sseEmitter;
    }



    // endregion
}
