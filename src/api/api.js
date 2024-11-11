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
