<template>
  <v-navigation-drawer v-model="drawerModel" app>
    <v-list>
      <v-list-item v-for="(item, i) in items" :key="i" :to="item.to" link>
        <template v-slot:prepend>
          <v-icon>{{ item.icon }}</v-icon>
        </template>
        <v-list-item-title v-text="item.title"></v-list-item-title>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import { defineComponent, computed } from "vue";

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

    return {
      drawerModel,
    };
  },
});
</script>
