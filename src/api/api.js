import axios from "axios";

axios.defaults.baseURL = "http://localhost:8080";

export const createCategory = async (formData) => {
  return await axios.post("/category", formData);
};

export const updateCategory = async (formData) => {
  return await axios.put("/category", formData);
};

export const deleteCategory = async (id) => {
  return await axios.delete(`/category/${id}`);
};

export const getAllAnken = async () => {
  return await axios.get("/anken");
};

export const getAllCategoryAccount = async () => {
  return await axios.get("/category/account-category");
};

export const getAllCategoryCampaign = async () => {
  return await axios.get("/category/campaign-category");
};

export const updateAccountCategoryDetails = async (formData) => {
  return await axios.post("/category-binding", formData);
};

export const deleteCategoryBinding = async (formData) => {
  return await axios.post("/category-binding/delete", formData);
};

export const previewFileExcel = async (formData) => {
  return await axios.post("/excel/preview", formData);
};
