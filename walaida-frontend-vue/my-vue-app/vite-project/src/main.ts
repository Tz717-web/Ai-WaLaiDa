import { createApp } from "vue";
import router from "./router";
import App from "./App.vue";
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css"; // 引入arco-design的样式
import ArcoVueIcon from "@arco-design/web-vue/es/icon";
import "../src/access";
import { createPinia } from "pinia";
const pinia = createPinia();
createApp(App)
  .use(ArcoVue)
  .use(router)
  .use(ArcoVueIcon)
  .use(pinia)
  .mount("#app");
