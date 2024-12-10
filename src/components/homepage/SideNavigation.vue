<template>
  <v-navigation-drawer v-model="drawerModel" app>
    <v-list>
      <v-list-item-group v-for="(item, i) in items" :key="i">
        <v-list-item v-if="item.title !== 'Logout'" :to="item.to" link>
          <template v-slot:prepend>
            <v-icon>{{ item.icon }}</v-icon>
          </template>
          <v-list-item-title v-text="item.title"></v-list-item-title>
        </v-list-item>
        <v-list-item v-if="item.title === 'Logout'" @click="logout" link>
          <template v-slot:prepend>
            <v-icon>{{ item.icon }}</v-icon>
          </template>
          <v-list-item-title v-text="item.title"></v-list-item-title>
        </v-list-item>
      </v-list-item-group>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import { defineComponent, computed } from "vue";
import router from "@/router";
export default defineComponent({
  props: {
    drawer: {
      type: Boolean,
      required: true,
    },
    items: {
      type: Array,
      required: true,
    },
  },
  setup(props, { emit }) {
    // Tạo computed property để đồng bộ hóa v-model
    const drawerModel = computed({
      get: () => props.drawer,
      set: (value) => emit("update:drawer", value),
    });

    const logout = () => {
      localStorage.removeItem("token");
      router.push({ name: "Login" });
    };

    return {
      drawerModel,
      logout,
    };
  },
});
</script>
