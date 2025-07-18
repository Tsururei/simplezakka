


const saveBtn = document.getElementById("save-button");
const productList = document.getElementById("product-list");
const modal = document.getElementById("modal");

let products = [];
let categories = [];

document.addEventListener("DOMContentLoaded", fetchProducts);

function renderProducts() {
  productList.innerHTML = "";
  products.forEach((p, i) => {
    const card = document.createElement("div");
    card.className = "product-card";
    card.innerHTML = `
      <input value="${p.categoryName}" placeholder="カテゴリ名" onchange="products[${i}].categoryName = this.value">
      <input value="${p.productId}" placeholder="商品ID" onchange="products[${i}].productId = this.value">
      <img src="${p.image || 'https://via.placeholder.com/150'}" alt="画像">
      <input value="${p.productName}" placeholder="商品名" onchange="products[${i}].productName = this.value">
      <input type="number" value="${p.productPrice}" placeholder="価格" onchange="products[${i}].productPrice = this.value">
      <input type="number" value="${p.stock}" placeholder="在庫数" onchange="products[${i}].stock = this.value">
      <textarea placeholder="商品説明" onchange="products[${i}].description = this.value">${p.description}</textarea>
      <button onclick="deleteProduct(${i})" style="background-color: #cc0000; color: white; padding 0.5em 1em;">削除</button>
    `;
    productList.appendChild(card);
  });
}

function openModal() {
  modal.style.display = "flex";
  renderCategorySelect();//いらないかも
}

function closeModal() {
  modal.style.display = "none";
  document.querySelectorAll("#modal input, #modal textarea").forEach(e => e.value = "");
}

// 個別の商品削除
async function deleteProduct(index) {
  if (confirm("この商品を削除しますか？")) {
    const productToDelete = products[index];
    try {
      const response = await fetch(`/api/admin/products/${productToDelete.productId}`, {
        method: "DELETE"
      });
      if (response.ok) {
        products.splice(index, 1);
        renderProducts();
        alert("商品が削除されました")
      } else {
        alert("削除に失敗しました");
      }
    } catch (error) {
      console.error("削除エラー:", error);
      alert("通信エラーが発生しました");
    }
  }
}

// 新規商品登録
async function registerProduct() {
  const formData = {
    categoryName: document.getElementById("new-category-name").value,
    productName: document.getElementById("new-name").value,
    productPrice: parseInt(document.getElementById("new-price").value, 10),
    description: document.getElementById("new-description").value,
    stock: parseInt(document.getElementById("new-stock").value, 10),
    imageUrl: document.getElementById("new-image-url").value, // 画像アップロード処理
  };

  //バリデーション
  if (!formData.categoryName || !formData.productName || !formData.productPrice || !formData.stock) {
        alert("必須項目（カテゴリ名, 商品名, 価格, 在庫）を入力してください。");
        return;
    }
    if (isNaN(formData.productPrice) || formData.productPrice <= 0) {
        alert("価格は正の数値で入力してください。");
        return;
    }
    if (isNaN(formData.stock) || formData.stock < 0) {
        alert("在庫数は0以上の数値で入力してください。");
        return;
    }

  try {
    const response = await fetch("/api/admin/products", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    });

    if (response.ok) {
      alert("商品が登録されました");
      fetchProducts(); // 一覧を再取得
      closeModal();
    } else {
      alert("登録に失敗しました");
    }
  } catch (error) {
    console.error("登録エラー:", error);
    alert("通信エラーが発生しました");
  }
}

//既存商品更新
async function updateProduct(index) {
  const productToUpdate = products[index];
  const formData = {
    productId: productToUpdate.productId,
    category名: productToUpdate.categoryName,
    productName: productToUpdate.productName,
    productPrice: productToUpdate.productPrice,
    description: productToUpdate.description,
    stock: productToUpdate.stock,
    imageUrl: productToUpdate.imageUrl
  };

  バリデーション
  if (!formData.categoryName || !formData.productName || !formData.productPrice || !formData.stock) {
        alert("更新する商品の必須項目が不足しています。");
        return;
    }

    try {
        const response = await fetch(`/api/admin/products/${formData.productId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                // "Authorization": "Bearer YOUR_JWT_TOKEN" // 認証が必要な場合
            },
            body: JSON.stringify(formData),
        });

        if (response.ok) {
            alert("商品が更新されました。");
            fetchProducts(); // 更新後、一覧を再取得
        } else {
            const errorData = await response.json();
            alert(`更新に失敗しました: ${errorData.message || response.statusText}`);
            console.error("更新エラーレスポンス:", errorData);
        }
    } catch (error) {
        console.error("更新エラー:", error);
        alert("通信エラーが発生しました");
    }
}

//一括保存
saveBtn.addEventListener("click", async () => {
    const productsToSave = products.map(p => ({
        productId: p.productId, 
        productName: p.productName,
        description: p.description,
        stock: parseInt(p.stock, 10), 
        productPrice: parseInt(p.productPrice, 10), 
        imageUrl: p.imageUrl,
        categoryId: p.categoryId
    }));

    //バリデーション
    const invalidProduct = productsToSave.find(p => 
        !p.categoryId || !p.productName || isNaN(p.productPrice) || p.productPrice <= 0 || isNaN(p.stock) || p.stock < 0
    );
    if (invalidProduct) {
        alert("リスト内の商品に未入力または不正な値があります。確認してください。");
        return;
    }

    try {
        const response = await fetch("/api/admin/products/save", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(productsToSave),
        });

        if (response.ok) {
            alert("すべての変更が保存されました！");
            fetchProducts(); 
        } else {
            const errorData = await response.json();
            alert(`保存に失敗しました: ${errorData.message || response.statusText}`);
            console.error("一括保存エラーレスポンス:", errorData);
        }
    } catch (error) {
        console.error("一括保存通信エラー:", error);
        alert("一括保存中に通信エラーが発生しました");
    }
});

//商品一覧を取得
async function fetchProducts() {
    try {
        const response = await fetch("/api/admin/products", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            }
        });

        if (response.ok) {
            products = await response.json(); 
            renderProducts(); 
        } else {
            const errorData = await response.json();
            alert(`商品一覧の取得に失敗しました: ${errorData.message || response.statusText}`);
            console.error("商品一覧取得エラーレスポンス:", errorData);
        }
    } catch (error) {
        console.error("商品一覧取得エラー:", error);
        alert("商品一覧の取得中に通信エラーが発生しました");
    }
}

function renderCategorySelect() {
    const categorySelect = document.getElementById("new-category-name");
    categorySelect.innerHTML = "<option value=''>選択してください</option>"; // デフォルトオプションを追加
    categories.forEach(cat => {
        const option = document.createElement("option");
        option.value = cat.categoryName; // categoryIdを使用
        option.textContent = `${cat.categoryName} - ${cat.categoryName}`; // categoryNameを使用
        categorySelect.appendChild(option);
    });
}

const registerProductButton = document.getElementById("register-product-button");
if (registerProductButton) {
    registerProductButton.addEventListener("click", registerProduct);

}

document.addEventListener("DOMContentLoaded", () => {
const dummyCategories = [
  { id: "1", name: "インテリア" },
  { id: "2", name: "キッチン用品" },
  { id: "3", name: "バスグッズ" }
];

dummyCategories.forEach(category => {
  const option = document.createElement("option");
  option.value = category.id;
  option.textContent = category.name;
  document.getElementById("new-category-select").appendChild(option);
});
});

