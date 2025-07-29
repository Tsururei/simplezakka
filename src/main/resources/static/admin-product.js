// admin-product.js

async function fetchCategories() {
  try {
    const res = await fetch("/api/admin/categories");
    if (!res.ok) throw new Error("カテゴリ一覧の取得に失敗しました");
    categories = await res.json();
    populateCategorySelect();
  } catch (err) {
    showMessage(err.message, "error");
  }
}

let products = [];
let editingProductId = null;

// DOMContentLoaded

document.addEventListener("DOMContentLoaded", () => {
  const imageFileInput = document.getElementById("new-image-file");
  const imageUrlInput = document.getElementById("new-image-url");

  if (imageFileInput) {
    imageFileInput.addEventListener("change", () => {
      const file = imageFileInput.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          const preview = document.getElementById("image-preview");
          if (preview) preview.src = e.target.result;
        };
        reader.readAsDataURL(file);
        if (imageUrlInput) imageUrlInput.value = "";
      }
    });
  }

  if (imageUrlInput) {
    imageUrlInput.addEventListener("input", () => {
      const preview = document.getElementById("image-preview");
      if (preview) preview.src = imageUrlInput.value;
      const fileInput = document.getElementById("new-image-file");
      if (fileInput) fileInput.value = "";
    });
  }

 
  fetchCategories();
  fetchProducts();

  document.getElementById("register-product-button")?.addEventListener("click", onSaveProduct);
  document.querySelector(".add-button")?.addEventListener("click", () => openModal());
  document.getElementById("save-button")?.addEventListener("click", () => {
    alert("一覧の一括保存機能が未実装です。");
  });
});

function populateCategorySelect() {
  const select = document.getElementById("new-category-select");
  if (!select) return;
  select.innerHTML = '<option value="">-- カテゴリを選択 --</option>';
  categories.forEach(cat => {
    const option = document.createElement("option");
    option.value = cat.categoryId;
    option.textContent = cat.categoryName;
    select.appendChild(option);
  });
}


async function fetchProducts() {
  showLoading(true);
  clearMessage();

  try {
    const response = await fetch("/api/admin/products");
    if (!response.ok) throw new Error("商品一覧の取得に失敗しました");
    products = await response.json();
    renderProductList();
  } catch (err) {
    showMessage(err.message, "error");
  } finally {
    showLoading(false);
  }
}

function renderProductList() {
  const list = document.getElementById("product-list");
  if (!list) return;
  list.innerHTML = "";

  products.forEach(product => {
    const imgHtml = product.imageUrl ? `<img src="${product.imageUrl}" alt="商品画像">` : "";

    const card = document.createElement("div");
    card.className = "product-card";

    card.innerHTML = `
      ${imgHtml}
      <label>商品名</label><div>${escapeHtml(product.productName)}</div>
      <label>価格</label><div>${product.productPrice} 円</div>
      <label>在庫数</label><div>${product.stock}</div>
      <label>カテゴリ</label><div>${getCategoryName(product.categoryId)}</div>
      <label>説明</label><div>${escapeHtml(product.description || "")}</div>
      <button class="delete-btn" onclick="deleteProduct(${product.productId})">削除</button>
      <button class="edit-btn" onclick="openModal(${product.productId})">編集</button>
    `;

    list.appendChild(card);
  });
}

function getCategoryName(categoryId) {
  const cat = categories.find(c => c.categoryId === categoryId);
  return cat ? cat.categoryName : "未設定";
}

function openModal(productId = null) {
  clearMessage();
  const modal = document.getElementById("modal");
  if (!modal) return;
  const header = modal.querySelector(".modal-header h3");
  const select = document.getElementById("new-category-select");
  const nameInput = document.getElementById("new-product-name");
  const priceInput = document.getElementById("new-product-price");
  const stockInput = document.getElementById("new-stock");
  const descInput = document.getElementById("new-description");
  const imgUrlInput = document.getElementById("new-image-url");
  const fileInput = document.getElementById("new-image-file");
  const preview = document.getElementById("image-preview");

  if (!select || !nameInput || !priceInput || !stockInput || !descInput || !imgUrlInput || !header) return;

  if (productId !== null) {
    editingProductId = Number(productId);
    header.textContent = "商品情報編集";
    const product = products.find(p => Number(p.productId) === editingProductId);
    if (!product) {
      alert("商品が見つかりません");
      editingProductId = null;
      return;
    }
    select.value = product.categoryId || "";
    nameInput.value = product.productName || "";
    priceInput.value = product.productPrice || "";
    stockInput.value = product.stock || "";
    descInput.value = product.description || "";
    imgUrlInput.value = product.imageUrl || "";
    if (preview) preview.src = product.imageUrl || "";
    if (fileInput) fileInput.value = "";
  } else {
    editingProductId = null;
    header.textContent = "新規商品登録";
    select.value = "";
    nameInput.value = "";
    priceInput.value = "";
    stockInput.value = "";
    descInput.value = "";
    imgUrlInput.value = "";
    if (preview) preview.src = "";
    if (fileInput) fileInput.value = "";
  }
  modal.style.display = "flex";
}

function closeModal() {
  const modal = document.getElementById("modal");
  if (modal) modal.style.display = "none";
  clearMessage();
}

async function onSaveProduct() {
  clearMessage();

  const categoryId = document.getElementById("new-category-select")?.value;
  const productName = document.getElementById("new-product-name")?.value.trim();
  const productPrice = parseInt(document.getElementById("new-product-price")?.value, 10);
  const stock = parseInt(document.getElementById("new-stock")?.value, 10);
  const description = document.getElementById("new-description")?.value.trim();
  const imageFile = document.getElementById("new-image-file")?.files[0];
  const imageUrl = document.getElementById("new-image-url")?.value.trim() || "";

  if (!categoryId || !productName || isNaN(productPrice) || productPrice <= 0 || isNaN(stock) || stock < 0) {
    showMessage("必須項目を正しく入力してください（カテゴリ、商品名、価格、在庫）", "error");
    return;
  }

const formJson = {
  categoryId,
  productName,
  productPrice,
  stock,
  description,
  imageUrl// 必要に応じて
};

const formData = new FormData();
formData.append("form", new Blob([JSON.stringify(formJson)], { type: "application/json" }));
if (imageFile) {
  formData.append("imageFile", imageFile);
}

  try {
    const url = editingProductId ? `/api/admin/products/${editingProductId}` : "/api/admin/products";
    const method = editingProductId ? "PUT" : "POST";

    const response = await fetch(url, {
      method,
      body: formData,  // Content-Typeは自動セットされる
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "保存に失敗しました");
    }

    alert(editingProductId ? "商品を更新しました" : "商品を登録しました");
    closeModal();
    fetchProducts();
  } catch (error) {
    showMessage(error.message, "error");
  }
}

async function deleteProduct(productId) {
  if (!confirm("本当にこの商品を削除しますか？")) return;
  clearMessage();
  showLoading(true);
  try {
    const response = await fetch(`/api/admin/products/${productId}`, {
      method: "DELETE"
    });
    if (!response.ok) throw new Error("削除に失敗しました");
    alert("商品を削除しました");
    fetchProducts();
  } catch (error) {
    showMessage(error.message, "error");
  } finally {
    showLoading(false);
  }
}

function showMessage(msg, type = "success") {
  const area = document.getElementById("message-area");
  if (!area) return;
  area.textContent = msg;
  area.className = type === "error" ? "error" : "success";
}

function clearMessage() {
  const area = document.getElementById("message-area");
  if (!area) return;
  area.textContent = "";
  area.className = "";
}

function showLoading(isLoading) {
  const loading = document.getElementById("loading");
  if (loading) loading.style.display = isLoading ? "block" : "none";
}

function escapeHtml(text) {
  if (!text) return "";
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

window.openModal = openModal;
window.closeModal = closeModal;
window.deleteProduct = deleteProduct;
