<template>
  <v-app>
    <TopBar @toggle-drawer="toggleDrawer" />
    <SideNavigation :drawer="drawer" :items="navigationItems" />
    <v-main>
      <v-container fluid>
        <CategoryTable />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from "vue";
import SideNavigation from "./SideNavigation.vue";
import TopBar from "./TopBar.vue";
import CategoryTable from "./CategoryTable.vue";

const drawer = ref(null);

const navigationItems = [
  { title: "Dashboard", icon: "mdi-view-dashboard", to: "/" },
  { title: "Users", icon: "mdi-account-group", to: "/users" },
  { title: "Products", icon: "mdi-package-variant-closed", to: "/products" },
  { title: "Orders", icon: "mdi-cart", to: "/orders" },
  { title: "Reports", icon: "mdi-chart-bar", to: "/reports" },
  { title: "Settings", icon: "mdi-cog", to: "/settings" },
];

const toggleDrawer = () => {
  drawer.value = !drawer.value;
  console.log(drawer.value);
};
</script>
