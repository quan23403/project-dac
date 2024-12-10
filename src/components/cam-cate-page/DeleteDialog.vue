<template>
  <v-dialog v-model="dialog" max-width="500px">
    <v-card>
      <v-card-title class="text-h5"
        >Are you sure you want to delete this item?</v-card-title
      >
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="close">Cancel</v-btn>
        <v-btn color="blue darken-1" text @click="deleteItem">OK</v-btn>
        <v-spacer></v-spacer>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, defineExpose } from "vue";

const props = defineProps(["itemId"]);
const emit = defineEmits(["delete", "close"]);

const dialog = ref(false);

const deleteItem = () => {
  console.log(props.itemId);
  emit("delete", props.itemId);
  close();
};

const close = () => {
  dialog.value = false;
  emit("close");
};

defineExpose({ dialog });
</script>
