const saveBtn = document.getElementById("save-button");
const productList = document.getElementById("product-list");
const modal = document.getElementById("modal");

let products = [
  { categoryId: "A1", productId: "P001", image: "", name: "商品A", price: 1200, desc: "説明A" },
  { categoryId: "B2", productId: "P002", image: "", name: "商品B", price: 2400, desc: "説明B" },
];

function renderProducts() {
  productList.innerHTML = "";
  products.forEach((p, i) => {
    const card = document.createElement("div");
    card.className = "product-card";
    card.innerHTML = `
      <input value="${p.categoryId}" placeholder="カテゴリID" onchange="products[${i}].categoryId = this.value">
      <input value="${p.productId}" placeholder="商品ID" onchange="products[${i}].productId = this.value">
      <img src="${p.image || 'https://via.placeholder.com/150'}" alt="画像">
      <input value="${p.name}" placeholder="商品名" onchange="products[${i}].name = this.value">
      <input type="number" value="${p.price}" placeholder="価格" onchange="products[${i}].price = this.value">
      <textarea placeholder="商品説明" onchange="products[${i}].desc = this.value">${p.desc}</textarea>
    `;
    productList.appendChild(card);
  });
}

function openModal() {
  modal.style.display = "flex";
}

function closeModal() {
  modal.style.display = "none";
  document.querySelectorAll("#modal input, #modal textarea").forEach(e => e.value = "");
}

function registerProduct() {
  const categoryId = document.getElementById("new-category-id").value;
  const productId = document.getElementById("new-product-id").value;
  const name = document.getElementById("new-name").value;
  const price = document.getElementById("new-price").value;
  const desc = document.getElementById("new-desc").value;
  const image = ""; // 実際の画像アップロード処理は別途

  if (categoryId && productId && name && price) {
    products.push({ categoryId, productId, name, price, desc, image });
    renderProducts();
    closeModal();
  } else {
    alert("必須項目を入力してください");
  }
}

saveBtn.addEventListener("click", async () => {
  const dataToSave = products;

  try {
    const response = await fetch("/api/save", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(dataToSave),
    });

    if (response.ok) {
      location.reload(); // 保存成功後にリロード
    } else {
      alert("保存に失敗しました");
    }
  } catch (error) {
    console.error("エラー:", error);
    alert("通信エラーが発生しました");
  }
});

renderProducts();