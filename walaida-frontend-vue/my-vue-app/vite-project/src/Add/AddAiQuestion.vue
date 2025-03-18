<template>
  <a-button type="outline" @click="handleClick">AI 生成题目</a-button>
  <a-drawer
    :width="340"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    unmountOnClose
  >
    <template #title> AI 生成题目</template>
    <div>
      <a-form
        :model="form"
        label-align="left"
        auto-label-width
        @submit="handleSubmit"
      >
        <a-form-item label="应用 id">
          {{ appId }}
        </a-form-item>
        <a-form-item field="questionNumber" label="题目数量">
          <a-input-number
            v-model="form.questionNumber"
            :min="0"
            :max="20"
            placeholder="请输入题目数量"
          />
        </a-form-item>
        <a-form-item field="optionNumber" label="选项数量">
          <a-input-number
            v-model="form.optionNumber"
            :min="0"
            :max="6"
            placeholder="请输入选项数量"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button
              :loading="submitting"
              type="primary"
              html-type="submit"
              style="width: 120px"
            >
              {{ submitting ? "生成中" : "一键生成" }}
            </a-button>
            <a-button
              :loading="SSEsubmitting"
              @click="handSSEleSubmit"
              style="width: 120px"
            >
              {{ submitting ? "生成中" : "实时生成(vip)" }}
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { defineProps, reactive, ref, withDefaults } from "vue";
import { aiGenerateQuestionUsingPost } from "../api/questionController";
import message from "@arco-design/web-vue/es/message";

interface Props {
  appId: string;
  onSuccess?: (result: API.QustionContentDTO[]) => void;
  onSSESuccess?: (result: API.QustionContentDTO[]) => void;
  onSSEStart?: (event: any) => void;
  onSSEClose?: (event: any) => void;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const visible = ref(false);
const submitting = ref(false);
const SSEsubmitting = ref(false);

const form = reactive({
  questionNumber: 5,
  optionNumber: 2,
} as API.AiGenerateQuestionRequest);

const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};

/**
 * 提交
 */
const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  submitting.value = true;
  const res = await aiGenerateQuestionUsingPost({
    appId: props.appId as any,
    ...form,
  });
  if (res.data.code === 0 && res.data.data.length > 0) {
    if (props.onSuccess) {
      props.onSuccess(res.data.data);
    } else {
      message.success("生成成功");
    }
    handleCancel();
  } else {
    message.error("操作失败，" + res.data.message);
  }
  submitting.value = false;
};
//流式返回
const handSSEleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  SSEsubmitting.value = true;
  //todo 手动填写完整后端的地址！
  const eventSource = new EventSource(
    "http://localhost:8101/api/question/ai_generate/sse" +
      `?appId=${props.appId}&questionNumber=${form.questionNumber}&optionNumber=${form.optionNumber}`,
    {
      //确保携带cookie
      withCredentials: true,
    }
  );
  eventSource.onmessage = function (event) {
    console.log(event.data);
    props.onSSESuccess?.(JSON.parse(event.data));
  };
  handleCancel();
  eventSource.onerror = function (event) {
    if (eventSource.readyState === EventSource.CLOSED) {
      console.log(" 正常关闭连接");
      eventSource.close(); //关闭连接
      props.onSSEClose?.(event);
    } else {
      eventSource.close(); //关闭连接
      props.onSSEClose?.(event);
    }

    SSEsubmitting.value = false;
  };
};
</script>
