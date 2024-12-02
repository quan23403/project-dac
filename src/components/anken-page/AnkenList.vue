<template>
  <v-container fluid>
    <!-- Tables Container -->
    <v-row>
      <!-- First Table -->
      <v-col :cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            Anken List
            <v-spacer></v-spacer>
            <v-text-field
              v-model="search"
              append-icon="mdi-magnify"
              label="Search"
              single-line
              hide-details
              density="compact"
            ></v-text-field>
          </v-card-title>
          <v-data-table
            :headers="headers"
            :items="items"
            :search="search"
            class="elevation-1"
          >
            <!-- Custom header to show column title with icon -->
            <template v-slot:[`header.data`]="{ column }">
              <v-icon size="small" class="mr-1">{{ column.icon }}</v-icon>
              {{ column.title }}
            </template>

            <!-- Status column with chips -->
            <template v-slot:[`item.status`]="{ item }">
              <v-chip
                :color="getStatusColor(item.status)"
                size="small"
                class="text-uppercase"
              >
                {{ item.status }}
              </v-chip>
            </template>

            <!-- Actions column -->
            <template v-slot:[`item.actions`]="{ item }">
              <v-icon size="small" class="mr-2" @click="editItem(item)">
                mdi-pencil
              </v-icon>
              <v-icon size="small" @click="deleteItem(item)">
                mdi-delete
              </v-icon>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { getAnkenByUser } from "@/api/api";
// Table controls
const search = ref("");
const errorMsg = ref();
// Table headers
const headers = [
  {
    title: "ID",
    align: "start",
    sortable: true,
    key: "id",
    icon: "mdi-pound",
  },
  {
    title: "Name",
    align: "start",
    sortable: true,
    key: "name",
    icon: "mdi-account",
  },
];

// Sample data
const items = ref();

const getAnkenByUserFunction = async () => {
  try {
    const response = await getAnkenByUser();
    items.value = response.data;
  } catch (error) {
    // Xử lý lỗi
    if (error.response) {
      // Nếu có phản hồi từ server
      errorMsg.value = `Lấy Anken Name thất bại:
          ${error.response.data.message || error.message}`;
    }
  }
};

onMounted(() => {
  getAnkenByUserFunction();
});
</script>

<style scoped>
.v-data-table {
  border-radius: 8px;
}

.v-card {
  transition: all 0.3s ease;
}

.v-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>
