<template>
  <v-btn color="secondary" @click="exportFileExcelCustomFunction">
    <pre>Export Data to Excel </pre>
    <v-icon icon="mdi-cloud-download"> </v-icon>
  </v-btn>
</template>

<script setup>
import { exportFileExcel, exportFileExcelCustom } from "@/api/api";
import { defineProps } from "vue";
const props = defineProps(["ankenList"]);

const exportFileExcelCustomFunction = async () => {
  try {
    const formData = {
      ids: props.ankenList,
    };
    const response = await exportFileExcelCustom(formData);
    console.log(formData);
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
