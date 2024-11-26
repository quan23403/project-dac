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

const drawer = ref(null);

const navigationItems = [
  { title: "Category", icon: "mdi-view-dashboard", to: "/" },
  { title: "Account-Category", icon: "mdi-account-group", to: "/acc-cate" },
  {
    title: "Campaign-Category",
    icon: "mdi-package-variant-closed",
    to: "/cam-cate",
  },
  { title: "Import/Export File", icon: "mdi-cart", to: "/orders" },
  { title: "Reports", icon: "mdi-chart-bar", to: "/reports" },
  { title: "Settings", icon: "mdi-cog", to: "/settings" },
];

const toggleDrawer = () => {
  drawer.value = !drawer.value;
  console.log(drawer.value);
};
</script>
