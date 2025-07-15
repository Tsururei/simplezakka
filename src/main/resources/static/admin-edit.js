const admins = [
  {
    name: "つるれい",
    email: "tsururei@example.com",
    password: "******",
    registered: "2025-07-14",
  },
];

const tbody = document.querySelector("#admin-edit-table tbody");

// 削除確認モーダル用の要素
let deleteTargetIndex = null;
const deleteModal = document.getElementById("delete-confirm-modal");
const confirmBtn = document.getElementById("confirm-delete-btn");
const cancelBtn = document.getElementById("cancel-delete-btn");

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

window.addEventListener("click", e => {
  if (e.target === deleteModal) {
    deleteModal.style.display = "none";
  }
});

displayAdmins();

    
    

