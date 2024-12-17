<template>
  <div class="content-wrapper">
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="6" lg="4">
          <v-card class="forgot-password-card">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Forgot Password</v-toolbar-title>
              <v-btn @click="backToLogin" style="font-size: medium">
                Back to Login
              </v-btn>
            </v-toolbar>
            <v-card-text>
              <v-stepper v-model="currentStep" alt-labels>
                <v-stepper-header>
                  <v-stepper-item step="1">Email</v-stepper-item>
                  <v-divider></v-divider>
                  <v-stepper-item step="2">OTP</v-stepper-item>
                  <v-divider></v-divider>
                  <v-stepper-item step="3">Mật khẩu mới</v-stepper-item>
                </v-stepper-header>

                <v-stepper-window>
                  <v-stepper-window-item value="1">
                    <v-form v-model="isEmailValid">
                      <v-text-field
                        v-model="email"
                        :rules="emailRules"
                        label="Email"
                        required
                        prepend-icon="mdi-email"
                      ></v-text-field>
                      <v-btn
                        color="primary"
                        @click="submitEmail"
                        :disabled="!isEmailValid"
                      >
                        Gửi OTP
                      </v-btn>
                    </v-form>
                  </v-stepper-window-item>

                  <v-stepper-window-item value="2">
                    <v-form v-model="isOTPValid">
                      <v-text-field
                        v-model="otp"
                        :rules="otpRules"
                        label="OTP"
                        required
                        prepend-icon="mdi-numeric"
                      ></v-text-field>
                      <v-btn
                        color="primary"
                        @click="submitOTP"
                        :disabled="!isOTPValid"
                      >
                        Xác nhận OTP
                      </v-btn>
                    </v-form>
                  </v-stepper-window-item>

                  <v-stepper-window-item value="3">
                    <v-form v-model="isPasswordValid">
                      <v-text-field
                        v-model="newPassword"
                        :rules="passwordRules"
                        label="Mật khẩu mới"
                        required
                        type="password"
                        prepend-icon="mdi-lock"
                      ></v-text-field>
                      <v-text-field
                        v-model="confirmPassword"
                        :rules="confirmPasswordRules"
                        label="Xác nhận mật khẩu mới"
                        required
                        type="password"
                        prepend-icon="mdi-lock-check"
                      ></v-text-field>
                      <v-btn
                        color="primary"
                        @click="submitNewPassword"
                        :disabled="!isPasswordValid"
                      >
                        Đặt lại mật khẩu
                      </v-btn>
                    </v-form>
                  </v-stepper-window-item>
                </v-stepper-window>
              </v-stepper>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
      <v-snackbar v-model="showMessage" :color="messageColor" timeout="5000">
        {{ message }}
      </v-snackbar>
    </v-container>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { verifyEmail, verifyOtp, changePassword } from "@/api/api";
import router from "@/router";

const currentStep = ref(0);
const email = ref("");
const otp = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

const isEmailValid = ref(false);
const isOTPValid = ref(false);
const isPasswordValid = ref(false);

const showMessage = ref(false);
const message = ref("");
const messageColor = ref("success");

const emailRules = [
  (v) => !!v || "Email là bắt buộc",
  (v) => /.+@.+\..+/.test(v) || "Email phải hợp lệ",
];

const otpRules = [
  (v) => !!v || "OTP là bắt buộc",
  (v) => /^\d{6}$/.test(v) || "OTP phải là 6 chữ số",
];

const passwordRules = [
  (v) => !!v || "Mật khẩu là bắt buộc",
  (v) => v.length >= 8 || "Mật khẩu phải có ít nhất 8 ký tự",
];

const confirmPasswordRules = computed(() => [
  (v) => !!v || "Xác nhận mật khẩu là bắt buộc",
  (v) => v === newPassword.value || "Mật khẩu không khớp",
]);

const submitEmail = async () => {
  if (!isEmailValid.value) return;

  try {
    // TODO: Implement API call to send OTP
    await verifyEmail(email.value); // Simulate API call
    showSuccessMessage("OTP đã được gửi đến email của bạn");
    currentStep.value = 1;
  } catch (error) {
    showErrorMessage("Không thể gửi OTP. Vui lòng thử lại.");
  }
};

const submitOTP = async () => {
  if (!isOTPValid.value) return;

  try {
    // TODO: Implement API call to verify OTP
    await verifyOtp(otp.value, email.value); // Simulate API call
    showSuccessMessage("OTP hợp lệ");
    currentStep.value = 2;
  } catch (error) {
    showErrorMessage("OTP không hợp lệ. Vui lòng thử lại.");
  }
};

const submitNewPassword = async () => {
  if (!isPasswordValid.value) return;

  try {
    // TODO: Implement API call to reset password
    await changePassword(email.value, {
      password: newPassword.value,
      repeatPassword: confirmPassword.value,
      otp: otp.value,
    }); // Simulate API call
    showSuccessMessage("Mật khẩu đã được đặt lại thành công");
    // TODO: Redirect to login page or handle success scenario
    router.push({ name: "Login" });
  } catch (error) {
    showErrorMessage("Không thể đặt lại mật khẩu. Vui lòng thử lại.");
  }
};

const showSuccessMessage = (msg) => {
  message.value = msg;
  messageColor.value = "success";
  showMessage.value = true;
};

const showErrorMessage = (msg) => {
  message.value = msg;
  messageColor.value = "error";
  showMessage.value = true;
};

const backToLogin = () => {
  router.push({ name: "Login" });
};
</script>

<style scoped>
.content-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(1px);
  z-index: 1;
}

.forgot-password-card {
  border-radius: 16px !important;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1) !important;
}
</style>
