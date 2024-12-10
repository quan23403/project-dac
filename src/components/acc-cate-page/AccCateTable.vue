<template>
  <v-container>
    <!-- Tabs để chuyển giữa các bảng -->
    <v-tabs v-model="selectedTab">
      <v-tab v-for="(table, index) in tables" :key="index">
        {{ table.name }}
      </v-tab>
    </v-tabs>

    <v-tabs-items v-model="selectedTab">
      <v-tab-item v-for="(table, index) in tables" :key="index">
        <!-- Hiển thị dữ liệu bảng dưới dạng bảng Vuetify sử dụng v-data-table -->
        <v-data-table
          :headers="table.headers"
          :items="table.data"
          item-key="id"
          :loading="table.loading"
          :items-per-page="10"
          :search="table.searchQuery"
        >
          <template v-slot:top>
            <v-text-field
              v-model="table.searchQuery"
              label="Tìm kiếm"
              class="mx-4"
              clearable
            ></v-text-field>
          </template>
          <template v-slot:[`item.actions`]="{ item }">
            <v-icon size="small" class="me-2" @click="editItem(item)">
              mdi-pencil
            </v-icon>
            <v-icon size="small" @click="deleteItem(item.accountId)">
              mdi-delete
            </v-icon>
          </template>
        </v-data-table>
      </v-tab-item>
    </v-tabs-items>
    <EditDialog
      ref="editDialog"
      :item="editedItem"
      :categoryOptions="categoryOptions"
      @update="updateItem"
      @close="closeEdit"
    />

    <DeleteDialog
      ref="deleteDialog"
      :itemId="itemToDeleteId"
      @delete="confirmDelete"
      @close="closeDelete"
    />
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import {
  getAllCategoryAccount,
  updateCategoryBindingDetails,
  deleteCategoryBinding,
} from "@/api/api";
import axios from "axios";
import EditDialog from "./EditDialog.vue";
import DeleteDialog from "./DeleteDialog.vue";
// Dữ liệu tabs và bảng
const selectedTab = ref(0);
const categoryOptions = ref();
const errorMsg = ref();
const tables = ref([
  {
    name: "Account-Category",
    apiUrl: "http://localhost:8080/category-binding/account-category",
    headers: [
      { title: "Anken Name", align: "start", key: "ankenName" },
      { title: "Account Id", key: "accountId" },
      { title: "Account Name", key: "accountName" },
      { title: "Account Code", key: "accountCode" },
      { title: "Media", key: "media" },
      { title: "Category Id", key: "categoryId" },
      { title: "Category Name", key: "categoryName" },
      { title: "Action", key: "actions" },
    ],
    data: ref(),
    loading: false,
    searchQuery: "",
  },
]);

const fetchCategoryAccount = async () => {
  try {
    const response = await getAllCategoryAccount();
    categoryOptions.value = response.data;
    // console.log(categoryOptions.value);
  } catch (error) {
    // Xử lý lỗi
    if (error.response) {
      // Nếu có phản hồi từ server
      errorMsg.value = `Lấy Anken Name thất bại:
          ${error.response.data.message || error.message}`;
    }
  }
};
// Lấy dữ liệu từ API
const fetchTableData = async () => {
  for (let table of tables.value) {
    table.loading = true; // Đánh dấu là đang tải dữ liệu
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get(table.apiUrl, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      // Lưu trữ dữ liệu và cột vào mỗi bảng
      table.data = response.data;
    } catch (error) {
      console.error(`Error fetching data for ${table.name}:`, error);
      table.data = []; // Nếu có lỗi, set dữ liệu là mảng rỗng
    } finally {
      table.loading = false; // Đánh dấu là đã tải xong
    }
  }
};

// Gọi API khi component được mount
onMounted(() => {
  fetchCategoryAccount();
  fetchTableData();
});

const editDialog = ref(null);
const deleteDialog = ref(null);
const editedItem = ref();
const itemToDeleteId = ref(null);

const editItem = (item) => {
  editedItem.value = { ...item };
  editDialog.value.dialog = true;
};

const closeEdit = () => {
  editedItem.value = {};
};

const updateItem = async (formData) => {
  try {
    const response = await updateCategoryBindingDetails({
      categoryId: formData.categoryId,
      entityId: formData.accountId,
      entityType: "ACCOUNT",
    });
    console.log(response);
  } catch (error) {
    // Xử lý lỗi
    console.log(error);
    if (error.response) {
      // Nếu có phản hồi từ server
      errorMsg.value = `Update thất bại:
          ${error.response.data.message || error.message}`;
    } else {
      errorMsg.value = `Update thất bại: ${error.message}`;
    }
  }

  await fetchTableData();
};

const deleteItem = (id) => {
  itemToDeleteId.value = id;
  deleteDialog.value.dialog = true;
};

const closeDelete = () => {
  itemToDeleteId.value = null;
};

const confirmDelete = async (id) => {
  console.log(id);
  try {
    const response = await deleteCategoryBinding({
      entityId: id,
      typeCategory: "ACCOUNT",
    });
    console.log(response);
  } catch (error) {
    // Xử lý lỗi
    console.log(error);
    if (error.response) {
      // Nếu có phản hồi từ server
      errorMsg.value = `Delete thất bại:
          ${error.response.data.message || error.message}`;
    } else {
      errorMsg.value = `Delete thất bại: ${error.message}`;
    }
  }
  await fetchTableData();
};
</script>

<style scoped>
/* Các style tùy chỉnh */
</style>
