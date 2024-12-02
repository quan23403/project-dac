import axiosInstance from "./axios";
import axios from "axios";
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

export const getAllAnken = async () => {
  return await axiosInstance.get("/anken");
};

export const getAllCategoryAccount = async () => {
  return await axiosInstance.get("/category/account-category");
};

export const getAllCategoryCampaign = async () => {
  return await axiosInstance.get("/category/campaign-category");
};

export const updateAccountCategoryDetails = async (formData) => {
  return await axiosInstance.post("/category-binding", formData);
};

export const deleteCategoryBinding = async (formData) => {
  return await axiosInstance.post("/category-binding/delete", formData);
};

export const previewFileExcel = async (formData) => {
  return await axiosInstance.post("/excel/preview", formData);
};

export const confirmFileExcel = async (formData) => {
  return await axiosInstance.post("/excel/read-excel", formData);
};

export const exportFileExcel = async () => {
  return await axiosInstance.get("/excel/export", { responseType: "blob" });
};

export const getAnkenByUser = async () => {
  return await axiosInstance.get("/anken");
};
