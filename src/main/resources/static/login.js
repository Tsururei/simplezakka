const API_URL = 'https://54.248.170.138:8080/tasks';
const API_BASE = '/api';

document.addEventListener('DOMContentLoaded', function () {
  // ✅ チェックボックスによるパスワード表示切り替え
  const passwordInput = document.getElementById('userPasswordInput');
  const showPasswordCheckbox = document.getElementById('show-password');

  if (showPasswordCheckbox && passwordInput) {
    showPasswordCheckbox.addEventListener('change', function () {
      passwordInput.type = this.checked ? 'text' : 'password';
    });
  }

  // ✅ ログインボタン
  document.getElementById('login-btn').addEventListener('click', async function(event) {
    event.preventDefault(); 
    tryLogin();
  });

  // ✅ 新規登録
  document.getElementById('register-link').addEventListener('click', function() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');  
    window.location.href = 'register.html';
  });

  // ✅ ゲストログイン
  document.getElementById('guestlogin-btn').addEventListener('click', function() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');
    window.location.href = 'home.html';
  });
});

async function tryLogin() {
  const userEmail = document.getElementById('userEmailInput').value;
  const userPassword = document.getElementById('userPasswordInput').value;

  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('userId');

  const response = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userEmail, userPassword })
  });

  if (response.ok) {
    const data = await response.json();
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('userId', data.userId);
    window.location.href = 'home.html';
  } else {
    const err = await response.text();
    alert(err);
  }
}
