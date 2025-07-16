document.addEventListener('DOMContentLoaded', function () {
  document.getElementById('login-form').addEventListener('submit', async function(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const params = new URLSearchParams();
    params.append('email', email);
    params.append('password', password);

    try {
      const response = await fetch('http://localhost:8080/admin/auth/login', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
      });

      if (response.ok) {
        window.location.href = 'http://localhost:8080/admin-top.html';
      } else {
        alert('ログインに失敗しました');
      }
    } catch (error) {
      alert('通信エラーが発生しました');
    }
  });
});