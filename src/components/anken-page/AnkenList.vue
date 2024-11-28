<template>
  <v-container fluid>
    <!-- Controls -->
    <v-row class="mb-4">
      <v-col cols="12" md="6">
        <v-switch
          v-model="splitTable"
          label="Split Table"
          hint="Toggle between single and split table view"
        ></v-switch>
      </v-col>
    </v-row>

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
            :items="firstTableItems"
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
import { ref, computed } from "vue";

// Table controls
const splitTable = ref(false);
const search = ref("");
const search2 = ref("");

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
  {
    title: "Status",
    align: "start",
    sortable: true,
    key: "status",
    icon: "mdi-alert-circle",
  },
  {
    title: "Actions",
    key: "actions",
    sortable: false,
    align: "end",
  },
];

// Sample data
const items = [
  { id: 1, name: "Project Alpha", status: "active" },
  { id: 2, name: "Project Beta", status: "pending" },
  { id: 3, name: "Project Gamma", status: "completed" },
  { id: 4, name: "Project Delta", status: "active" },
  { id: 5, name: "Project Epsilon", status: "pending" },
  { id: 6, name: "Project Zeta", status: "completed" },
  { id: 7, name: "Project Eta", status: "active" },
  { id: 8, name: "Project Theta", status: "pending" },
  { id: 5, name: "Project Epsilon", status: "pending" },
  { id: 6, name: "Project Zeta", status: "completed" },
  { id: 7, name: "Project Eta", status: "active" },
  { id: 8, name: "Project Theta", status: "pending" },
];

// Split items between tables when in split mode
const firstTableItems = computed(() => {
  return splitTable.value ? items.slice(0, Math.ceil(items.length / 2)) : items;
});

const secondTableItems = computed(() => {
  return items.slice(Math.ceil(items.length / 2));
});

// Status color mapping
const getStatusColor = (status) => {
  const colors = {
    active: "success",
    pending: "warning",
    completed: "info",
  };
  return colors[status] || "grey";
};

// Action methods
const editItem = (item) => {
  console.log("Edit item:", item);
};

const deleteItem = (item) => {
  console.log("Delete item:", item);
};
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
