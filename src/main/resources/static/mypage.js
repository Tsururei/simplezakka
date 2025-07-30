const API_BASE = '/api';
const token = localStorage.getItem('accessToken');
const isGuest = !token;

document.addEventListener('DOMContentLoaded', async function () {
  await loadUser();
});

async function loadUser() {
    const token = localStorage.getItem('accessToken');
  console.log("accessToken:", token);
  if (!token) return;
  
try{
  const resp = await fetch(`${API_BASE}/user/mypage`,{
    headers: { 'Authorization': `Bearer ${token}` }
  });

  console.log("status:", resp.status);
  if (!resp.ok) return;

  const user = await resp.json();
  console.log("user data:", user);

  document.getElementById('user-name').textContent = user.name;
  document.getElementById('user-email').textContent = user.email;
  document.getElementById('user-address').textContent = user.address;
  document.getElementById('user-password').textContent = user.password;
} catch(e){
  console.error("prefill error:", e);
}
}




// ログアウトボタンのイベント登録
  document.getElementById('logout-btn').addEventListener('click', async function(event) {
  tryLogout();
});

async function tryLogout() {
  const response = await fetch(`${API_BASE}/auth/logout`, {
    method: 'POST',
    credentials: 'include'
  });

  if (response.ok) {
    window.location.href = '/index.html';
  } else {
    alert('ログアウトに失敗しました');
  }
}