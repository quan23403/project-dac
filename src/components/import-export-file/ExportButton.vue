<template>
  <v-btn color="primary" @click="exportToExcel" :disabled="!canExport">
    Export to Excel
  </v-btn>
</template>

<script setup>
import { computed } from "vue";
import { defineProps } from "vue";
// const props = defineProps({
//   tables: {
//     type: Array,
//     required: true,
//   },
// });

// const canExport = computed(() => {
//   return props.tables.some((table) => table.items.length > 0);
// });

// const exportToExcel = () => {
//   const workbook = XLSX.utils.book_new();

//   props.tables.forEach((table) => {
//     if (table.items.length > 0) {
//       const worksheet = XLSX.utils.json_to_sheet(table.items);
//       XLSX.utils.book_append_sheet(workbook, worksheet, table.name);
//     }
//   });

//   XLSX.writeFile(workbook, "exported_data.xlsx");
// };
</script>
