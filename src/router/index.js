import { createRouter, createWebHistory } from "vue-router";
import CategoryTable from "@/components/homepage/CategoryTable.vue";
import MainLayout from "@/layouts/MainLayout.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: CategoryTable,
    meta: {
      layout: MainLayout,
      requiresAuth: true, // Gán layout cho trang login
    },
  },
  {
    path: "/about",
    name: "About",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
    meta: {
      layout: MainLayout,
      requiresAuth: true,
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
      requiresAuth: true,
    },
  },
  {
    path: "/cam-cate",
    name: "CampaignCategory",
    component: () => import("../components/cam-cate-page/CamCateTable.vue"),
    meta: {
      layout: MainLayout,
      requiresAuth: true,
    },
  },
  {
    path: "/import-export-file",
    name: "ImportExportPage",
    component: () =>
      import("../components/import-export-file/ImportExportScreen.vue"),
    meta: {
      layout: MainLayout,
      requiresAuth: true,
    },
  },
  {
    path: "/anken-list",
    name: "AnkenList",
    component: () => import("../components/anken-page/AnkenList.vue"),
    meta: {
      layout: MainLayout,
      requiresAuth: true,
    },
  },
  {
    path: "/account",
    name: "UserSetting",
    component: () => import("../components/user-setting/UserInfoForm.vue"),
    meta: {
      layout: MainLayout,
      requiresAuth: true,
    },
  },
  {
    path: "/upload-file-history",
    name: "UploadFileHistory",
    component: () =>
      import("../components/upload-history/UploadFileHistory.vue"),
    meta: {
      layout: MainLayout,
      requiresAuth: true,
    },
  },
  {
    path: "/forgot-password",
    name: "ForgotPassword",
    component: () => import("../views/auth/ForgotPasswordView.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// Đặt route guard để kiểm tra trước khi vào trang yêu cầu xác thực
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token"); // Kiểm tra nếu có token trong localStorage

  // Kiểm tra nếu route yêu cầu xác thực và người dùng chưa đăng nhập
  if (to.matched.some((record) => record.meta.requiresAuth) && !token) {
    next("/login"); // Chuyển hướng đến trang login nếu chưa đăng nhập
  } else {
    next(); // Cho phép vào trang nếu đã đăng nhập hoặc không yêu cầu xác thực
  }
});

export default router;
