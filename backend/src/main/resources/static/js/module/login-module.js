export function isLogin() {
  return localStorage.getItem('token') !== null;
}
