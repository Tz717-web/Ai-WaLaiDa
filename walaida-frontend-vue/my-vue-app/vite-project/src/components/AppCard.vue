<template>
  <a-card class="app-card" @click="docardClick">
    <template #actions>
      <span class="icon-hover"> <IconThumbUp /> </span>
      <span class="icon-hover" @click="doShare"> <IconShareInternal /> </span>
      <span class="icon-hover"> <IconMore /> </span>
    </template>
    <template #cover>
      <div
        :style="{
          height: '204px',
          overflow: 'hidden',
        }"
      >
        <img
          :style="{ width: '100%', transform: 'translateY(-20px)' }"
          :alt="app.appName"
          :src="app.appIcon ?? app.userVO?.userAvatar"
        />
      </div>
    </template>
    <a-card-meta :title="app.appName" :description="app.appDesc">
      <template #avatar>
        <div
          :style="{ display: 'flex', alignItems: 'center', color: '#1D2129' }"
        >
          <a-avatar
            :size="24"
            :style="{ marginRight: '8px' }"
            :image-url="app.userVO?.userAvatar"
          />

          <a-typography-text>{{
            app.userVO?.userName ?? "匿名"
          }}</a-typography-text>
        </div>
      </template>
    </a-card-meta>
  </a-card>
  <ShareModal ref="shareModalRef" :link="shareLink" title="分享应用" />
</template>
<script setup lang="ts">
import {
  IconThumbUp,
  IconShareInternal,
  IconMore,
} from "@arco-design/web-vue/es/icon";
import { ref } from "vue";
import { useRouter } from "vue-router";
import ShareModal from "./ShareModal.vue";
interface Props {
  app: API.AppVO;
}
const props = withDefaults(defineProps<Props>(), {
  app: () => {
    return {};
  },
});

// 分享弹窗引用
const shareModalRef = ref();
// 分享链接
const shareLink = `${window.location.protocol}//${window.location.host}/app/detail/${props.app.id}`;
// 分享
const doShare = (e: Event) => {
  if (shareModalRef.value) {
    shareModalRef.value.openModal();
  }
  e.stopPropagation(); // 阻止冒泡
};
const router = useRouter();
const docardClick = () => {
  router.push(`/app/detail/${props.app.id}`);
};
</script>
<style scoped>
.app-card {
  width: 320px;
  border-radius: 8px;
  transition: all 0.1s;
}
.app-card:hover {
  box-shadow: 0 4px 15px 0 rgba(45, 20, 234, 0.3);
  width: 325px;
}
.icon-hover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  transition: all 0.1s;
}
.icon-hover:hover {
  background-color: rgb(var(--gray-2));
}
</style>
