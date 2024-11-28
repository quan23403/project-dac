import { createRouter, createWebHistory } from "vue-router";
import CategoryTable from "@/components/homepage/CategoryTable.vue";
import MainLayout from "@/layouts/MainLayout.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: CategoryTable,
    meta: {
      layout: MainLayout, // Gán layout cho trang login
    },
  },
  {
    path: "/about",
    name: "About",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
    meta: {
      layout: MainLayout, // Gán layout cho trang login
    },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/auth/LoginView.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/auth/RegisterView.vue"),
  },
  {
    path: "/acc-cate",
    name: "AccountCategory",
    component: () => import("../components/acc-cate-page/AccCateTable.vue"),
    meta: {
      layout: MainLayout,
    },
  },
  {
    path: "/cam-cate",
    name: "CampaignCategory",
    component: () => import("../components/cam-cate-page/CamCateTable.vue"),
    meta: {
      layout: MainLayout,
    },
  },
  {
    path: "/import-export-file",
    name: "ImportExportPage",
    component: () =>
      import("../components/import-export-file/ImportExportScreen.vue"),
    meta: {
      layout: MainLayout,
    },
  },
  {
    path: "/anken-list",
    name: "AnkenList",
    component: () => import("../components/anken-page/AnkenList.vue"),
    meta: {
      layout: MainLayout,
    },
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
