<template>
  <v-card class="mt-5">
    <v-card-title>
      Category
      <v-spacer></v-spacer>
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        single-line
        hide-details
      ></v-text-field>
    </v-card-title>
    <v-data-table :headers="headers" :items="categories" :search="search">
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Category Management</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <v-dialog v-model="dialog" max-width="500px">
            <template v-slot:activator="{ props }">
              <v-btn color="primary" dark class="mb-2" v-bind="props">
                New Category
              </v-btn>
            </template>
            <v-card>
              <v-card-title>
                <span class="text-h5">{{ formTitle }}</span>
              </v-card-title>
              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col cols="12" sm="6" md="4">
                      <v-text-field
                        v-model="editedItem.name"
                        label="Name"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6" md="4">
                      <v-text-field
                        v-model="editedItem.email"
                        label="Email"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6" md="4">
                      <v-text-field
                        v-model="editedItem.role"
                        label="Role"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue-darken-1" variant="text" @click="close">
                  Cancel
                </v-btn>
                <v-btn color="blue-darken-1" variant="text" @click="save">
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
                  @click="deleteItemConfirm"
                  >OK</v-btn
                >
                <v-spacer></v-spacer>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon size="small" class="me-2" @click="editItem(item.raw)">
          mdi-pencil
        </v-icon>
        <v-icon size="small" @click="deleteItem(item.raw)"> mdi-delete </v-icon>
      </template>
    </v-data-table>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
const search = ref("");
const dialog = ref(false);
const dialogDelete = ref(false);
const categories = ref();
const headers = [
  { title: "Name", align: "start", key: "name" },
  { title: "Type Category", key: "typeCategory" },
  { title: "Budget", key: "budget" },
  { title: "Start Date", key: "startDate" },
  { title: "End Date", key: "endDate" },
  { title: "KPI Type", key: "kpiType" },
  { title: "KPI Goal", key: "kpiGoal" },
  { title: "Action", key: "actions" },
];

const fetchCategories = async () => {
  try {
    const response = await axios.get("http://localhost:8080/category");
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
    console.log(categories.value); // Kiểm tra dữ liệu sau khi xử lý
  } catch (error) {
    console.error("Lỗi khi lấy thông tin:", error);
  }
};
onMounted(() => {
  fetchCategories(); // Gọi hàm lấy thông tin khi component được mount
});

const formatDate = (date) => {
  if (!(date instanceof Date)) return "";
  const day = date.getDate().toString().padStart(2, "0");
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const year = date.getFullYear();
  return `${year}-${month}-${day}`;
};
const editedIndex = ref(-1);
const editedItem = ref({
  name: "",
  email: "",
  role: "",
  lastLogin: "",
});
const defaultItem = {
  name: "",
  email: "",
  role: "",
  lastLogin: "",
};

const formTitle = computed(() => {
  return editedIndex.value === -1 ? "New User" : "Edit User";
});

function editItem(item) {
  editedIndex.value = categories.value.indexOf(item);
  editedItem.value = Object.assign({}, item);
  dialog.value = true;
}

function deleteItem(item) {
  editedIndex.value = categories.value.indexOf(item);
  editedItem.value = Object.assign({}, item);
  dialogDelete.value = true;
}

function deleteItemConfirm() {
  categories.value.splice(editedIndex.value, 1);
  closeDelete();
}

function close() {
  dialog.value = false;
  editedItem.value = Object.assign({}, defaultItem);
  editedIndex.value = -1;
}

function closeDelete() {
  dialogDelete.value = false;
  editedItem.value = Object.assign({}, defaultItem);
  editedIndex.value = -1;
}

function save() {
  if (editedIndex.value > -1) {
    Object.assign(categories.value[editedIndex.value], editedItem.value);
  } else {
    categories.value.push(editedItem.value);
  }
  close();
}
</script>
