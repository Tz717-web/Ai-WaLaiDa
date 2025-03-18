<template>
  <div id="UserLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <h1>
          <img src="../img/哇·来答.svg" class="logo" />哇来答 AI 答题应用平台
        </h1>
      </a-layout-header>
      <a-layout-content class="content"><Router-view /></a-layout-content>
      <a-layout-footer class="footer"
        ><a href="">小智开源 by 哇来答</a></a-layout-footer
      >
    </a-layout>
  </div>
</template>
<script setup lang="ts"></script>
<style scoped>
#UserLayout {
  height: 100vh;
  width: 100vw;
  background-image: url("../img/userPage.png");
  background-repeat: no-repeat;
  background-size: 100% 100%;
}

#UserLayout .header {
  margin-bottom: 12px;
  height: 50px;
}
#UserLayout .header h1 {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center;
  font-family: "Franklin Gothic Medium", "Arial Narrow", Arial, sans-serif;
  font-size: 30px;
  font-weight: 550;
  color: #f2fcfc;
}
#UserLayout .content {
  padding: 24px;
  margin-top: 10px;
}
#UserLayout .footer {
  text-align: center;
  height: 18px;
  line-height: 18px;
  background-color: #edf2fa;
  box-sizing: #eee 1px 1px 5px;
}
#UserLayout .footer a {
  color: #434242;
  text-decoration: none;
  font-size: 14px;
  font-weight: 530;
}
#UserLayout .header h1 .logo {
  width: 40px;
  padding-right: 10px;
}
</style>
