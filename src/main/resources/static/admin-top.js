const API_BASE = '/api';

document.getElementById('logout-btn').addEventListener('click', async function () {
  tryLogout();
});

async function tryLogout() {
  const response = await fetch('http://localhost:8080/admin/auth/logout', {
    method: 'POST',
    credentials: 'include'  
  });

  if (response.ok) {
    window.location.href = 'http://localhost:8080/admin-login.html';
  } else {
    alert('ログアウトに失敗しました');
  }
}