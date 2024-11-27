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
const categoryData = ref();
const categoryAccountData = ref();
const categoryCampaignData = ref();

const tabs = ref([
  {
    name: "Category",
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
    data: categoryData,
    loading: false,
    searchQuery: "",
  },
  {
    name: "Account-Category",
    headers: [
      { title: "Anken Name", align: "start", key: "ankenName" },
      { title: "Account Id", key: "accountId" },
      { title: "Account Name", key: "accountName" },
      { title: "Account Code", key: "accountCode" },
      { title: "Media", key: "media" },
      { title: "Category Id", key: "categoryId" },
      { title: "Category Name", key: "categoryName" },
      { title: "Action", key: "action" },
    ],
    data: categoryAccountData,
    loading: false,
    searchQuery: "",
  },
  {
    name: "Campaign-Category",
    headers: [
      { title: "Anken Name", align: "start", key: "ankenName" },
      { title: "Account Name", key: "accountName" },
      { title: "Account Code", key: "accountCode" },
      { title: "Media", key: "media" },
      { title: "Campaign Id", key: "campaignId" },
      { title: "Campaign Name", key: "campaignName" },
      { title: "Campaign Code", key: "campaignCode" },
      { title: "Category Id", key: "categoryId" },
      { title: "Category Name", key: "categoryName" },
      { title: "Action", key: "action" },
    ],
    data: categoryCampaignData,
    loading: false,
    searchQuery: "",
  },
]);
const fetchTableData = async () => {
  const formData = new FormData();
  formData.append("file", props.file);
  try {
    const response = await axios.post(
      "http://localhost:8080/excel/preview/category",
      formData
    );
    categoryData.value = response.data[0].data.map((category) => {
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
    categoryAccountData.value = response.data[1].data;
    categoryCampaignData.value = response.data[2].data;
  } catch (error) {
    console.error(`Error fetching data from file`, error);
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
