<template>
  <v-btn color="primary" @click="exportFileExcelFunction">
    Export Data to Excel
  </v-btn>
</template>

<script setup>
import { computed } from "vue";
import { defineProps } from "vue";
import { exportFileExcel } from "@/api/api";
const exportFileExcelFunction = async () => {
  try {
    const response = await exportFileExcel();
    console.log(response);
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "data_export.xlsx");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link); // Dọn dẹp
  } catch (error) {
    console.error("Có lỗi xảy ra:", error);
  }
};
</script>
