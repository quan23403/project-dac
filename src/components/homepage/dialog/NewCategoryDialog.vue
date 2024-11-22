<template>
  <v-dialog v-model="dialog" max-width="500px">
    <template v-slot:activator="{ props }">
      <v-btn color="primary" dark class="mb-2" v-bind="props">
        New Category
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class="text-h5">New Category</span>
      </v-card-title>
      <v-card-text>
        <!-- Trường Name -->
        <v-text-field
          v-model="newItem.name"
          label="Name"
          required
        ></v-text-field>

        <!-- Trường Type Category -->
        <v-select
          v-model="newItem.typeCategory"
          :items="typeCategoryOptions"
          label="Type"
          required
        ></v-select>

        <!-- Trường Budget -->
        <v-text-field
          v-model="newItem.budget"
          label="Budget"
          type="number"
          required
        ></v-text-field>

        <!-- Trường Start Date -->
        <v-date-input
          v-model="newItem.startDate"
          label="Start Date"
          variant="outlined"
          persistent-placeholder
        ></v-date-input>

        <!-- Trường End Date -->
        <v-date-input
          v-model="newItem.endDate"
          label="End Date"
          variant="outlined"
          persistent-placeholder
        ></v-date-input>

        <!-- Trường KPI Type -->
        <v-select
          v-model="newItem.kpiType"
          :items="kpiTypeOptions"
          label="Types of KPI"
          required
        ></v-select>
        <v-select
          v-model="newItem.kpiType"
          :items="kpiTypeOptions"
          label="Anken Name"
          required
        ></v-select>
        <!-- Trường KPI Goal -->
        <v-text-field
          v-model="newItem.kpiGoal"
          label="KPI Goal"
          type="number"
          required
        ></v-text-field>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="closeDialog" color="grey darken-1" text>Cancel</v-btn>
        <v-btn @click="createCategoryFunction" color="primary">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref } from "vue";
import { createCategory } from "@/api/api";
import { defineEmits } from "vue";
const dialog = ref(false);
const errorMsg = ref("");
const typeCategoryOptions = ["ACCOUNT", "CAMPAIGN"];
const kpiTypeOptions = ["IMP", "CPC", "CPV"];
const newItem = ref({
  name: "",
  typeCategory: "",
  startDate: new Date(),
  endDate: new Date(),
  budget: 0,
  kpiType: "",
  kpiGoal: 0,
  ankenName: "",
});

const createCategoryFunction = async () => {
  try {
    const response = await createCategory(newItem.value);
    console.log(response.data);
    alert("Create Successfully");
    dialog.value = false;
    emit("create-success");
  } catch (error) {
    // Xử lý lỗi
    console.log(error);
    if (error.response) {
      // Nếu có phản hồi từ server
      errorMsg.value = `Đăng ký thất bại:
          ${error.response.data.message || error.message}`;
    } else {
      errorMsg.value = `Đăng ký thất bại: ${error.message}`;
    }
  }
};
const emit = defineEmits();
// Methods
const closeDialog = () => {
  dialog.value = false;
};
</script>
