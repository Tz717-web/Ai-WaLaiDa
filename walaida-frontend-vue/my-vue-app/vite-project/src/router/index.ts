import { createRouter, createWebHistory } from "vue-router";
import { routes } from "./routes";
// import Layout from '../components/HelloWorld.vue'

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
