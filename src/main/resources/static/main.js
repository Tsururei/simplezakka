// グローバル変数宣言
let productModal, cartModal, checkoutModal, orderCompleteModal;
let allProductsContainer, kitchenContainer, interiorContainer;
let cartItems = [];

const API_BASE = '/api';

document.addEventListener('DOMContentLoaded', function () {
  loadCartItems();

  // タブ切り替え処理
  document.getElementById('kitchen-tab').addEventListener('click', function () {
    document.querySelectorAll('.tab-pane').forEach(pane => pane.classList.remove('show', 'active'));
    document.getElementById('kitchen').classList.add('show', 'active');
    document.getElementById('interior-tab').classList.remove('active');
    this.classList.add('active');
    allProductsContainer.style.display = 'none';
    kitchenContainer.style.display = 'block';
    interiorContainer.style.display = 'none';
  });

  document.getElementById('interior-tab').addEventListener('click', function () {
    document.querySelectorAll('.tab-pane').forEach(pane => pane.classList.remove('show', 'active'));
    document.getElementById('interior').classList.add('show', 'active');
    document.getElementById('kitchen-tab').classList.remove('active');
    this.classList.add('active');
    allProductsContainer.style.display = 'none';
    kitchenContainer.style.display = 'none';
    interiorContainer.style.display = 'block';
  });

  // モーダル初期化
  productModal = new bootstrap.Modal(document.getElementById('productModal'));
  cartModal = new bootstrap.Modal(document.getElementById('cartModal'));
  checkoutModal = new bootstrap.Modal(document.getElementById('checkoutModal'));
  orderCompleteModal = new bootstrap.Modal(document.getElementById('orderCompleteModal'));

  allProductsContainer = document.getElementById('all-products');
  kitchenContainer = document.querySelector('.kitchen-products').parentElement;
  interiorContainer = document.querySelector('.interior-products').parentElement;

  allProductsContainer.style.display = 'flex';
  kitchenContainer.style.display = 'none';
  interiorContainer.style.display = 'none';

  fetchProducts();

  document.getElementById('logout-btn').addEventListener('click', async function () {
    const response = await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
    if (response.ok) {
      window.location.href = '/index.html';
    } else {
      alert('ログアウトに失敗しました');
    }
  });

  updateCartDisplay();

  document.getElementById('cart-btn').addEventListener('click', function () {
    updateCartModalContent();
    cartModal.show();
  });

  document.getElementById('checkout-btn').addEventListener('click', function () {
    cartModal.hide();
    checkoutModal.show();
  });

  document.getElementById('confirm-order-btn').addEventListener('click', function () {
    submitOrder();
  });
});

async function fetchProducts() {
  try {
    const response = await fetch(`${API_BASE}/products`);
    if (!response.ok) throw new Error('商品の取得に失敗しました');
    const products = await response.json();
    displayProducts(products);
  } catch (error) {
    console.error('Error:', error);
    alert('商品の読み込みに失敗しました');
  }
}

function displayProducts(products) {
  allProductsContainer.innerHTML = '';
  kitchenContainer.innerHTML = '';
  interiorContainer.innerHTML = '';

  products.forEach(product => {
    const cardHtml = `
      <div class="col">
        <div class="card product-card">
          <img src="${product.imageUrl || 'https://via.placeholder.com/300x200'}" class="card-img-top" alt="${product.name}">
          <div class="card-body">
            <h5 class="card-title">${product.name}</h5>
            <p class="card-text">¥${product.price.toLocaleString()}</p>
            <button class="btn btn-outline-primary view-product" data-id="${product.productId}">詳細を見る</button>
          </div>
        </div>
      </div>`;

    allProductsContainer.insertAdjacentHTML('beforeend', cardHtml);
    (product.name.includes('タンブラー') ? kitchenContainer : interiorContainer).insertAdjacentHTML('beforeend', cardHtml);
  });

  [allProductsContainer, interiorContainer, kitchenContainer].forEach(container => {
    container.querySelectorAll('.view-product').forEach(button => {
      button.addEventListener('click', () => fetchProductDetail(button.dataset.id));
    });
  });
}

async function fetchProductDetail(productId) {
  try {
    const response = await fetch(`${API_BASE}/products/${productId}`);
    if (!response.ok) throw new Error('商品詳細の取得に失敗しました');
    const product = await response.json();
    displayProductDetail(product);
  } catch (error) {
    console.error('Error:', error);
    alert('商品詳細の読み込みに失敗しました');
  }
}

function displayProductDetail(product) {
  document.getElementById('productModalTitle').textContent = product.name;
  const modalBody = document.getElementById('productModalBody');
  modalBody.innerHTML = `
    <div class="row">
      <div class="col-md-6">
        <img src="${product.imageUrl || 'https://via.placeholder.com/400x300'}" class="img-fluid" alt="${product.name}">
      </div>
      <div class="col-md-6">
        <p class="fs-4">¥${product.price.toLocaleString()}</p>
        <p>${product.description}</p>
        <p>在庫: ${product.stock} 個</p>
        <div class="d-flex align-items-center mb-3">
          <label for="quantity" class="me-2">数量:</label>
          <input type="number" id="quantity" class="form-control w-25" value="1" min="1" max="${product.stock}">
        </div>
        <button class="btn btn-primary add-to-cart" data-id="${product.productId}">カートに入れる</button>
      </div>
    </div>`;

  modalBody.querySelector('.add-to-cart').addEventListener('click', function () {
    const quantity = parseInt(document.getElementById('quantity').value);
    addToCart(product.productId, quantity);
  });

  productModal.show();
}

async function addToCart(productId, quantity) {
  try {
    const response = await fetch(`${API_BASE}/cart`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ productId, quantity })
    });

    if (!response.ok) throw new Error('カートへの追加に失敗しました');
    const cart = await response.json();
    updateCartBadge(cart.totalQuantity);
    productModal.hide();
    alert('商品をカートに追加しました');
  } catch (error) {
    console.error('Error:', error);
    alert('カートへの追加に失敗しました');
  }
}

async function updateCartDisplay() {
  try {
    const response = await fetch(`${API_BASE}/cart`);
    if (!response.ok) throw new Error('カート情報の取得に失敗しました');
    const cart = await response.json();
    updateCartBadge(cart.totalQuantity);
  } catch (error) {
    console.error('Error:', error);
  }
}

function updateCartBadge(count) {
  document.getElementById('cart-count').textContent = count;
}

async function updateCartModalContent() {
  try {
    const response = await fetch(`${API_BASE}/cart`);
    if (!response.ok) throw new Error('カート情報の取得に失敗しました');
    const cart = await response.json();
    cartItems = cart.items;
    displayCart(cart);
  } catch (error) {
    console.error('Error:', error);
    alert('カート情報の読み込みに失敗しました');
  }
}

function displayCart(cart) {
  const modalBody = document.getElementById('cartModalBody');
  if (!cart.items || Object.keys(cart.items).length === 0) {
    modalBody.innerHTML = '<p class="text-center">カートは空です</p>';
    document.getElementById('checkout-btn').disabled = true;
    return;
  }

  let html = '<table class="table"><thead><tr><th>商品</th><th>単価</th><th>数量</th><th>小計</th><th></th></tr></thead><tbody>';
  Object.values(cart.items).forEach(item => {
    html += `<tr><td>${item.name}</td><td>¥${item.price.toLocaleString()}</td><td><input type="number" class="form-control form-control-sm update-quantity" data-id="${item.id}" value="${item.quantity}" min="1" style="width: 70px"></td><td>¥${item.subtotal.toLocaleString()}</td><td><button class="btn btn-sm btn-danger remove-item" data-id="${item.id}">削除</button></td></tr>`;
  });
  html += `</tbody><tfoot><tr><th colspan="3" class="text-end">合計:</th><th>¥${cart.totalPrice.toLocaleString()}</th><th></th></tr></tfoot></table>`;

  modalBody.innerHTML = html;
  document.querySelectorAll('.update-quantity').forEach(input => {
    input.addEventListener('change', () => updateItemQuantity(input.dataset.id, input.value));
  });
  document.querySelectorAll('.remove-item').forEach(button => {
    button.addEventListener('click', () => removeItem(button.dataset.id));
  });

  document.getElementById('checkout-btn').disabled = false;
}

async function updateItemQuantity(itemId, quantity) {
  try {
    const response = await fetch(`${API_BASE}/cart/items/${itemId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ quantity: parseInt(quantity) })
    });
    if (!response.ok) throw new Error('数量の更新に失敗しました');
    const cart = await response.json();
    displayCart(cart);
    updateCartBadge(cart.totalQuantity);
  } catch (error) {
    console.error('Error:', error);
    alert('数量の更新に失敗しました');
    updateCartModalContent();
  }
}

async function removeItem(itemId) {
  try {
    const response = await fetch(`${API_BASE}/cart/items/${itemId}`, { method: 'DELETE' });
    if (!response.ok) throw new Error('商品の削除に失敗しました');
    const cart = await response.json();
    displayCart(cart);
    updateCartBadge(cart.totalQuantity);
  } catch (error) {
    console.error('Error:', error);
    alert('商品の削除に失敗しました');
  }
}

async function submitOrder() {
  const form = document.getElementById('order-form');
  if (!form.checkValidity()) {
    form.classList.add('was-validated');
    return;
  }

  const payMethod = document.querySelector('input[name="pay_method"]:checked');
  if (!payMethod) {
    alert("決済方法を選択してください");
    return;
  }

  const selectedMethod = payMethod.value;
  const token = localStorage.getItem('accessToken');
  let url = token ? '/api/user/orders' : '/api/orders';
  let headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const orderData = {
    customerInfo: {
      name: document.getElementById('customerName').value,
      email: document.getElementById('customerEmail').value,
      address: document.getElementById('address').value ||''
    },
    shippingInfo: {
      name: document.getElementById('shippingName').value,
      address: document.getElementById('shippingAddress').value
    },
    payment_method: selectedMethod,
    items: cartItems
  };

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(orderData)
    });

    if (!response.ok) throw new Error('注文の確定に失敗しました');

    const order = await response.json();
    displayOrderComplete(order);
    checkoutModal.hide();
    orderCompleteModal.show();
    updateCartBadge(0);
    form.reset();
    form.classList.remove('was-validated');
  } catch (error) {
    console.error('Error:', error);
    alert('注文の確定に失敗しました');
  }
}

function displayOrderComplete(order) {
  document.getElementById('orderCompleteBody').innerHTML = `
    <p>ご注文ありがとうございます。注文番号は <strong>${order.orderId}</strong> です。</p>
    <p>ご注文日時: ${new Date(order.orderDate).toLocaleString()}</p>
    <p>お客様のメールアドレスに注文確認メールをお送りしました。</p>`;
}

async function loadCartItems() {
  try {
    const response = await fetch(`${API_BASE}/cart`);
    const data = await response.json();
    cartItems = data.items || [];
  } catch (error) {
    console.error("カートの読み込みに失敗しました", error);
  }
}
