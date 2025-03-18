<template>
  <div id="userlogin">
    <a-space direction="vertical" size="large" :style="{ width: '500px' }">
      <h1>创建应用</h1>
      <a-form :model="form" :layout="layout" @submit="onSubmit">
        <a-form-item
          field="appName"
          label="应用名称"
          style="font-weight: 550; padding-left: 10px"
        >
          <a-input
            class="input"
            v-model="form.appName"
            placeholder="请输入你的应用名称"
          />
        </a-form-item>
        <a-form-item
          field="appDesc"
          label="应用描述"
          style="font-weight: 550; padding-left: 10px"
        >
          <a-input
            class="input"
            v-model="form.appDesc"
            placeholder="请输入你的应用描述"
          />
        </a-form-item>
        <a-form-item
          field="appIcon"
          label="应用图标"
          style="font-weight: 550; padding-left: 10px"
        >
          <a-input
            class="input"
            v-model="form.appIcon"
            placeholder="请输入你的应用图标"
          />
        </a-form-item>
        <a-form-item
          field="appType"
          label="应用类型"
          style="font-weight: 550; padding-left: 10px"
        >
          <a-select :style="{ width: '320px' }" v-model="form.scoringStrategy">
            <a-option
              v-for="(value, key) in APP_TYPE_MAP"
              :label="value"
              :value="Number(key)"
            ></a-option>
          </a-select>
        </a-form-item>
        <a-form-item
          field="appTape"
          label="答题策略"
          style="font-weight: 550; padding-left: 10px"
        >
          <a-select :style="{ width: '320px' }" v-model="form.appType">
            <a-option
              v-for="(value, key) in APP_SCORING_STRATEGY_MAP"
              :label="value"
              :value="Number(key)"
            ></a-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button class="buttom1" html-type="submit">修改应用</a-button>
          <a-button class="buttom2" :href="`/app/detail/${props.id}`"
            >我在想想</a-button
          >
        </a-form-item>
      </a-form>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import message from "@arco-design/web-vue/es/message"; //message提示框
import { useRouter } from "vue-router";
import { addAppUsingPost, getAppVoByIdUsingGet } from "../api/appController";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "../constant/app";
import { editAppUsingPost } from "../api/appController";
const router = useRouter();
const layout = "vertical";
const form = ref({
  appName: "",
  appDesc: "",
  appIcon: "",
  appType: 0,
  scoringStrategy: 0,
} as API.AppAddRequest);
interface Props {
  id: string;
}
const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "0";
  },
});
const oldApp = ref<API.AppVO>();

// 加载数据
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.id as any,
  });
  if (res.data.code === 0 && res.data.data) {
    oldApp.value = res.data.data;
    form.value = res.data.data;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};

// 获取旧数据
watchEffect(() => {
  loadData();
});
/**
 * 提交
 */
const onSubmit = async () => {
  // 是修改
  let res: any;
  if (props.id) {
    res = await editAppUsingPost({
      id: props.id as any,
      ...form.value,
    });
  } else {
    res = await addAppUsingPost(form.value);
  }
  if (res.data.code === 0) {
    message.success("修改成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${props.id ?? res.data.data}`);
    }, 3000);
  } else {
    message.error("修改失败，" + res.data.message);
  }
};
</script>

<style scoped>
#userlogin {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 580px;
  width: 600px;
  background-color: rgba(255, 255, 255, 0.2); /* 设置透明度 */
  border-radius: 10px; /* 设置圆角边框 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  margin: auto; /* 水平和垂直居中 */
  position: absolute;
  top: 50px;
  left: 0;
  right: 0;
  bottom: 0;
}
#userlogin h1 {
  text-align: center;
  margin-top: 0px;
  color: #2f17e1;
  font-size: 24px;
}
#userlogin .buttom1 {
  padding: 10px;
  width: 16%;
  background-color: #1182df;
  color: #fff;
  border: none;
  border-radius: 5px;
  margin-left: 42%;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
#userlogin .buttom2 {
  padding: 10px;
  width: 16%;
  background-color: #a2a8ad;
  color: #fff;
  border: none;
  border-radius: 5px;
  margin-left: 30%;
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
