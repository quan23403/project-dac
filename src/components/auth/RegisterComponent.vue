<template>
  <div class="register-container">
    <div class="background-image"></div>
    <div class="content-wrapper">
      <v-container class="fill-height" fluid>
        <v-row align="center" justify="center">
          <v-col cols="12" sm="8" md="6" lg="4">
            <v-card class="register-card">
              <v-card-title class="text-h4 font-weight-bold text-center my-4">
                Create Account
              </v-card-title>
              <v-card-text>
                <v-form @submit.prevent="handleRegister" ref="form">
                  <v-text-field
                    v-model="firstName"
                    :rules="[rules.required]"
                    label="First Name"
                    prepend-inner-icon="mdi-account"
                    variant="outlined"
                    class="mb-4"
                  ></v-text-field>
                  <v-text-field
                    v-model="lastName"
                    :rules="[rules.required]"
                    label="Last Name"
                    prepend-inner-icon="mdi-account"
                    variant="outlined"
                    class="mb-4"
                  ></v-text-field>
                  <v-text-field
                    v-model="email"
                    :rules="[rules.required, rules.email]"
                    label="Email"
                    prepend-inner-icon="mdi-email"
                    variant="outlined"
                    class="mb-4"
                  ></v-text-field>
                  <v-text-field
                    v-model="password"
                    :rules="[rules.required, rules.minLength]"
                    label="Password"
                    prepend-inner-icon="mdi-lock"
                    :append-inner-icon="
                      showPassword ? 'mdi-eye' : 'mdi-eye-off'
                    "
                    @click:append-inner="showPassword = !showPassword"
                    :type="showPassword ? 'text' : 'password'"
                    variant="outlined"
                    class="mb-4"
                  ></v-text-field>
                  <v-text-field
                    v-model="confirmPassword"
                    :rules="[rules.required, rules.passwordMatch]"
                    label="Confirm Password"
                    prepend-inner-icon="mdi-lock-check"
                    :type="showPassword ? 'text' : 'password'"
                    variant="outlined"
                    class="mb-4"
                  ></v-text-field>
                  <v-btn
                    block
                    color="primary"
                    size="large"
                    type="submit"
                    :loading="isLoading"
                  >
                    Register
                  </v-btn>
                </v-form>
              </v-card-text>
              <v-card-actions class="justify-center">
                <v-btn variant="text" @click="goToLogin">
                  Already have an account? Login
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </div>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" location="top">
      {{ snackbar.text }}
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();
const form = ref(null);
const firstName = ref("");
const lastName = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");
const showPassword = ref(false);
const isLoading = ref(false);
const snackbar = reactive({
  show: false,
  text: "",
  color: "info",
});

const rules = {
  required: (v) => !!v || "This field is required",
  email: (v) => /.+@.+\..+/.test(v) || "Please enter a valid email",
  minLength: (v) => v.length >= 8 || "Password must be at least 8 characters",
  passwordMatch: (v) => v === password.value || "Passwords do not match",
};

const showSnackbar = (text, color = "info") => {
  snackbar.text = text;
  snackbar.color = color;
  snackbar.show = true;
};

const useAuth = () => {
  const register = async (userData) => {
    try {
      isLoading.value = true;
      // Replace with your actual API endpoint
      const response = await axios.post(
        "https://api.example.com/register",
        userData
      );
      console.log(response);
      showSnackbar("Registration successful", "success");
      router.push("/login");
    } catch (error) {
      console.error("Registration error:", error);
      showSnackbar(
        error.response?.data?.message || "Registration failed",
        "error"
      );
    } finally {
      isLoading.value = false;
    }
  };

  return { register };
};

const { register } = useAuth();

const handleRegister = async () => {
  const { valid } = await form.value.validate();
  if (valid) {
    const userData = {
      firstName: firstName.value,
      lastName: lastName.value,
      email: email.value,
      password: password.value,
    };
    await register(userData);
  }
};

const goToLogin = () => {
  router.push("/login");
};
</script>

<style scoped>
.register-container {
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

.content-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(1px);
  z-index: 1;
}

.register-card {
  background: rgba(255, 255, 255, 0.7) !important;
  backdrop-filter: blur(10px);
  border-radius: 16px !important;
  padding: 2rem;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
}

.v-text-field ::v-deep .v-field__outline__start,
.v-text-field ::v-deep .v-field__outline__end {
  border-color: rgba(255, 255, 255, 0.7) !important;
}

.v-text-field ::v-deep .v-field__outline__notch {
  border-color: rgba(255, 255, 255, 0.7) !important;
}

.v-text-field ::v-deep .v-field__outline {
  color: rgba(0, 0, 0, 0.6) !important;
}

.v-text-field ::v-deep .v-field__input {
  color: rgba(0, 0, 0, 0.87) !important;
}

.v-text-field ::v-deep .v-label {
  color: rgba(0, 0, 0, 0.6) !important;
}

@media (max-width: 600px) {
  .register-card {
    padding: 1rem;
  }
}
</style>
