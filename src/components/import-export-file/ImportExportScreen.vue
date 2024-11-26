<template>
  <v-container>
    <h1 class="text-h4 mb-6">Data Import/Export</h1>
    <v-row>
      <v-col cols="12">
        <FileUploader @file-loaded="handleFileLoaded" />
      </v-col>
    </v-row>
    <v-row v-if="showPreview">
      <v-col cols="12">
        <DataPreview
          :file="file"
          @confirm-import="confirmImport"
          @cancel-import="cancelImport"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <ExportButton :tables="tables" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import FileUploader from "./FileUploader.vue";
import DataPreview from "./DataPreview.vue";
import ExportButton from "./ExportButton.vue";

const showPreview = ref(false);
const file = ref([]);
const tables = ref([
  { name: "Users", headers: [], items: [] },
  { name: "Products", headers: [], items: [] },
  { name: "Orders", headers: [], items: [] },
]);

const handleFileLoaded = (data) => {
  file.value = data;
  showPreview.value = true;
};

// const confirmImport = () => {
//   tables.value = previewData.value.map((table, index) => ({
//     name: tables.value[index].name,
//     headers: Object.keys(table[0]).map((key) => ({
//       text: key.charAt(0).toUpperCase() + key.slice(1),
//       value: key,
//     })),
//     items: table,
//   }));
//   showPreview.value = false;
//   previewData.value = [];
// };

const cancelImport = () => {
  showPreview.value = false;
  file.value = null;
};
</script>
