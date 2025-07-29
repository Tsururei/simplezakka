const tbody = document.querySelector("#admin-category-table tbody");

const deleteModal = document.getElementById("delete-confirm-modal");
const confirmBtn = document.getElementById("confirm-delete-btn");
const cancelBtn = document.getElementById("cancel-delete-btn");

const registerModal = document.getElementById("register-modal");
const registerBtn = document.getElementById("administrator-registration-button");
const registerConfirmBtn = document.getElementById("register-confirm-btn");
const registerCancelBtn = document.getElementById("register-cancel-btn");

const newName = document.getElementById("new-categoryname");
const newId = document.getElementById("new-categoryid");

let deleteTargetId = null;

async function fetchCategories() {
  try {
    const res = await fetch("http://localhost:8080/api/admin/categories"); 
    if (!res.ok) throw new Error("管理者一覧の取得に失敗しました");
    const categories = await res.json();
    displayCategories(categories);
  } catch (error) {
    alert(error.message);
  }
}

function displayCategories(categories) {
  tbody.innerHTML = "";
  categories.forEach(category => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${category.categoryName}</td>
      <td>${category.categoryId}</td>
      <td><button data-id="${category.categoryId}" class="delete-btn">削除</button></td>
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
  try {
    const res = await fetch(`http://localhost:8080/api/admin/categories/${deleteTargetId}`, {
      method: "DELETE",
    });
    if (!res.ok) throw new Error("削除に失敗しました");
    deleteTargetId = null;
    deleteModal.style.display = "none";
    fetchCategories();
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
  newId.value = "";
  registerModal.style.display = "flex";
});


registerConfirmBtn.addEventListener("click", async () => {
  const name = newName.value.trim();
  const id = newId.value.trim();

  if (!name || !id) {
    alert("すべての項目を入力してください。");
    return;
  }

  try {
    const res = await fetch("http://localhost:8080/api/admin/categories", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        categoryName: name,
        categoryId: id,
      }),
    });

    if (!res.ok) throw new Error("登録に失敗しました");

    registerModal.style.display = "none";
    fetchCategories();
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

fetchCategories();
