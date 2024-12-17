import axios from "axios";
import router from "@/router";
const axiosInstance = axios.create({
  baseURL: "http://localhost:8080", // Your backend base URL
  timeout: 10000, // Optional timeout setting
});

// Add a request interceptor to attach JWT token to the Authorization header globally
axiosInstance.interceptors.request.use(
  (config) => {
    // Check if there's a JWT token in localStorage or Vuex store
    const token = localStorage.getItem("token"); // Or use Vuex if you store token there
    if (token) {
      // console.log("Adding token to request header:", token); // Log to confirm the token is added
      config.headers["Authorization"] = `Bearer ${token}`; // Add token to Authorization header
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle 401 error and redirect to login
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear the token from localStorage or Vuex if necessary
      localStorage.removeItem("token"); // Remove the token if expired or invalid

      // Redirect to login page
      router.push({ name: "Login" }); // Replace 'login' with your actual route name for login

      // Optionally, you can also log the user out in Vuex or trigger a global logout action
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
