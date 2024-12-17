<!-- <template>
  <v-dialog v-model="dialog" max-width="500px">
    <v-card>
      <v-card-title>
        <span class="text-h5">Edit Category</span>
      </v-card-title>
      <v-card-text>
        <v-text-field
          v-model="props.editedItem.name"
          label="Name"
        ></v-text-field>
        <v-text-field v-model="editedItem.email" label="Email"></v-text-field>
        <v-text-field v-model="editedItem.role" label="Role"></v-text-field>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="closeDialog" color="blue-darken-1" variant="text"
          >Cancel</v-btn
        >
        <v-btn @click="save" color="blue-darken-1" variant="text">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits } from "vue";

// Props
const props = defineProps({
  dialogEdit: Boolean,
  editedItem: Object,
});

// Emits
const emit = defineEmits(["update:dialog", "saveItem"]);
const dialog = ref(props.dialogEdit);
// Methods
const closeDialog = () => {
  emit("update:dialog", false);
};

const save = () => {
  emit("saveItem", props.editedItem);
};
</script> -->
