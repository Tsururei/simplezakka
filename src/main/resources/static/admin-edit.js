const admins = [
  {
    name: "つるれい",
    email: "tsururei@example.com",
    password: "******",
    registered: "2025-07-14",
  },
];

const tbody = document.querySelector("#admin-edit-table tbody");


let deleteTargetIndex = null;
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

function displayAdmins() {
  tbody.innerHTML = "";
  admins.forEach((admin, index) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${admin.name}</td>
      <td>${admin.email}</td>
      <td>${admin.password}</td>
      <td>${admin.registered}</td>
      <td><button data-index="${index}" class="delete-btn">削除</button></td>
    `;
    tbody.appendChild(tr);
  });
}


tbody.addEventListener("click", e => {
  if (e.target.classList.contains("delete-btn")) {
    deleteTargetIndex = e.target.getAttribute("data-index");
    deleteModal.style.display = "flex";
  }
});


confirmBtn.addEventListener("click", () => {
  if (deleteTargetIndex !== null) {
    admins.splice(deleteTargetIndex, 1);
    displayAdmins();
    deleteTargetIndex = null;
    deleteModal.style.display = "none";
  }
});


cancelBtn.addEventListener("click", () => {
  deleteTargetIndex = null;
  deleteModal.style.display = "none";
});


registerBtn.addEventListener("click", () => {
  newName.value = "";
  newEmail.value = "";
  newPassword.value = "";
  registerModal.style.display = "flex";
});


registerConfirmBtn.addEventListener("click", () => {
  const name = newName.value.trim();
  const email = newEmail.value.trim();
  const password = newPassword.value.trim();

  if (name && email && password) {
    const now = new Date();
    const dateStr = now.toISOString().split("T")[0];

    admins.push({
      name,
      email,
      password: "******",
      registered: dateStr
    });

    displayAdmins();
    registerModal.style.display = "none";
  } else {
    alert("すべての項目を入力してください。");
  }
});

registerCancelBtn.addEventListener("click", () => {
  registerModal.style.display = "none";
});

window.addEventListener("click", e => {
  if (e.target === deleteModal) {
    deleteModal.style.display = "none";
  }
  if (e.target === registerModal) {
    registerModal.style.display = "none";
  }
});

displayAdmins();
    
    

