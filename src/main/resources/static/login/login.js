document.getElementById("login-form").addEventListener("submit", function (e) {
  e.preventDefault(); // ページがリロードしないようにする

  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  if (email === "test@example.com" && password === "password123") {
    localStorage.setItem("isLoggedIn", "true"); // ログイン状態を保存
    window.location.href = "index.html"; // トップページに移動
  } else {
    document.getElementById("error-message").style.display = "block"; // エラー表示
  }
});