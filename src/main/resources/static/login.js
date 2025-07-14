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
  const loginName = document.getElementById('loginNameInput').value;
  const loginPassword = document.getElementById('loginPasswordInput').value;

  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ loginName, loginPassword })
  });

  if (response.ok) {
    const data = await response.json();
    window.location.href = '/home.html';
  } else {
    const err = await response.text();
    alert(err);
  }
};
