import axiosInstance from "./axios";

// Category
export const createCategory = async (formData) => {
  return await axiosInstance.post("/category", formData);
};

export const updateCategory = async (formData) => {
  return await axiosInstance.put("/category", formData);
};

export const getCategory = async () => {
  return await axiosInstance.get("/category");
};

export const deleteCategory = async (id) => {
  return await axiosInstance.delete(`/category/${id}`);
};

export const getAllCategoryAccount = async () => {
  return await axiosInstance.get("/category/account-category");
};

export const getAllCategoryCampaign = async () => {
  return await axiosInstance.get("/category/campaign-category");
};

// Category Binding
export const updateCategoryBindingDetails = async (formData) => {
  return await axiosInstance.post("/category-binding", formData);
};

export const deleteCategoryBinding = async (formData) => {
  return await axiosInstance.post("/category-binding/delete", formData);
};

// Excel
export const previewFileExcel = async (formData) => {
  return await axiosInstance.post("/excel/preview", formData);
};

export const confirmFileExcel = async (formData) => {
  return await axiosInstance.post("/excel/read-excel", formData);
};

export const exportFileExcel = async () => {
  return await axiosInstance.get("/excel/export", { responseType: "blob" });
};

export const exportFileExcelCustom = async (formData) => {
  return await axiosInstance.post("/excel/export", formData, {
    responseType: "blob",
  });
};

// Anken
export const getAnkenByUser = async () => {
  return await axiosInstance.get("/anken");
};

export const getAllAnken = async () => {
  return await axiosInstance.get("/all-anken");
};

// File
export const getUploadFile = async () => {
  return await axiosInstance.get("/fileUpload");
};

export const downloadFileUpload = async (fileCode) => {
  return await axiosInstance.get(fileCode, {
    responseType: "blob",
  });
};

// User
export const getAllUsers = async () => {
  return await axiosInstance.get("/user");
};

export const updateUser = async (formData, id) => {
  return await axiosInstance.put(`/user/${id}`, formData);
};

export const getCurrentUser = async () => {
  return await axiosInstance.get("/current-user");
};

// Forget Password
export const verifyEmail = async (email) => {
  return await axiosInstance.post(`/forgotPassword/verifyMail/${email}`);
};

export const verifyOtp = async (otp, email) => {
  return await axiosInstance.post(`/forgotPassword/verifyOtp/${otp}/${email}`);
};

export const changePassword = async (email, formData) => {
  return await axiosInstance.post(
    `/forgotPassword/changePassword/${email}`,
    formData
  );
};
