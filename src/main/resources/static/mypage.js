  // ログアウトボタンのイベント登録
  document.getElementById('logout-btn').addEventListener('click', async function(event) {
  tryLogout();
});

async function tryLogout() {
  const response = await fetch('http://localhost:8080/api/auth/logout', {
    method: 'POST',
    credentials: 'include'
  });

  if (response.ok) {
    window.location.href = '/index.html';
  } else {
    alert('ログアウトに失敗しました');
  }
}