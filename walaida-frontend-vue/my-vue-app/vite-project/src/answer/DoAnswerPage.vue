<template>
  <div id="doAnswerPage">
    <div>
      <h1>{{ data.appName }}</h1>
      <h2>{{ app.appName }}</h2>
      <div v-if="questionContent">
        <h2>{{ currentQuestion.title }}</h2>

        <a-radio-group
          v-model="currentAnswer"
          direction="vertical"
          :options="questionOptions"
          @change="onRadioChange"
          size="large"
        />
      </div>
    </div>
    <div style="margin-top: 24px">
      <a-space size="large">
        <a-button
          type="primary"
          circle
          v-if="current < questionContent.length"
          :disabled="!currentAnswer"
          @click="doNext"
        >
          下一题
        </a-button>
        <a-button
          type="primary"
          v-if="current === questionContent.length"
          :loading="submitting"
          circle
          :disabled="!currentAnswer"
          @click="doSubmit"
        >
          {{ submitting ? "小哇评分中！" : "查看结果" }}
        </a-button>
        <a-button v-if="current > 1" circle @click="current -= 1">
          上一题
        </a-button>
      </a-space>
    </div>
  </div>
</template>
<script setup lang="ts">
import app from "antd/es/app";
import { ref, computed, reactive, watch, watchEffect } from "vue";
import { getAppVoByIdUsingGet } from "../api/appController";
import { listQuestionVoByPageUsingPost } from "../api/questionController";
import { Message } from "@arco-design/web-vue";
import { Value } from "sass";
import router from "../router";
import { addUserAnswerUsingPost } from "../api/userAnswerController";
import message from "@arco-design/web-vue/es/message";
import { generateUserAnswerIdUsingGet } from "../api/userController";
// 当前题目序号（从 1 开始）
const current = ref(1);
// 当前题目
const currentQuestion = ref<API.QustionContentDTO>({});
const questionOptions = computed(() => {
  return currentQuestion.value.options
    ? currentQuestion.value.options.map((option) => ({
        label: `${option.key}. ${option.value}`,
        value: option.key || "", // 提供一个默认值，避免 value 为 undefined
      }))
    : [];
});

const questionContent = ref<API.QustionContentDTO[]>([]);
const submitting = ref(false);
const doNext = (value: any) => {
  current.value += 1;
  currentAnswer.value = value;
  answerList[current.value - 1] = value;
};

const onRadioChange = (value: any) => {
  currentAnswer.value = value;
  // 记录回答
  answerList[current.value - 1] = value;
};

// 当前答案
const currentAnswer = ref<string>("");

// 回答列表
const answerList = reactive<string[]>([]);
// 声明 props
interface Props {
  appId?: string;
}
const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});
//唯一id
const id = ref<number>();
//生成唯一id
const generateId = async () => {
  const res = await generateUserAnswerIdUsingGet();
  if (res.data.code === 0) {
    id.value = res.data.data;
  } else {
    Message.error("获取唯一id失败," + res.data.message);
  }
};
//进入页面时生成唯一id
watchEffect(() => {
  generateId();
});
//获取题目信息
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  let res: any = await getAppVoByIdUsingGet({
    id: props.appId as any,
  });
  if (res.data.code === 0 && res.data.data) {
    app.value = res.data.data;
  } else {
    Message.error("获取应用失败，" + res.data.message);
  }
  res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0 && res.data.data?.records) {
    questionContent.value = res.data.data.records[0].questionContent;
  } else {
    Message.error("获取题目失败，" + res.data.message);
  }
};
watchEffect(() => {
  loadData();
});
watchEffect(() => {
  currentQuestion.value = questionContent.value[current.value - 1];
});
const doSubmit = async () => {
  submitting.value = true;
  const res = await addUserAnswerUsingPost({
    id: id.value as any,
    appId: props.appId as any,
    choices: answerList,
  });

  if (res.data.code === 0 && res.data.data) {
    router.push(`/answer/result/${res.data.data}`);
  } else {
    message.error("提交答案失败，" + res.data.message);
  }
  submitting.value = false;
};
//获取应用名字

const data = ref<API.AppVO>({});
const LoadData = async () => {
  if (!props.appId) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.appId as any,
  });

  if (res.data.code === 0) {
    data.value = res.data.data as API.AppVO;
  } else {
    message.error("获取应用名失败，" + res.data.message);
  }
};

watchEffect(() => {
  LoadData();
});
</script>
<style scoped>
#doAnswerPage {
  background-color: #ffffff;
  width: 500px;
  margin: 20px;
  padding: 20px;
  box-shadow: 3px 3px 3px 2px #aaa9a9;
  border-radius: 0.5%;
}
</style>
