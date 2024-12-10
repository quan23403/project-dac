<template>
  <v-container>
    <h1 class="text-h4 mb-6">Upload/Download File</h1>
    <v-autocomplete
      v-model="selectedItems"
      :items="options"
      item-value="id"
      item-title="name"
      label="Select Anken"
      multiple
      searchable
      @change="handleSelectChange"
    >
      <!-- Thêm mục 'Chọn tất cả' vào đầu danh sách -->
      <template v-slot:prepend-item>
        <v-list-item @click="selectAll">
          <v-list-item-content>
            <v-list-item-title>Chọn tất cả</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </template>
    </v-autocomplete>
    <v-row>
      <v-col cols="12">
        <ExportButton :ankenList="selectedItems" />
      </v-col>
    </v-row>
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
  </v-container>
</template>

<script setup>
import { onMounted, ref } from "vue";
import FileUploader from "./FileUploader.vue";
import DataPreview from "./DataPreview.vue";
import ExportButton from "./ExportButton.vue";
import { getAnkenByUser } from "@/api/api";

const options = ref([]);
const showPreview = ref(false);
const file = ref([]);
const selectedItems = ref([]); // Mảng lưu trữ các mục đã chọn
const errorMsg = ref();
const handleFileLoaded = (data) => {
  file.value = data;
  showPreview.value = true;
};

const cancelImport = () => {
  showPreview.value = false;
  file.value = null;
};

// Hàm selectAll: Chọn tất cả hoặc bỏ chọn tất cả các mục
const selectAll = () => {
  if (selectedItems.value.length === options.value.length) {
    selectedItems.value = []; // Nếu đã chọn tất cả, bỏ chọn
  } else {
    selectedItems.value = options.value.map((option) => option.id); // Chọn tất cả
  }
};

// Hàm handleSelectChange: Đảm bảo chọn tất cả các mục khi cần
const handleSelectChange = () => {
  if (selectedItems.value.length === options.value.length) {
    selectedItems.value = options.value.map((option) => option.id); // Chọn tất cả nếu tất cả đã được chọn
  }
};

const fetchAnkenByUser = async () => {
  try {
    const response = await getAnkenByUser();
    options.value = response.data;
  } catch (error) {
    // Xử lý lỗi
    console.log(error);
    if (error.response) {
      // Nếu có phản hồi từ server
      errorMsg.value = `Fetch Anken thất bại:
          ${error.response.data.message || error.message}`;
    } else {
      errorMsg.value = `Fetch Anken thất bại: ${error.message}`;
    }
  }
};
onMounted(() => {
  fetchAnkenByUser();
});
</script>
