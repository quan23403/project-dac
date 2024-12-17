<template>
  <v-container>
    <v-card variant="text" class="mx-auto" max-width="800">
      <v-card-title class="text-h4 font-weight-bold">
        Upload History
      </v-card-title>
      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="uploadHistory"
          :items-per-page="5"
          class="elevation-1"
        >
          <template v-slot:[`item.size`]="{ item }">
            {{ formatFileSize(item.size) }}
          </template>
          <template v-slot:[`item.uploadDate`]="{ item }">
            {{ formatDate(item.uploadDate) }}
          </template>
          <template v-slot:[`item.status`]="{ item }">
            <v-chip
              :color="item.status === 'SUCCESS' ? 'green' : 'red'"
              :text-color="item.status === 'SUCCESS' ? 'white' : 'white'"
            >
              {{ item.status }}
            </v-chip>
          </template>
          <template v-slot:[`item.actions`]="{ item }">
            <v-btn
              color="primary"
              size="small"
              @click="downloadFile(item)"
              :loading="downloading"
            >
              <v-icon start icon="mdi-download"></v-icon>
              Download
            </v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { getUploadFile, downloadFileUpload } from "@/api/api";

const headers = [
  { title: "File Name", align: "start", key: "fileName" },
  { title: "Size", align: "end", key: "size" },
  { title: "Upload Date", align: "start", key: "uploadDate" },
  { title: "Status", align: "start", key: "status" },
  { title: "Actions", align: "center", key: "actions", sortable: false },
];

const uploadHistory = ref([]);
const downLoading = ref();
const errorMsg = ref();

const getUploadFileFunction = async () => {
  try {
    const response = await getUploadFile();
    uploadHistory.value = response.data;
  } catch (error) {
    // Xử lý lỗi
    console.log(error);
    if (error.response) {
      // Nếu có phản hồi từ server
      errorMsg.value = `Lấy Upload File thất bại:
          ${error.response.data.message || error.message}`;
    } else {
      errorMsg.value = `Lấy Upload File thất bại: ${error.message}`;
    }
  }
};

const formatFileSize = (bytes) => {
  if (bytes === 0) return "0 Bytes";
  const k = 1024;
  const sizes = ["Bytes", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

function formatDate(dateArray) {
  if (dateArray === null) {
    return "";
  }
  console.log(dateArray);
  const date = new Date();
  date.setFullYear(dateArray[0]);
  date.setMonth(dateArray[1] - 1);
  date.setDate(dateArray[2]);
  date.setHours(dateArray[3]);
  date.setMinutes(dateArray[4]);
  date.setSeconds(dateArray[5]);
  console.log(date);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0"); // Tháng bắt đầu từ 0, cộng thêm 1 và đảm bảo 2 chữ số
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");

  return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
}

const downloadFile = async (item) => {
  downLoading.value = true;
  try {
    const response = await downloadFileUpload(item.downLoadUri);
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", item.fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link); // Dọn dẹp
    console.log(`Downloading file: ${item.fileName}`);
  } catch (error) {
    console.error("Error downloading file:", error);
  } finally {
    downLoading.value = false;
  }
};

onMounted(() => {
  getUploadFileFunction();
});
</script>
