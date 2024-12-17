<template>
  <v-dialog v-model="dialog" max-width="900px">
    <v-card>
      <v-container>
        <v-card-title class="text-h4 font-weight-bold">
          User Information
        </v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="user.firstName"
                label="First Name"
                :readonly="!editing"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="user.lastName"
                label="Last Name"
                :readonly="!editing"
                :rules="[(v) => !!v || 'Last name is required']"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="user.email"
                label="Email"
                readonly
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-select
                v-model="user.ankenListId"
                :items="ankenList"
                item-value="id"
                item-title="name"
                label="Anken"
                :readonly="!editing"
                multiple
              ></v-select>
            </v-col>
            <v-col cols="12">
              <v-select
                v-model="user.roles"
                :items="rolesList"
                label="Role"
                :readonly="!editing"
                multiple
              ></v-select>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn v-if="!editing" color="primary" @click="startEditing">
            Edit
          </v-btn>
          <v-btn v-if="editing" color="success" @click="saveUserInfo">
            Save
          </v-btn>
          <v-btn v-if="editing" color="error" @click="cancelEditing">
            Cancel
          </v-btn>
        </v-card-actions>
      </v-container>
    </v-card>
  </v-dialog>
</template>

<script setup>
import {
  ref,
  onMounted,
  defineProps,
  defineExpose,
  defineEmits,
  watch,
} from "vue";
import { getAllAnken, updateUser } from "@/api/api";
const editing = ref(false);
const errorMsg = ref();
const user = ref({});
const dialog = ref(false);
const userBackup = ref({});
const props = defineProps(["item"]);
defineExpose({ dialog });
const emit = defineEmits(["success"]);
const rolesList = ["ADMIN", "USER"];
const startEditing = () => {
  editing.value = true;
  Object.assign(userBackup.value, { ...user.value });
};

const cancelEditing = () => {
  Object.assign(user.value, userBackup.value);
  editing.value = false;
};

watch(
  () => props.item,
  (newItem) => {
    user.value = { ...newItem };
  },
  { deep: true }
);

const ankenList = ref();
const fetchAllAnken = async () => {
  try {
    const response = await getAllAnken();
    ankenList.value = response.data;
    console.log(ankenList.value);
  } catch (error) {
    errorMsg.value = `Lấy Anken List thất bại: ${error.message}`;
  }
};

const saveUserInfo = async () => {
  try {
    const formData = {
      firstName: user.value.firstName,
      lastName: user.value.lastName,
      listAnkenId: user.value.ankenListId,
      roles: user.value.roles,
    };
    const response = await updateUser(formData, user.value.id);
    console.log(response.data);
    userBackup.value = user.value;
    editing.value = false;
    emit("success");
  } catch (error) {
    errorMsg.value = `Update User Info thất bại: ${error.message}`;
  }
};

onMounted(() => {
  fetchAllAnken();
});
</script>
