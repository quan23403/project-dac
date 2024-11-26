<template>
  <v-container>
    <!-- Tab Navigation -->
    <v-tabs v-model="activeTab" vertical>
      <v-tab v-for="(tab, index) in tabs" :key="index">{{ tab.name }}</v-tab>
    </v-tabs>

    <!-- Tab Content -->
    <v-tabs-items v-model="activeTab">
      <v-tab-item v-for="(table, index) in tabs" :key="index">
        <!-- Hiển thị dữ liệu bảng dưới dạng bảng Vuetify sử dụng v-data-table -->
        <v-data-table
          v-if="activeTab === index"
          :headers="table.headers"
          :items="table.data"
          item-key="id"
          :loading="table.loading"
          :items-per-page="10"
          :search="table.searchQuery"
        >
        </v-data-table>
      </v-tab-item>
    </v-tabs-items>
  </v-container>
</template>

<script setup>
import { ref, onMounted, defineProps } from "vue";
import axios from "axios";
const props = defineProps(["file"]);
const activeTab = ref();
const tabs = ref([
  {
    name: "Category",
    apiUrl: "http://localhost:8080/excel/preview/category",
    headers: [
      { title: "Anken Name", align: "start", key: "ankenName" },
      { title: "Category Id", key: "categoryId" },
      { title: "Category Name", key: "categoryName" },
      { title: "Type of Category", key: "typeOfCategory" },
      { title: "Budget", key: "budget" },
      { title: "Type of KPI", key: "typeOfKPI" },
      { title: "KPI Goal", key: "kpiGoal" },
      { title: "Start Date", key: "startDate" },
      { title: "End Date", key: "endDate" },
      { title: "Action", key: "action" },
    ],
    data: ref(),
    loading: false,
    searchQuery: "",
  },
  {
    name: "Account-Category",
    apiUrl: "http://localhost:8080/excel/preview/category",
    headers: [
      { title: "Anken Name", align: "start", key: "ankenName" },
      { title: "Account Id", key: "accountId" },
      { title: "Account Name", key: "accountName" },
      { title: "Account Code", key: "accountCode" },
      { title: "Media", key: "media" },
      { title: "Category Id", key: "categoryId" },
      { title: "Category Name", key: "categoryName" },
    ],
    data: ref(),
    loading: false,
    searchQuery: "",
  },
  {
    name: "Campaign-Category",
    apiUrl: "http://localhost:8080/category-binding/account-category",
    headers: [
      { title: "Anken Name", align: "start", key: "ankenName" },
      { title: "Cmpaign Id", key: "accountId" },
      { title: "Account Name", key: "accountName" },
      { title: "Account Code", key: "accountCode" },
      { title: "Media", key: "media" },
      { title: "Category Id", key: "categoryId" },
      { title: "Category Name", key: "categoryName" },
    ],
    data: ref(),
    loading: false,
    searchQuery: "",
  },
]);
const fetchTableData = async () => {
  for (let tab of tabs.value) {
    tab.loading = true; // Đánh dấu là đang tải dữ liệu
    const formData = new FormData();
    formData.append("file", props.file);
    try {
      const response = await axios.post(tab.apiUrl, formData);
      console.log(response.data);
      tab.data = response.data.map((category) => {
        // Chuyển mảng ngày tháng thành đối tượng Date
        if (Array.isArray(category.endDate)) {
          category.endDate = formatDate(
            new Date(
              category.endDate[0],
              category.endDate[1] - 1,
              category.endDate[2]
            )
          );
        }
        if (Array.isArray(category.startDate)) {
          category.startDate = formatDate(
            new Date(
              category.startDate[0],
              category.startDate[1] - 1,
              category.startDate[2]
            )
          );
        }
        return category;
      });
    } catch (error) {
      console.error(`Error fetching data for ${tab.name}:`, error);
      tab.data = []; // Nếu có lỗi, set dữ liệu là mảng rỗng
    } finally {
      tab.loading = false; // Đánh dấu là đã tải xong
    }
  }
};

const formatDate = (date) => {
  if (!(date instanceof Date)) return "";
  const day = date.getDate().toString().padStart(2, "0");
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const year = date.getFullYear();
  return `${year}-${month}-${day}`;
};

onMounted(() => {
  fetchTableData();
});
</script>

<style scoped>
/* Optional styling for the container */
</style>
