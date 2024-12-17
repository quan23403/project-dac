<template>
  <v-container>
    <v-card variant="text" class="mx-auto" max-width="800">
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
              readonly
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
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getAllAnken, getCurrentUser, updateUser } from "@/api/api";
const editing = ref(false);
const errorMsg = ref();
const user = ref({});

const userBackup = ref({});

const startEditing = () => {
  editing.value = true;
  Object.assign(userBackup.value, { ...user.value });
};

const cancelEditing = () => {
  Object.assign(user.value, userBackup.value);
  editing.value = false;
};

const getUserInfor = async () => {
  try {
    const response = await getCurrentUser();
    user.value = response.data;
    console.log(user);
  } catch (error) {
    errorMsg.value = `Lấy User Info thất bại: ${error.message}`;
  }
};

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

// const saveUserInfo = async () => {
//   try {
//     const formData = {
//       firstName: user.value.firstName,
//       lastName: user.value.lastName,
//       listAnkenId: user.value.ankenListId,
//     };
//     const response = await updateUser(formData);
//     console.log(response.data);
//     userBackup.value = user.value;
//     localStorage.setItem("token", response.data.token);
//     editing.value = false;
//   } catch (error) {
//     errorMsg.value = `Update User Info thất bại: ${error.message}`;
//   }
// };

onMounted(() => {
  getUserInfor();
  fetchAllAnken();
});
</script>
