<template>
  <v-container>
    <v-data-table :headers="headers" :items="formattedUsers">
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon size="small" class="me-2" @click="editItem(item)">
          mdi-pencil
        </v-icon>
        <v-icon size="small" @click="deleteItem(item.id)"> mdi-delete </v-icon>
      </template>
    </v-data-table>
    <EditUser ref="editDialog" :item="user" @success="fetchAllUsers" />
  </v-container>
</template>

<script setup>
import { getAllUsers } from "@/api/api";
import { format } from "date-fns"; // Import format từ date-fns
import { onMounted, ref, computed } from "vue";
import EditUser from "./EditUser.vue";
const errorMsg = ref();
const users = ref();
const headers = [
  { title: "User Id", align: "start", key: "id" },
  { title: "Email", key: "email" },
  { title: "First Name", key: "firstName" },
  { title: "Last Name", key: "lastName" },
  { title: "List Anken Id", key: "ankenListId" },
  { title: "Created At", key: "createdAt" },
  { title: "Updated At", key: "updatedAt" },
  { title: "Roles", key: "roles" },
  { title: "Action", key: "actions" },
];

const fetchAllUsers = async () => {
  try {
    const response = await getAllUsers();
    users.value = response.data;
  } catch (error) {
    if (error.response) {
      errorMsg.value = `Lấy Anken Name thất bại:
          ${error.response.data.message || error.message}`;
    }
  }
};

const user = ref();
const editDialog = ref(null);

const editItem = (item) => {
  user.value = { ...item };
  console.log(item);
  editDialog.value.dialog = true;
};
// This will format the ankenListId before displaying it
const formattedUsers = computed(() => {
  if (users.value) {
    return users.value.map((user) => ({
      ...user,
      // Format the ankenListId (e.g., join IDs with commas)
      ankenListId: user.ankenListId.length > 0 ? user.ankenListId : null,
      createdAt: user.createdAt
        ? format(new Date(user.createdAt), "yyyy-MM-dd HH:mm:ss")
        : "N/A", // Định dạng ngày tháng
      updatedAt: user.updatedAt
        ? format(new Date(user.updatedAt), "yyyy-MM-dd HH:mm:ss")
        : "N/A", // Định dạng ngày tháng
    }));
  }
  return [];
});

onMounted(() => {
  fetchAllUsers();
});
</script>
