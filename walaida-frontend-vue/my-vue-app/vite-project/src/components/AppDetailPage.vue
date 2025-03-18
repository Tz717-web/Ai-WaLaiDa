<template>
  <div id="app-detail-page">
    <a-card>
      <a-row class="grid-demo" style="margin-bottom: 16px">
        <a-col flex="auto">
          <h1>{{ data.appName }}</h1>
          <p>{{ data.appDesc }}</p>
          <p>应用类型: {{ APP_TYPE_MAP[data.appType] }}</p>
          <p>答题策略: {{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>

          <a-avatar
            :size="24"
            :style="{ marginRight: '8px' }"
            :image-url="data.userVO?.userAvatar"
          />

          <a-typography-text>{{
            data.userVO?.userName ?? "匿名"
          }}</a-typography-text>

          <p>
            创建时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </p>
          <div class="buttom">
            <a-button
              type="primary"
              @click="router.push(`/answer/do/${props.id}`)"
            >
              开始答题
            </a-button>
            <a-button @click="doShare"> 分享应用 </a-button>
            <div class="Mybuttom" v-if="isMy">
              <a-button class="mybutton" :href="`/add/question/${id}`">
                设置题目</a-button
              >
              <a-button
                class="mybutton"
                @click="router.push(`/add/soring_result/${props.id}`)"
              >
                设置评分
              </a-button>
              <a-button class="mybutton" :href="`/add/app/${id}`">
                修改应用
              </a-button>
            </div>
          </div>
        </a-col>

        <a-col flex="320px">
          <img :src="data.appIcon" class="image-area" />
        </a-col>
      </a-row>
    </a-card>
  </div>
  <ShareModal ref="shareModalRef" :link="shareLink" title="分享应用" />
</template>

<script setup lang="ts">
import message from "@arco-design/web-vue/es/message";
import { computed, ref, watchEffect } from "vue";
import { useRouter } from "vue-router";
import { getAppVoByIdUsingGet } from "../api/appController";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { useLoginUserStore } from "../store/userStore";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "../constant/app";
import ShareModal from "./ShareModal.vue";

/**
 * 加载数据
 */
interface Props {
  id: string;
}
const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "0";
  },
});
const router = useRouter();
const data = ref<API.AppVO>({});
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.id as any,
  });

  if (res.data.code === 0) {
    data.value = res.data.data as API.AppVO;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};

watchEffect(() => {
  loadData();
});
const loginUserStore = useLoginUserStore();
let loginUserId = loginUserStore.loginUser.id;
const isMy = computed(() => {
  return loginUserId && data.value.userVO?.id === loginUserId;
});

// 分享弹窗引用
const shareModalRef = ref();
// 分享链接
const shareLink = `${window.location.protocol}//${window.location.host}/app/detail/${props.id}`;
// 分享
const doShare = (e: Event) => {
  if (shareModalRef.value) {
    shareModalRef.value.openModal();
  }
  e.stopPropagation(); // 阻止冒泡
};
</script>

<style scoped>
#app-detail-page {
  padding: 16px;
}
.image-area {
  width: 320px;
  height: 280px;
}
.buttom {
  margin: 20px;
}
.buttom button {
  margin-right: 10px;
}
.Mybuttom .mybutton {
  margin-right: 10px;
}
.Mybuttom {
  display: inline-block;
}
</style>
