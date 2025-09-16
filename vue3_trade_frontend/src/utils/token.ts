// 封装本地存储数据与读取数据的方法

export const SET_TOKEN = (token: string) => {
  localStorage.setItem("TOKEN", token);
};
export const GET_TOKEN = () => {
  return localStorage.getItem("TOKEN");
};
export const REMOVE_TOKEN = () => {
  return localStorage.removeItem("TOKEN");
};
export const SET_ID = (id: string) => {
  localStorage.setItem("id", id);
};
export const GET_ID = () => {
  return localStorage.getItem("id");
};
export const SET_ROLE = (role: string) => {
  localStorage.setItem("role", role);
};
export const GET_ROLE = () => {
  return localStorage.getItem("role");
};
export const SET_AVATAR = (avatar: string) => {
  localStorage.setItem("avatar", avatar);
};
export const GET_AVATAR = () => {
  return localStorage.getItem("avatar") ?? "";
};
export const SET_USER_NAME = (userName: string) => {
  localStorage.setItem("userName", userName);
};
export const GET_USER_NAME = () => {
  return localStorage.getItem("userName") ?? "";
};
export const SET_SELL_PERMISSION = (permission: number) => {
  localStorage.setItem("sellPermission", String(permission));
};
export const GET_SELL_PERMISSION = () => {
  const value = localStorage.getItem("sellPermission");
  return value === null ? null : Number(value);
};
export const SET_RENT_PERMISSION = (permission: number) => {
  localStorage.setItem("rentPermission", String(permission));
};
export const GET_RENT_PERMISSION = () => {
  const value = localStorage.getItem("rentPermission");
  return value === null ? null : Number(value);
};
export const SET_SELL_APPLY_STATUS = (status: number) => {
  localStorage.setItem("sellApplyStatus", String(status));
};
export const GET_SELL_APPLY_STATUS = () => {
  const value = localStorage.getItem("sellApplyStatus");
  return value === null ? null : Number(value);
};
export const SET_RENT_APPLY_STATUS = (status: number) => {
  localStorage.setItem("rentApplyStatus", String(status));
};
export const GET_RENT_APPLY_STATUS = () => {
  const value = localStorage.getItem("rentApplyStatus");
  return value === null ? null : Number(value);
};
