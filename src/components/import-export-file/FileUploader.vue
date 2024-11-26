<template>
  <v-card>
    <v-card-title>Upload Excel File</v-card-title>
    <v-card-text>
      <v-file-input
        v-model="file"
        label="Choose Excel file"
        accept=".xlsx, .xls"
        @change="handleFileChange"
        :error-messages="errorMessages"
      ></v-file-input>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref } from "vue";
import { defineEmits } from "vue";
const emit = defineEmits(["file-loaded"]);

const file = ref(null);
const errorMessages = ref([]);

const handleFileChange = () => {
  errorMessages.value = [];
  if (file.value) {
    try {
      emit("file-loaded", file.value);
    } catch (error) {
      errorMessages.value.push(
        "Error reading Excel file. Please check the file format."
      );
      console.error("Error importing Excel:", error);
    }
  }
};
</script>
