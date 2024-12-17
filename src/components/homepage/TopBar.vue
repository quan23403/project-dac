<template>
  <v-app-bar app>
    <v-app-bar-nav-icon @click="toggleDrawer"></v-app-bar-nav-icon>
    <v-toolbar-title>Admin Dashboard</v-toolbar-title>
    <v-spacer></v-spacer>
    <v-btn icon>
      <v-icon>mdi-bell</v-icon>
    </v-btn>
    <v-btn icon>
      <v-icon>mdi-account</v-icon>
    </v-btn>
  </v-app-bar>
</template>

<script>
export default {
  setup(props, { emit }) {
    const toggleDrawer = () => {
      emit("toggle-drawer");
    };

    return {
      toggleDrawer,
    };
  },
};
</script>
