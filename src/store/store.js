import { createStore } from "vuex";
import { jwtDecode } from "jwt-decode";
// Create a new store instance.
const store = createStore({
  state: {
    token: localStorage.getItem("token") || "",
    email: null,
    role: null,
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token;
      localStorage.setItem("token", token);
      // Giải mã JWT để lấy thông tin người dùng và quyền
      const decoded = jwtDecode(token);
      console.log(decoded);
      state.email = decoded.user.email;
      state.role = decoded.scope;
    },
    LOGOUT(state) {
      state.token = "";
      state.email = null;
      state.role = null;
      localStorage.removeItem("token");
    },
  },
  actions: {
    login({ commit }, token) {
      commit("SET_TOKEN", token);
    },
    logout({ commit }) {
      commit("LOGOUT");
    },
  },
  getters: {
    email: (state) => state.email,
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.role === "ADMIN",
    isUser: (state) => state.role === "USER",
  },
});

export default store;
