<template>
  <v-app>
    <TopBar @toggle-drawer="toggleDrawer" />
    <SideNavigation :drawer="drawer" :items="navigationItems" />
    <v-main>
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from "vue";
import SideNavigation from "@/components/homepage/SideNavigation.vue";
import TopBar from "@/components/homepage/TopBar.vue";

const drawer = ref(false);

const navigationItems = [
  { title: "Category", icon: "mdi-view-dashboard", to: "/" },
  { title: "Users Management", icon: "mdi-view-dashboard", to: "/users" },
  { title: "Anken List", icon: "mdi-format-list-numbered", to: "/anken-list" },
  { title: "Account-Category", icon: "mdi-account-group", to: "/acc-cate" },
  {
    title: "Campaign-Category",
    icon: "mdi-package-variant-closed",
    to: "/cam-cate",
  },
  {
    title: "Upload/Download File",
    icon: "mdi-file-excel",
    to: "/import-export-file",
  },
  {
    title: "Upload File History",
    icon: "mdi-history",
    to: "/upload-file-history",
  },
  { title: "Account", icon: "mdi-account", to: "/account" },
  { title: "Logout", icon: "mdi-logout" },
];

const toggleDrawer = () => {
  drawer.value = !drawer.value;
};
</script>
