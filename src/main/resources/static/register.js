document.getElementById('register-btn').addEventListener('click', async function(event) {
  event.preventDefault();
  tryRegisiter();
});

async function tryRegisiter() {
  const registerName = document.getElementById('user_name').value.trim();
  const registerAddress = document.getElementById('user_address').value.trim();
  const registerEmail = document.getElementById('user_email').value.trim();
  const registerPassword = document.getElementById('user_password').value.trim();
  const confirm = document.getElementById('confirm_password').value.trim();

  const errDiv = document.getElementById('login-error');
  errDiv.style.display = 'none';
  errDiv.innerText = '';

  // バリデーション
  if (!registerName || !registerAddress || !registerEmail || !registerPassword || !confirm) {
    errDiv.innerText = 'すべての項目を入力してください。';
    errDiv.style.display = 'block';
    return;
  }
  if (registerPassword.length < 8) {
    errDiv.innerText = 'パスワードは8文字以上にしてください。';
    errDiv.style.display = 'block';
    return;
  }
  if (registerPassword !== confirm) {
    errDiv.innerText = 'パスワードが一致しません。';
    errDiv.style.display = 'block';
    return;
  }

    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        registerName: registerName,
        registerAddress: registerAddress,
        registerEmail: registerEmail,
        registerPassword: registerPassword
    })
    });

    if (response.ok) {
      const data = await response.json();
      // 例：data.token にJWTが入っている想定
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken);  
      localStorage.setItem('userId', data.userId);
      // ホーム画面に遷移
      window.location.href = 'home.html';
    } else {
      const errText = await response.text();
      errDiv.innerText = `登録に失敗しました: ${errText}`;
      errDiv.style.display = 'block';
    }
};
