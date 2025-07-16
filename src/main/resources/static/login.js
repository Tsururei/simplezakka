document.getElementById('login-btn').addEventListener('click', async function(event) {
  event.preventDefault(); 
  tryLogin();
});


document.getElementById('register-link').addEventListener('click', function() {
    window.location.href = 'register.html';
  });

document.getElementById('guestlogin-btn').addEventListener('click', function() {
    window.location.href = 'home.html';
  });

async function tryLogin() {
  const userEmail = document.getElementById('userEmailInput').value;
  const userPassword = document.getElementById('userPasswordInput').value;

  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userEmail, userPassword })
  });

  if (response.ok) {
    const data = await response.json();
    localStorage.setItem('token', data.token);
    window.location.href = 'home.html';
  } else {
    const err = await response.text();
    alert(err);
  }
};
