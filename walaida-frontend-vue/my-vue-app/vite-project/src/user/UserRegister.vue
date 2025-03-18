<template>
  <div id="userlogin">
    <a-space direction="vertical" size="large" :style="{ width: '500px' }">
      <h1>用户登录</h1>
      <a-form :model="form" :layout="layout" @submit="onSubmit">
        <a-form-item
          field="name"
          label="账号"
          style="font-weight: 550; padding-left: 10px"
        >
          <a-input
            class="input"
            v-model="form.userAccount"
            placeholder="请输入您的账号"
          />
        </a-form-item>
        <a-form-item
          field="Password"
          label="密码"
          tooltip="密码长度不能小于8位"
          style="font-weight: 550; padding-left: 10px"
        >
          <a-input-password
            class="input"
            v-model="form.userPassword"
            allow-clear
            placeholder="请输入您的密码"
          />
        </a-form-item>
        <a-form-item
          field="checkPassword"
          label="密码"
          tooltip="确认密码长度不能小于8位"
          style="font-weight: 550; padding-left: 10px"
        >
          <a-input-password
            class="input"
            v-model="form.checkPassword"
            allow-clear
            placeholder="请再次输入您的密码"
          />
        </a-form-item>
        <a-form-item>
          <a-button class="buttom" html-type="submit">注册</a-button>
          <a
            href="/user/login"
            style="margin-left: 150px; text-decoration: none; color: #499ade"
            >直接登录</a
          >
        </a-form-item>
      </a-form>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { userRegisterUsingPost } from "../api/userController";
import { useLoginUserStore } from "../store/userStore";
import message from "@arco-design/web-vue/es/message"; //message提示框
import { useRouter } from "vue-router";
const router = useRouter();
const UseLoginUserStore = useLoginUserStore();
const layout = "vertical";
const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
} as API.UserRegisterRequest);
const onSubmit = async () => {
  const res = await userRegisterUsingPost(form);
  if (form.userPassword !== form.checkPassword) {
    message.error("两次密码不一致");
    return;
  } else {
    if (res.data.code === 0) {
      await UseLoginUserStore.fetchLoginUser();
      message.success("注册成功");
      router.push({
        path: "/user/userLogin",
        replace: true,
      });
    } else {
      message.error("注册失败" + res.data.message);
    }
  }
};
</script>

<style scoped>
#userlogin {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 500px;
  width: 600px;
  background-color: rgba(255, 255, 255, 0.2); /* 设置透明度 */
  border-radius: 10px; /* 设置圆角边框 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  margin: auto; /* 水平和垂直居中 */
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
#userlogin h1 {
  text-align: center;
  margin-top: 0px;
  color: #fff;
  font-size: 24px;
  margin-bottom: 20px;
}
#userlogin .buttom {
  width: 16%;
  background-color: #499ade;
  color: #fff;
  border: none;
  border-radius: 5px;
  margin-left: 42%;
  padding: 0;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
#userlogin .input {
  width: 100%;
  height: 40px;
  border: 0.5px solid #9a9aa1;
  border-radius: 8px;
  padding: 0 10px;
}
#userlogin .admin {
  margin-left: 35%;
  font-size: 14px;
}
</style>
