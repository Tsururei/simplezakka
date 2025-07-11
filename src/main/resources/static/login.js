const API_BASE = '/api';

document.getElementById('login-btn').addEventListener('click', async function() {
      tryLogin();
  });

document.getElementById('register-link').addEventListener('click', function() {
    window.location.href = 'register.html';
  });

document.getElementById('guestlogin-btn').addEventListener('click', function() {
    window.location.href = 'home.html';
  });

async function tryLogin() {
      const response = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        loginName: loginName,
        loginPassword: loginPassword
    })
  })
};