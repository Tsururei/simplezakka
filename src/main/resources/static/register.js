document.getElementById('register-btn').addEventListener('click', async () => {
  const name = document.getElementById('user_name').value.trim();
  const address = document.getElementById('user_address').value.trim();
  const email = document.getElementById('user_email').value.trim();
  const password = document.getElementById('user_password').value.trim();
  const confirm = document.getElementById('confirm_password').value.trim();

  const errDiv = document.getElementById('login-error');
  errDiv.style.display = 'none';
  errDiv.innerText = '';

  // バリデーション
  if (!name || !address || !email || !password || !confirm) {
    errDiv.innerText = 'すべての項目を入力してください。';
    errDiv.style.display = 'block';
    return;
  }
  if (password.length < 8) {
    errDiv.innerText = 'パスワードは8文字以上にしてください。';
    errDiv.style.display = 'block';
    return;
  }
  if (password !== confirm) {
    errDiv.innerText = 'パスワードが一致しません。';
    errDiv.style.display = 'block';
    return;
  }

  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: name,
        address: address,
        email: email,
        password: password
      }),
    });

    if (response.ok) {
      const data = await response.json();
      // 例：data.token にJWTが入っている想定
      localStorage.setItem('token', data.token);
      // ホーム画面に遷移
      window.location.href = '/home.html';
    } else {
      const errText = await response.text();
      errDiv.innerText = `登録に失敗しました: ${errText}`;
      errDiv.style.display = 'block';
    }
  } catch (e) {
    errDiv.innerText = '通信エラーが発生しました。';
    errDiv.style.display = 'block';
    console.error(e);
  }
});
