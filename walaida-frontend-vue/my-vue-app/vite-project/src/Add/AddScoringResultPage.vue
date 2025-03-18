<template>
  <div id="addScoringResultPage">
    <h2 style="margin-bottom: 32px">创建评分结果</h2>
    <a-form
      :model="form"
      :style="{ width: '480px' }"
      label-align="left"
      auto-label-width
      @submit="handleSubmit"
    >
      <a-form-item field="appName" label="应用id" disabled>
        {{ appId }}
      </a-form-item>
      <a-form-item field="resultName" label="结果名称">
        <a-input v-model="form.resultName" placeholder="请输入结果名称" />
      </a-form-item>
      <a-form-item field="resultDesc" label="结果描述">
        <a-input v-model="form.resultDesc" placeholder="请输入结果描述" />
      </a-form-item>
      <a-form-item field="resultPicture" label="结果图标">
        <a-input
          v-model="form.resultPicture"
          placeholder="请输入结果图标地址"
        />
      </a-form-item>
      <a-form-item field="resultProp" label="结果集">
        <a-input-tag
          v-model="form.resultProp"
          :style="{ width: '320px' }"
          placeholder="输入结果集"
          allow-clear
        />
      </a-form-item>
      <a-form-item field="resultScoreRange" label="结果得分范围">
        <a-input-number
          v-model="form.resultScoreRange"
          placeholder="请输入结果得分范围"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">
          提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
  <h2 style="margin-bottom: 32px">管理评分结果表</h2>
  <ScoringResultTable :appId="appId" :doUpdate="doUpdate" ref="tableRef" />
</template>
<script setup lang="ts">
import { reactive, ref } from "vue";
import message from "@arco-design/web-vue/es/message";
import {
  addScoringResultUsingPost,
  updateScoringResultUsingPost,
} from "../api/scoringResultController";
import router from "../router";
import ScoringResultTable from "./ScoringResultTable.vue";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => "",
});

const form = reactive({
  resultDesc: "",
  resultName: "",
  resultPicture: "",
} as API.ScoringResultAddRequest);
// 声明 ref
const tableRef = ref();
// 调用
if (tableRef.value) {
  tableRef.value.loadData();
}
// 用于判断是否更新
let updateId = ref();

/**
 * 修改
 * @param scoringResult
 */
const doUpdate = (scoringResult: API.ScoringResultVO) => {
  updateId.value = scoringResult.id;
  form.resultDesc = scoringResult.resultDesc;
  form.resultName = scoringResult.resultName;
  form.resultPicture = scoringResult.resultPicture;
  form.resultProp = scoringResult.resultProp;
  form.resultScoreRange = scoringResult.resultScoreRange;
};

/**
 * 提交
 */
const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  let res;
  // 区分创建和修改
  if (updateId.value) {
    res = await updateScoringResultUsingPost({
      id: updateId.value,
      ...form,
    });
  } else {
    res = await addScoringResultUsingPost({
      appId: props.appId as any,
      ...form,
    });
  }
  if (res.data.code === 0) {
    message.success("操作成功");
    setTimeout(() => {
      router.push(`/app/detail/${props.appId}`);
    }, 3000);
  } else {
    message.error("操作失败，" + res.data.message);
  }
  if (tableRef.value) {
    updateId.value = undefined;
  }
};
</script>
<style scoped>
#addScoringResultPage {
  padding: 32px;
}
</style>
