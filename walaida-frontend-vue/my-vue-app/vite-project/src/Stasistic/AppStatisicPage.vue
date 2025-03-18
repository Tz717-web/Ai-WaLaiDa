<template>
  <div id="appStatisic">
    <div id="appStatisticPage">
      <h2>热门应用统计</h2>
      <v-chart :option="appAnswerCountOptions" style="height: 300px" />
      <div class="searchBar">
        <a-input-search
          :style="{ width: '320px' }"
          placeholder="输入 appId"
          button-text="搜索"
          search-button
          @search="(value) => loadAppAnswerResultCountData(value)"
        />
      </div>
      <div style="margin-bottom: 20px" />

      <h2>应用结果统计</h2>
      <v-chart
        :option="appAnswerResultCountOptions"
        style="height: 300px; width: 1000px"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import VChart from "vue-echarts";
import "echarts";
import message from "@arco-design/web-vue/es/message";
import {
  getAppAnswerCountUsingGet,
  getAppAnswerResultCountUsingGet,
} from "../api/statisicController";
import { log } from "console";
const appAnswerCountList = ref<API.AppAnswerCountDTO[]>([]);
const appAnswerResultCountList = ref<API.AppAnswerResultCountDTO[]>([]);
/**
 * 加载数据
 */
const loadAppAnswerCountData = async () => {
  const res = await getAppAnswerCountUsingGet();
  if (res.data.code === 0) {
    appAnswerCountList.value = res.data.data || [];
    console.log(appAnswerCountList.value);
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};

/**
 * 加载数据
 */
const loadAppAnswerResultCountData = async (appId: string) => {
  if (!appId) {
    return;
  }

  const res = await getAppAnswerResultCountUsingGet({
    appId: appId as any,
  });
  if (res.data.code === 0) {
    appAnswerResultCountList.value = res.data.data || [];
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
//柱状图样式
const appAnswerCountOptions = computed(() => {
  return {
    xAxis: {
      type: "category",
      data: appAnswerCountList.value.map((item) => {
        return {
          value: item.appId,
        };
      }),
      name: "应用 id",
    },
    yAxis: {
      type: "value",
      name: "做题用户数",
    },
    series: [
      {
        data: appAnswerCountList.value.map((item) => item.answerCount),
        type: "bar",
      },
    ],
  };
});
//柱状图样式
const appAnswerResultCountOptions = computed(() => {
  return {
    tooltip: {
      trigger: "item",
    },
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        type: "pie",
        radius: "50%",
        data: appAnswerResultCountList.value.map((item) => {
          return {
            value: item.resultCount,
            name: item.resultName,
          };
        }),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };
});

//数据变化触法重新加载
watchEffect(() => {
  loadAppAnswerCountData();
});
watchEffect(() => {
  loadAppAnswerResultCountData("1");
});
</script>
<style scoped>
#appStatisic {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
