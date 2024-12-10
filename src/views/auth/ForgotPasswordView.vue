<template>
  <div class="login-container">
    <div class="background-image"></div>
    <ForgotPassword></ForgotPassword>
  </div>
</template>

<script>
import ForgotPassword from "@/components/auth/ForgotPassword.vue";
export default {
  components: {
    ForgotPassword,
  },
};
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("https://images4.alphacoders.com/134/1341416.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
</style>
