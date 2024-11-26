import axios from "axios";
export const createCategory = async (formData) => {
  return await axios.post("http://localhost:8080/category", formData);
};

export const updateCategory = async (formData) => {
  return await axios.put("http://localhost:8080/category", formData);
};

export const deleteCategory = async (id) => {
  return await axios.delete(`http://localhost:8080/category/${id}`);
};

export const getAllAnken = async () => {
  return await axios.get("http://localhost:8080/anken");
};

export const getAllCategoryAccount = async () => {
  return await axios.get("http://localhost:8080/category/account-category");
};

export const getAllCategoryCampaign = async () => {
  return await axios.get("http://localhost:8080/category/campaign-category");
};

export const updateAccountCategoryDetails = async (formData) => {
  return await axios.post("http://localhost:8080/category-binding", formData);
};

export const deleteCategoryBinding = async (formData) => {
  return await axios.post(
    "http://localhost:8080/category-binding/delete",
    formData
  );
};
