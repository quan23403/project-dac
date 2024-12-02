import axios from "axios";

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

export default axiosInstance;
