  AI 答题应用 自述
  
基于Springboot +Redis +ChatGLM +Rxjava +SSE(VUE3 +Arco +piain)的Ai智能答题应用平台

用户可以基于自己的想象力用ai辅助生成题目和评分，制作属于自己的有趣应用hhh，并且携带统计分析功能根据热门应用和答题数进行排名。

*后端(Spring Boot) :

选用智谱Ai加持，SSE流式和缓存机制优化，加上线程池隔离技术，至少能支持1000+人在线进行答题，并且性能不会受到太大影响，

基于Rxjava的操作符链式调用处理AI异步数据流，并会通过licode括号算法将整道题目进行流式输出，保证了因数据流太大而输出时间过长导致的请求失误问题。

提交题目用雪花算法生成唯一id,防止用户反复提交和恶意提交，拦截脏数据。

选用MyBatis进行数据访问层开发，全局装配了异常处理器，采用双检锁单例模式进行管理，使用JUnit Jupiter API 的@Test 注释和Assertions lei实现对用户模块的单元测试，覆盖率高达90%。

*前端(Vue.js React+Taro+ui): 
Vue.js用于实现用户界面，构建过程使用了阿里arco组件库，使用pinia库管理全局状态，发送请求给后端接口并展示响应数据，并配备了全局路由守卫统一全局权限。

 React+Taro+ui 小程序实现了多端跨域，纯前台项目小而精简。
 
*###通信方式:前端通过Axios(或其他HTTP 客户端库)向Django API发送请求，并通过JSON 格式交换数据。

a.Spring Boot 项目

-安装最新idae,用文件中maven构建依赖和jdk(自行下载，推荐18版本，不易出问题)，然后自建数据库连接运行UserCenterApplication.·即可

默认情况下，后端会运行在http://127.0.0.1:8101
b.vue 项目

安装Node.js（>19）

安装npm(pnpm)或yarn(基本知识自行在网上搜索指令)

安装Axios和 Pinia库 pnpm install axios ，pnpm install pinia，当然如果用 npm 也是同理

安装 vue vite 脚手架 npm install -g @vue/vite

通过 npm instal 构建和安装相关依赖


运行中可能存在的问题
前端在编写过程中使用了prettier进行代码规范化，可能不符合您的IDE设置，如果遇到格式之类的错误，可尝试 npm run lint --fix 进行修复 （如文件代码规范支持代码_行尾序列_为LF,大多数情况为CRLF，导致大面积报错，记得修改设置即可）

在根目录配置xml文件的底部 AI 的key需要自己在质谱AI 官网注册申请key,官网：https://open.bigmodel.cn/dev/api/normal-model/glm-4#sdk_install ，有免费200wtoken，放心造。

后端端口号被占用导致的错误 解决办法： https://blog.csdn.net/weixin_46030002/article/details/126649348 项目庞大许多逻辑是作者手搓，可能存在小bug，还请海涵。
