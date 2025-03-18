<template>
  <div id="Layout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header"><HeaderLayout /> </a-layout-header>
      <a-layout-content class="content"><Router-view /></a-layout-content>
      <a-layout-footer class="footer"
        ><a href="">arco.design</a></a-layout-footer
      >
    </a-layout>
  </div>
</template>
<script setup lang="ts">
import HeaderLayout from "../components/Header.vue";
</script>
<style scoped>
#Layout {
  height: 100vh;
  width: 100vw;
  background-color: #f0f2f5;
}

#Layout .header {
  margin-bottom: 12px;
  background-color: #fff;
  height: 50px;
  box-shadow: #eee 1px 1px 5px;
}

.content {
  background-color: #fff;
  padding: 24px;
  margin-top: 28px;
}
.footer {
  background-color: #f0f2f5;
  text-align: center;
  height: 32px;
  line-height: 32px;
}
</style>
