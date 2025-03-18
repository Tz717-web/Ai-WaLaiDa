import HelloWorld from "../components/HelloWorld.vue";
import UserLayout from "../layout/UserLayout.vue";
import NoAuthPage from "../view/NoAuthPage.vue";
import ACCESS_ENUM from "../access/accessEnum";
import UserLogin from "../user/UserLogin.vue";
import UserRegister from "../user/UserRegister.vue";
import type { RouteRecordRaw } from "vue-router";
import AdminUserPage from "../damin/adminUserPage.vue";
import AdminAppPage from "../damin/adminAppPage.vue";
import AdminQuestionPage from "../damin/AdminQuestionPage.vue";
import AdminUserAwserPage from "../damin/AdminUserAwserPage.vue";
import AdminScoringRsultPage from "../damin/AdminScoringRsultPage.vue";
import AppDetailPage from "../components/AppDetailPage.vue";
import AddAppPage from "../Add/AddAppPage.vue";
import AddQuestionPage from "../Add/AddQuestionPage.vue";
import UpdataAppPage from "../Add/UpdataAppPage.vue";
import AddScoringResultPage from "../Add/AddScoringResultPage.vue";
import DoAnswerDoPage from "../answer/DoAnswerPage.vue";
import AnswerResultPage from "../answer/AnswerResultPage.vue";
import UserAnswerTable from "../answer/UserAnswerTable.vue";
import AppStatisicPage from "../Stasistic/AppStatisicPage.vue";
//获取当前路由地址

export const routes: Array<RouteRecordRaw> = [
  {
    //路由初始指向
    path: "/",
    name: "首页",
    component: HelloWorld,
  },
  {
    path: "/app/detail/:id",
    name: "应用详情",
    component: AppDetailPage,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/answer/do/:appId",
    name: "答题",
    component: DoAnswerDoPage,
    props: true,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/result/:id",
    name: "答题结果",
    component: AnswerResultPage,
    props: true,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/my",
    name: "我的答题",
    component: UserAnswerTable,
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },

  {
    path: "/add/app",
    name: "创建应用",
    component: AddAppPage,
  },
  {
    path: "/add/app/:id",
    name: "修改应用",
    component: UpdataAppPage,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/add/question/:appId",
    name: "创建题目",
    component: AddQuestionPage,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/add/soring_result/:appId",
    name: "创建评分",
    component: AddScoringResultPage,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },

  {
    path: "/hide",
    name: "隐藏页面",
    component: UserRegister,
    //隐藏页面专属
    meta: {
      hideInMenu: true,
    },
  },

  {
    path: "/adminUserpage",
    name: "用户管理",
    component: AdminUserPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/adminApppage",
    name: "应用管理",
    component: AdminAppPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/adminScoringRsultpage",
    name: "评分管理",
    component: AdminScoringRsultPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/adminQuestionpage",
    name: "题目管理",
    component: AdminQuestionPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/adminAnswerpage",
    name: "答案管理",
    component: AdminUserAwserPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/appStatisic",
    name: "应用统计",
    component: AppStatisicPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },

  {
    path: "/NoAuth",
    name: "无权限",
    component: NoAuthPage,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/user",
    name: "用户中心",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: UserLogin,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: UserRegister,
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
  // {
  //   path: "/:pathMatch(.*)*",
  //   meta: {
  //     title: "找不到此页面",
  //   },
  //   component: (to) => {``
  //     // 获取路径的最后一个字符串
  //     const segments = to.path;
  //     return import(`${segments}`);
  //   },
  // },
];
