<template>
  <v-dialog v-model="dialog" max-width="900px">
    <v-card>
      <v-card-title>
        <span class="text-h5">Edit Account-Category</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                v-model="editedItem.ankenName"
                label="Anken Name"
                readonly
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                v-model="editedItem.accountId"
                label="Account Id"
                readonly
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                v-model="editedItem.accountName"
                label="Account Name"
                readonly
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                v-model="editedItem.accountCode"
                label="Account Code"
                readonly
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                v-model="editedItem.media"
                label="Media"
                readonly
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                v-model="editedItem.categoryId"
                label="Category Id"
                type="number"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-select
                v-model="editedItem.categoryName"
                :items="props.categoryOptions"
                item-title="name"
                label="Category Name"
                required
                @update:modelValue="updateCategoryId"
              ></v-select>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="close">Cancel</v-btn>
        <v-btn color="blue darken-1" text @click="save()">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { watch, ref, defineProps, defineEmits, defineExpose } from "vue";
const props = defineProps(["item", "categoryOptions"]);

const emit = defineEmits(["update", "close"]);
const dialog = ref(false);
const editedItem = ref();

watch(
  () => props.item,
  (newItem) => {
    editedItem.value = { ...newItem };
  },
  { deep: true }
);

const save = () => {
  console.log(editedItem.value);
  emit("update", editedItem.value);
  close();
};

const close = () => {
  dialog.value = false;
  emit("close");
};

defineExpose({ dialog });

// Hàm cập nhật categoryId khi chọn categoryName
const updateCategoryId = () => {
  const selectedCategory = props.categoryOptions.find(
    (category) => category.name === editedItem.value.categoryName
  );
  if (selectedCategory) {
    editedItem.value.categoryId = selectedCategory.id;
    console.log(editedItem);
  }
};
</script>
