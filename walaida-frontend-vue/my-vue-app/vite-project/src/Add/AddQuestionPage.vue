<template>
  <div id="userlogin">
    <a-space direction="vertical" size="large">
      <h1>创建应用id {{ appId }}</h1>
      <a-form :model="questionContent" :layout="layout" @submit="handleSubmit">
        <a-form-item
          label="题目列表"
          :content-flex="false"
          :merge-props="false"
        >
          <a-space size="medium">
            <a-button @click="addQuestion(questionContent.length)">
              底部添加题目
            </a-button>
            <!-- AI 生成题目抽屉 -->
            <AddAiQuestion
              :appId="appId"
              :onSuccess="onAiGenerateSuccess"
              :onSSESuccess="onAiGenerateSuccessSSE"
              :onSSEStart="onSSEStart"
              :onSSEClose="onSSEClose"
            />
          </a-space>

          <!-- 题目列表 -->
          <div v-for="(question, index) in questionContent" :key="index">
            <!-- 添加题目 -->
            <a-space>
              <h3>题目{{ index + 1 }}</h3>

              <a-button
                class="buttom"
                @click="addQuestion(index + 1)"
                size="medium"
                >添加题目</a-button
              >
              <a-button
                size="medium"
                status="danger"
                class="button"
                @click="deleteQuestion(index)"
                >删除题目</a-button
              ></a-space
            >
            <!-- 输入标题 -->
            <a-form-item
              field="posts.post1"
              :label="`题目${index + 1}`"
              class="form-item"
            >
              <a-input
                class="form-input"
                v-model="question.title"
                placeholder="请输入标题"
                maxlength="100"
              />
            </a-form-item>

            <!-- 添加题目选项 -->
            <a-space class="option">
              <h4>题目{{ index + 1 }}选项列表</h4>
              <a-button
                size="mini"
                class="buttom"
                @click="addQuestionOption(question, question.options.length)"
                >底部添加选项</a-button
              >
            </a-space>
            <div
              v-for="(option, optionIndex) in question.options"
              :key="optionIndex"
              class="option"
            >
              <a-form-item
                :field="`posts.post${optionIndex}`"
                :label="`选项${optionIndex + 1}`"
              >
                <a-form-item label="选项 key" class="input">
                  <a-input
                    v-model="option.key"
                    placeholder="请输入选项 key"
                    maxlength="50"
                  />
                </a-form-item>

                <a-form-item label="选项值" class="input">
                  <a-input
                    v-model="option.value"
                    placeholder="请输入选项值"
                    maxlength="100"
                  />
                </a-form-item>
              </a-form-item>
              <a-form-item label="测评值result" class="input">
                <a-input
                  v-model="option.result"
                  placeholder="请输入特征值（测评类填写）"
                  maxlength="50"
                />
              </a-form-item>
              <a-form-item label="分数 score" class="input">
                <a-input
                  v-model="option.score"
                  placeholder="请输入分数 (得分类填写)"
                  maxlength="50"
                />
              </a-form-item>

              <a-space>
                <a-button
                  size="mini"
                  class="buttom"
                  @click="addQuestionOption(question, optionIndex + 1)"
                  >添加选项</a-button
                >
                <a-button
                  size="mini"
                  status="danger"
                  class="button"
                  @click="deleteQuestionOption(question, optionIndex)"
                  >删除选项</a-button
                >
              </a-space>
            </div>
          </div>
        </a-form-item>

        <a-form-item>
          <a-button class="buttom" html-type="submit">提交</a-button>
        </a-form-item>
      </a-form>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import message from "@arco-design/web-vue/es/message"; //message提示框
import { useRouter } from "vue-router";
import {
  addQuestionUsingPost,
  editQuestionUsingPost,
  listQuestionVoByPageUsingPost,
} from "../api/questionController";
import AddAiQuestion from "./AddAiQuestion.vue";
const router = useRouter();
const layout = "vertical";
// 使用 Vue 3 的 ref 函数创建一个响应式对象 form，初始值为一个包含 posts 属性的对象
// posts 属性是一个对象，包含两个空字符串 post1 和 post2
// as API.AppAddRequest 表示将 form 对象的类型断言为 API.AppAddRequest 类型
// 定义一个接口 Props，包含一个字符串类型的属性 AppId
interface Props {
  appId?: string;
}
const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});
//题目内容结构，题目列表
const questionContent = ref<API.QustionContentDTO[]>([]);

/**
 * 添加题目
 * @param index
 */
const addQuestion = (index: number) => {
  questionContent.value.splice(index, 0, {
    title: "",
    options: [],
  });
};
/**
 * 删除题目
 * @param index
 */
const deleteQuestion = (index: number) => {
  questionContent.value.splice(index, 1);
};
/**
 * 添加题目选项
 * @param question
 * @param index
 */
const addQuestionOption = (question: API.QustionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 0, {
    key: "",
    value: "",
  });
};

/**
 * 删除题目选项
 * @param question
 * @param index
 */
const deleteQuestionOption = (
  question: API.QustionContentDTO,
  index: number
) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 1);
};

const oldQuestion = ref<API.QuestionVO>();

// 加载数据
const loadData = async () => {
  if (!props.appId) {
    return;
  }

  // 获取最新的题目记录
  const res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0 && res.data.data?.records) {
    oldQuestion.value = res.data.data.records[0];
    if (oldQuestion.value) {
      questionContent.value = oldQuestion.value.questionContent ?? [];
    }
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
 */ const handleSubmit = async () => {
  if (!props.appId || !questionContent.value) {
    return;
  }

  let res;
  if (oldQuestion.value?.id) {
    res = await editQuestionUsingPost({
      id: oldQuestion.value?.id,
      questionContent: questionContent.value,
    });
  } else {
    res = await addQuestionUsingPost({
      appId: props.appId as any,
      questionContent: questionContent.value,
    });
  }
  if (res.data.code === 0) {
    message.success("创建成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${props.appId}`);
    }, 3000);
  } else {
    message.error("创建失败，" + res.data.message);
  }
};
//Ai生成题目
const onAiGenerateSuccess = (result: API.QustionContentDTO[]) => {
  questionContent.value = [...questionContent.value, ...result];
  message.success(`AI 生成题目成功，已新增 ${result.length} 道题目`);
};
//SSE实时生成题目后执行
const onAiGenerateSuccessSSE = (result: API.QustionContentDTO[]) => {
  questionContent.value = [...questionContent.value, result];
};
/**
 * SSE开始生成题目
 * @param event
 */
const onSSEStart = (event: any) => {
  message.success("尊贵的vip用户,小哇开始生成题目中");
};
/**
 * SSE生成题目完毕
 * @param event
 */
const onSSEClose = (event: any) => {
  message.success("尊贵的vip用户,题目已生成完毕!");
};
</script>

<style scoped>
#userlogin {
  width: 100%;
  height: 100%;
  background-color: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
#userlogin h1 {
  margin-top: 0px;
  color: #2f17e1;
  font-size: 24px;
}
#userlogin .buttom {
  background-color: #1182df;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  transition: background-color 0.3s ease;
}
#userlogin .button {
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  transition: background-color 0.3s ease;
}
#userlogin .form-item {
  max-width: 2000px;
  margin: 5px;
}
#userlogin .form-input {
  max-width: 300px;
}
#userlogin .admin {
  font-size: 14px;
}
.option {
  margin-left: 100px;
}
.input {
  margin-left: 20px;
  max-width: 200px;
}
</style>
