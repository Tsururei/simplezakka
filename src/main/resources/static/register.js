const form = document.getElementById("register-form");
const button = document.getElementById("register-btn");

form.addEventListener("input", checkForm);

function checkForm() {
  const isValid = form.checkValidity();
  const password = document.getElementById("user_password").value;
  const confirmPassword = document.getElementById("confirm_password").value;

  const passwordsMatch = (password === confirmPassword);
  button.disabled = !(isValid && passwordsMatch);
}
