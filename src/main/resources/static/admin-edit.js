const API_BASE = '/api';

const tbody = document.querySelector("#admin-edit-table tbody");

const deleteModal = document.getElementById("delete-confirm-modal");
const confirmBtn = document.getElementById("confirm-delete-btn");
const cancelBtn = document.getElementById("cancel-delete-btn");

const registerModal = document.getElementById("register-modal");
const registerBtn = document.getElementById("administrator-registration-button");
const registerConfirmBtn = document.getElementById("register-confirm-btn");
const registerCancelBtn = document.getElementById("register-cancel-btn");

const newName = document.getElementById("new-name");
const newEmail = document.getElementById("new-email");
const newPassword = document.getElementById("new-password");

let deleteTargetId = null;

 const passwordInput = document.getElementById('new-password');
  const showPasswordCheckbox = document.getElementById('show-password');

  // チェックボックスが存在すればイベント追加
  if (showPasswordCheckbox) {
    showPasswordCheckbox.addEventListener('change', function () {
      if (this.checked) {
        passwordInput.type = 'text';
      } else {
        passwordInput.type = 'password';
      }
    });
  }

async function fetchAdmins() {
  try {
    const res = await fetch("http://localhost:8080/api/admins"); 
    if (!res.ok) throw new Error("管理者一覧の取得に失敗しました");
    const admins = await res.json();
    displayAdmins(admins);
  } catch (error) {
    alert(error.message);
  }
}

function displayAdmins(admins) {
  tbody.innerHTML = "";
  admins.forEach(admin => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${admin.adminName}</td>
      <td>${admin.adminEmail}</td>
      <td>******</td>
      <td>${new Date(admin.adminDate).toLocaleString()}</td>
      <td><button data-id="${admin.adminId}" class="delete-btn">削除</button></td>
    `;
    tbody.appendChild(tr);
  });
}

tbody.addEventListener("click", e => {
  if (e.target.classList.contains("delete-btn")) {
    deleteTargetId = e.target.getAttribute("data-id");
    deleteModal.style.display = "flex";
  }
});

confirmBtn.addEventListener("click", async () => {
  if (!deleteTargetId) return;

  const resAdmins = await fetch("http://localhost:8080/api/admins");
  const admins = await resAdmins.json();
  if (admins.length <= 1) {
    alert("少なくとも1人の管理者が必要です。削除できません。");
    deleteModal.style.display = "none";
    deleteTargetId = null;
    return;
  }
  
  try {
    const res = await fetch(`http://localhost:8080/api/admins/${deleteTargetId}`, {
      method: "DELETE",
    });
    if (!res.ok) throw new Error("削除に失敗しました");
    deleteTargetId = null;
    deleteModal.style.display = "none";
    fetchAdmins();
  } catch (error) {
    alert(error.message);
  }
});


cancelBtn.addEventListener("click", () => {
  deleteTargetId = null;
  deleteModal.style.display = "none";
});


registerBtn.addEventListener("click", () => {
  newName.value = "";
  newEmail.value = "";
  newPassword.value = "";
  registerModal.style.display = "flex";
});


registerConfirmBtn.addEventListener("click", async () => {
  const name = newName.value.trim();
  const email = newEmail.value.trim();
  const password = newPassword.value.trim();

  if (!name || !email || !password) {
    alert("すべての項目を入力してください。");
    return;
  }

  try {
    const res = await fetch("http://localhost:8080/api/admins", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        adminName: name,
        adminEmail: email,
        adminPassword: password,
      }),
    });

    if (!res.ok) throw new Error("登録に失敗しました");

    registerModal.style.display = "none";
    fetchAdmins();
  } catch (error) {
    alert(error.message);
  }
});


registerCancelBtn.addEventListener("click", () => {
  registerModal.style.display = "none";
});


window.addEventListener("click", e => {
  if (e.target === deleteModal) {
    deleteModal.style.display = "none";
    deleteTargetId = null;
  }
  if (e.target === registerModal) {
    registerModal.style.display = "none";
  }
});

fetchAdmins();
