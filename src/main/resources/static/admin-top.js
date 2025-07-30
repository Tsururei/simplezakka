const API_BASE = '/api';

document.getElementById('logout-btn').addEventListener('click', async function () {
  tryLogout();
});

async function tryLogout() {
  const response = await fetch(`${API_BASE}/admin/auth/logout`, {
    method: 'POST',
    credentials: 'include'  
  });

  if (response.ok) {
    window.location.href = 'admin-login.html';
  } else {
    alert('ログアウトに失敗しました');
  }
}