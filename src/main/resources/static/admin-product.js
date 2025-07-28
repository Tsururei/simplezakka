const categories = [
  { id: "cate002", name: "インテリア" },
  { id: "cate001", name: "キッチン用品" },
];

let products = [];
let editingProductId = null;

// 初期処理
document.addEventListener("DOMContentLoaded", () => {
  populateCategorySelect();
  fetchProducts();

  // モーダル登録ボタン
  document.getElementById("register-product-button").addEventListener("click", onSaveProduct);

  // 「＋ 新規商品情報登録」ボタン
  document.querySelector(".add-button").addEventListener("click", () => openModal());
});

// カテゴリプルダウン初期化
function populateCategorySelect() {
  const select = document.getElementById("new-category-select");
  select.innerHTML = '<option value="">-- カテゴリを選択 --</option>';
  categories.forEach(cat => {
    const option = document.createElement("option");
    option.value = cat.id;
    option.textContent = cat.name;
    select.appendChild(option);
  });
}

// 商品一覧取得して表示
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

// 商品一覧描画
function renderProductList() {
  const list = document.getElementById("product-list");
  list.innerHTML = "";

  products.forEach(product => {
    const imgHtml = product.imageUrl
      ? `<img src="${product.imageUrl}" alt="商品画像">`
      : ''; // 画像がなければ空文字（必要なら代替画像パスを入れてもOK）

    const card = document.createElement("div");
    card.className = "product-card";

    card.innerHTML = `
      ${imgHtml}
      <label>商品名</label><div>${escapeHtml(product.productName)}</div>
      <label>価格</label><div>${product.productPrice} 円</div>
      <label>在庫数</label><div>${product.stock}</div>
      <label>カテゴリ</label><div>${getCategoryName(product.categoryId)}</div>
      <label>説明</label><div>${escapeHtml(product.description || "")}</div>
      <button class="delete-btn" onclick="deleteProduct('${product.productId}')">削除</button>
      <button class="edit-btn" onclick="openModal('${product.productId}')">編集</button>
    `;

    list.appendChild(card);
  });
}

// カテゴリ名取得（IDから）
function getCategoryName(categoryId) {
  const cat = categories.find(c => c.id === categoryId);
  return cat ? cat.name : "未設定";
}

// モーダルを開く（productIdがあれば編集モード）
function openModal(productId = null) {
  console.log("openModal called with productId:", productId);
  clearMessage();

  const modal = document.getElementById("modal");
  const header = modal.querySelector(".modal-header h3");
  const select = document.getElementById("new-category-select");
  const nameInput = document.getElementById("new-product-name");
  const priceInput = document.getElementById("new-product-price");
  const stockInput = document.getElementById("new-stock");
  const descInput = document.getElementById("new-description");
  const imgInput = document.getElementById("new-image-url");

  if (productId) {
    // 編集モード
    editingProductId = productId;
    header.textContent = "商品情報編集";

    const product = products.find(p => p.productId === productId);
    if (!product) {
      alert("商品が見つかりません");
      return;
    }

    select.value = product.categoryId || "";
    nameInput.value = product.productName || "";
    priceInput.value = product.productPrice || "";
    stockInput.value = product.stock || "";
    descInput.value = product.description || "";
    imgInput.value = product.imageUrl || "";

  } else {
    // 新規登録モード
    editingProductId = null;
    header.textContent = "新規商品登録";

    select.value = "";
    nameInput.value = "";
    priceInput.value = "";
    stockInput.value = "";
    descInput.value = "";
    imgInput.value = "";
  }

  modal.style.display = "flex";
}

// モーダルを閉じる
function closeModal() {
  document.getElementById("modal").style.display = "none";
  clearMessage();
}

// 保存ボタン押下時処理
async function onSaveProduct() {
  clearMessage();

  const categoryId = document.getElementById("new-category-select").value;
  const productName = document.getElementById("new-product-name").value.trim();
  const productPrice = parseInt(document.getElementById("new-product-price").value, 10);
  const stock = parseInt(document.getElementById("new-stock").value, 10);
  const description = document.getElementById("new-description").value.trim();
  const imageUrl = document.getElementById("new-image-url").value.trim();

  // バリデーション
  if (!categoryId || !productName || isNaN(productPrice) || productPrice <= 0 || isNaN(stock) || stock < 0) {
    showMessage("必須項目を正しく入力してください（カテゴリ、商品名、価格、在庫）", "error");
    return;
  }

  const payload = {
    categoryId,
    productName,
    productPrice,
    stock,
    description,
    imageUrl
  };

  try {
    let response;
    if (editingProductId) {
      // 編集PUT
      response = await fetch(`/api/admin/products/${editingProductId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
    } else {
      // 新規POST
      response = await fetch("/api/admin/products", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
    }

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

// 商品削除
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

// メッセージ表示 helper
function showMessage(msg, type = "success") {
  const area = document.getElementById("message-area");
  area.textContent = msg;
  area.className = type === "error" ? "error" : "success";
}

// メッセージクリア helper
function clearMessage() {
  const messageArea = document.getElementById("message-area");
  if (messageArea) {
    messageArea.textContent = "";
    messageArea.className = "";
  }
}

// ローディング表示 helper
function showLoading(isLoading) {
  document.getElementById("loading").style.display = isLoading ? "block" : "none";
}

// HTML特殊文字エスケープ（XSS対策に推奨）
function escapeHtml(text) {
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

window.openModal = openModal;
window.closeModal = closeModal;
