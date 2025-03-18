<template>
  <div id="header-layout">
    <div class="menu-demo">
      <a-menu
        mode="horizontal"
        :default-selected-keys="['1']"
        @menu-item-click="toclick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="logo">
            <img src="../img/哇·来答.svg" alt="哇来答" />
            <p class="img-text">哇来答</p>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>

        <div v-if="LoginUserStore.loginUser.id" class="button">
          <a-button type="primary" shape="round" href="/user/login">
            {{ LoginUserStore.loginUser.userName ?? "无名" }}</a-button
          >
        </div>
        <div v-else class="button">
          <a-button type="primary" shape="round" href="/user/login"
            >登录</a-button
          >
        </div>
      </a-menu>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { useLoginUserStore } from "../store/userStore";
import { routes } from "../router/routes";
import checkAccess from "../access/CheckEnum";
import { computed } from "vue";

const LoginUserStore = useLoginUserStore();
LoginUserStore.fetchLoginUser();
const router = useRouter();
const toclick = (key: string) => {
  router.push({
    path: key,
  });
};
// 展示在菜单的路由数组
const visibleRoutes = computed(() =>
  routes.filter((item) => {
    // 根据路由meta中的hideInMenu属性判断是否隐藏，true隐藏，false不隐藏
    if (item.meta?.hideInMenu) {
      return false;
    }
    // 根据权限过滤菜单
    if (!checkAccess(LoginUserStore.loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  })
);
</script>
<style scoped>
#header-layout {
  box-shadow: #eee 1px 1px 5px;
}
.box {
  margin-right: 38px;
}
.logo {
  height: 50px;
  width: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.logo img {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}
.logo .img-text {
  font-size: 14px;
  font-weight: 550;
  color: black;
}
.button {
  position: absolute;
  top: 25px;
  right: 50px;
  width: 80px;
  color: #ffff;
  font-weight: 540;
  font-size: 14px;
  padding: 0;
}
</style>
