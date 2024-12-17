<template>
  <v-card class="mt-5">
    <v-card-title class="d-flex align-center">
      <!-- Cột tìm kiếm -->
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        outlined
        hide-details
        class="mr-6"
        dense
        style="max-width: 300px"
      ></v-text-field>

      <!-- Dropdown chọn loại category -->
      <v-select
        v-model="search"
        :items="typeCategoryOptions"
        label="Category"
        outlined
        dense
        class="mt-5"
        style="max-width: 200px"
      ></v-select>
    </v-card-title>
    <v-data-table :headers="headers" :items="categories" :search="search">
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Category Management</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <!-- New Category Dialog -->
          <NewCategoryDialog
            @create-success="needReload = true"
            :options="ankenOptions"
          />

          <v-dialog v-model="dialogEdit" max-width="500px">
            <v-card>
              <v-card-title>
                <span class="text-h5">Edit Category</span>
              </v-card-title>
              <v-card-text>
                <!-- Trường Name -->
                <v-text-field
                  v-model="editedItem.name"
                  label="Name"
                  required
                ></v-text-field>

                <!-- Trường Type Category -->
                <v-text-field
                  v-model="editedItem.typeCategory"
                  label="Type of Category"
                  readonly
                ></v-text-field>

                <!-- Trường Budget -->
                <v-text-field
                  v-model="editedItem.budget"
                  label="Budget"
                  type="number"
                  required
                ></v-text-field>

                <!-- Trường Start Date -->
                <v-date-input
                  v-model="editedItem.startDate"
                  label="Start Date"
                  variant="outlined"
                  persistent-placeholder
                  format="yyyy-MM-dd"
                ></v-date-input>

                <!-- Trường End Date -->
                <v-date-input
                  v-model="editedItem.endDate"
                  label="End Date"
                  variant="outlined"
                  persistent-placeholder
                  format="yyyy-MM-dd"
                ></v-date-input>

                <!-- Trường KPI Type -->
                <v-select
                  v-model="editedItem.kpiType"
                  :items="kpiTypeOptions"
                  label="Types of KPI"
                  required
                ></v-select>

                <v-select
                  v-model="editedItem.ankenName"
                  :items="ankenOptions.map((option) => option.name)"
                  label="Anken Name"
                  required
                ></v-select>

                <!-- Trường KPI Goal -->
                <v-text-field
                  v-model="editedItem.kpiGoal"
                  label="KPI Goal"
                  type="number"
                  required
                ></v-text-field>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn
                  color="blue-darken-1"
                  variant="text"
                  @click="closeUpdate"
                >
                  Cancel
                </v-btn>
                <v-btn
                  color="blue-darken-1"
                  variant="text"
                  @click="updateCategoryFunction"
                >
                  Save
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
          <v-dialog v-model="dialogDelete" max-width="500px">
            <v-card>
              <v-card-title class="text-h5"
                >Are you sure you want to delete this item?</v-card-title
              >
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue-darken-1" variant="text" @click="closeDelete"
                  >Cancel</v-btn
                >
                <v-btn
                  color="blue-darken-1"
                  variant="text"
                  @click="deleteCategoryFunction()"
                  >OK</v-btn
                >
                <v-spacer></v-spacer>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon size="small" class="me-2" @click="editItem(item)">
          mdi-pencil
        </v-icon>
        <v-icon size="small" @click="deleteItem(item.id)"> mdi-delete </v-icon>
      </template>
    </v-data-table>
  </v-card>
  <v-snackbar v-model="snackbar" timeout="2000">
    {{ text }}
    <template v-slot:actions>
      <v-btn color="blue" variant="text" @click="snackbar = false">
        Close
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script setup>
import { ref, watch, onMounted, computed } from "vue";
import NewCategoryDialog from "./dialog/NewCategoryDialog.vue";
import {
  updateCategory,
  deleteCategory,
  getAllAnken,
  getCategory,
  getAnkenByUser,
} from "@/api/api";

const snackbar = ref(false);
const text = ref();
const search = ref();
const dialogDelete = ref(false);
const dialogEdit = ref(false);
const categories = ref();
const errorMsg = ref("");
const needReload = ref(false);
const ankenOptions = ref();
const headers = [
  { title: "Anken Name", align: "start", key: "ankenName" },
  { title: "Name", align: "start", key: "name" },
  { title: "Type Category", key: "typeCategory" },
  { title: "Budget", key: "budget" },
  { title: "KPI Type", key: "kpiType" },
  { title: "KPI Goal", key: "kpiGoal" },
  { title: "Start Date", key: "startDate" },
  { title: "End Date", key: "endDate" },
  { title: "Action", key: "actions" },
];

const typeCategoryOptions = ["ACCOUNT", "CAMPAIGN"];
const kpiTypeOptions = ["IMP", "CPC", "CPV"];

const fetchCategories = async () => {
  try {
    const response = await getCategory();
    // Xử lý dữ liệu ngày tháng khi nhận từ API
    categories.value = response.data.map((category) => {
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
    console.error("Lỗi khi lấy thông tin:", error);
  }
};

const getAllAnkenFunction = async () => {
  try {
    const response = await getAnkenByUser();
    ankenOptions.value = response.data;
  } catch (error) {
    // Xử lý lỗi
    if (error.response) {
      // Nếu có phản hồi từ server
      errorMsg.value = `Lấy Anken Name thất bại:
          ${error.response.data.message || error.message}`;
    } else {
      errorMsg.value = `Lấy Anken Name thất bại: ${error.message}`;
    }
  }
};

onMounted(() => {
  getAllAnkenFunction();
  fetchCategories(); // Gọi hàm lấy thông tin khi component được mount
});

const formatDate = (date) => {
  if (!(date instanceof Date)) return "";
  const day = date.getDate().toString().padStart(2, "0");
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const year = date.getFullYear();
  return `${year}-${month}-${day}`;
};

const editedItem = ref({
  name: "",
  typeCategory: "",
  startDate: new Date(),
  endDate: new Date(),
  budget: 0,
  kpiType: "",
  kpiGoal: 0,
  ankenName: "",
});

const updateCategoryFunction = async () => {
  try {
    const formData = { ...editedItem.value };
    console.log(formData);

    // console.log(formatDate(editedItem.value.startDate));
    formData.startDate = formatDate(editedItem.value.startDate);
    formData.endDate = formatDate(editedItem.value.endDate);
    const response = await updateCategory(formData);
    // alert("Update Successfully");
    snackbar.value = true;
    text.value = "Update Successfully";
    dialogEdit.value = false;
    needReload.value = true;
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
};

function editItem(item) {
  const temp = { ...item };
  temp.startDate = new Date(item.startDate);
  temp.endDate = new Date(item.endDate);
  editedItem.value = temp;
  dialogEdit.value = true;
}

const deleteId = ref();
function deleteItem(id) {
  dialogDelete.value = true;
  deleteId.value = id;
}

const deleteCategoryFunction = async () => {
  try {
    const response = await deleteCategory(deleteId.value);
    console.log(response.data);
    snackbar.value = true;
    text.value = "Delete Successfully";
    dialogDelete.value = false;
    needReload.value = true;
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
};
function closeUpdate() {
  dialogEdit.value = false;
}
function closeDelete() {
  dialogDelete.value = false;
}

watch(needReload, (newValue) => {
  if (newValue) {
    fetchCategories(); // Gọi lại hàm fetchUsers khi needReload thay đổi
    needReload.value = false;
  }
});
</script>
