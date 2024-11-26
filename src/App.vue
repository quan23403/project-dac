<template>
  <v-app>
    <v-main>
      <component :is="layout">
        <router-view></router-view>
      </component>
    </v-main>
  </v-app>
</template>

<script>
import { defineComponent } from "vue";
import LoginLayout from "./layouts/LoginLayout.vue";

export default defineComponent({
  computed: {
    layout() {
      // Lấy layout từ meta hoặc mặc định là MainLayout
      return this.$route.meta.layout || LoginLayout;
    },
  },
});
</script>
