<script setup lang="ts">
import { useRoute } from "vue-router";
import BasicLayout from "./layout/BasicLayout.vue";
import { getLoginUserUsingGet } from "./api/userController";
import { onMounted } from "vue";
import PictureUploader from "./components/PictureUploader.vue";

getLoginUserUsingGet().then((res) => {
  console.log(res);
});
const route = useRoute();
/**
 * 全局初始化函数，有全局单次调用的代码，都可以写到这里
 */
const doInit = () => {
  console.log("hello 欢迎来到我的项目");
};

onMounted(() => {
  doInit();
});
</script>

<template>
  <div id="App">
    <template v-if="route.path.startsWith('/user')">
      <router-view />
    </template>
    <template v-else>
      <BasicLayout />
    </template>
  </div>
</template>
<style scoped></style>
