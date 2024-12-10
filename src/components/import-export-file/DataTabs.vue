<template>
  <v-card>
    <v-tabs v-model="activeTab">
      <v-tab v-for="table in tables" :key="table.name">
        {{ table.name }}
      </v-tab>
    </v-tabs>
    <v-tabs-items v-model="activeTab">
      <v-tab-item v-for="table in tables" :key="table.name">
        <v-data-table
          :headers="table.headers"
          :items="table.items"
          class="elevation-1"
        ></v-data-table>
      </v-tab-item>
    </v-tabs-items>
  </v-card>
</template>

<script setup>
import { ref } from "vue";
import { defineProps } from "vue";
defineProps({
  tables: {
    type: Array,
    required: true,
  },
});

const activeTab = ref(0);
</script>
