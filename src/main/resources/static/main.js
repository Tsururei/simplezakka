// グローバル変数宣言
let productModal, cartModal, checkoutModal, orderCompleteModal;
let allProductsContainer, kitchenContainer, interiorContainer;
let cartItems = [];

const API_BASE = '/api';
const token = localStorage.getItem('accessToken');
const isGuest = !token;

document.addEventListener('DOMContentLoaded', async function () {
  await commonInit();

    if (isGuest) {
    await initGuest();
  } else {
    await initMember();
  }
});

async function commonInit() {
  //ここに共通処理 
  //モーダル初期化
  productModal = new bootstrap.Modal(document.getElementById('productModal'));
  cartModal = new bootstrap.Modal(document.getElementById('cartModal'));
  checkoutModal = new bootstrap.Modal(document.getElementById('checkoutModal'));
  orderCompleteModal = new bootstrap.Modal(document.getElementById('orderCompleteModal'));

  // 商品一覧コンテナを取得（グローバル変数）
  allProductsContainer = document.querySelector('#all-products .all-products');
  kitchenContainer = document.querySelector('#kitchen .kitchen-products');
  interiorContainer = document.querySelector('#interior .interior-products');

  // タブクリックで関数呼び出し
  document.getElementById('all-tab').addEventListener('click', () => showTab('all'));
  document.getElementById('kitchen-tab').addEventListener('click', () => showTab('kitchen'));
  document.getElementById('interior-tab').addEventListener('click', () => showTab('interior'));

  // 最初に全商品を表示
  showTab('all');

  // 商品取得処理
  await fetchProducts();


}


async function initGuest() {
    //この中にゲスト処理
    //ログアウトボタンの画面遷移
  document.getElementById('logout-btn').addEventListener('click', async function () {
      window.location.href = 'index.html';
    });

    //ゲストの購入へボタン
   document.getElementById('checkout-btn').addEventListener('click', function() {
    cartModal.hide();
    checkoutModal.show();
    })
    //カートの更新
  updateCartDisplay();
  document.getElementById('cart-btn').addEventListener('click', function () {
    updateCartModalContent();
    cartModal.show();
  });
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

//カートの表示
function displayCart(cart) {
  const modalBody = document.getElementById('cartModalBody');
  if (!cart.items || Object.keys(cart.items).length === 0) {
    modalBody.innerHTML = '<p class="text-center">カートは空です</p>';
    document.getElementById('checkout-btn').disabled = true;
    return;
  }
else {
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
}}

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

//注文確定ボタン
  document.getElementById('confirm-order-btn').addEventListener('click', function () {
    submitOrder();
  });
        async function submitOrder() {
        const form = document.getElementById('order-form');
        console.log('注文確定ボタンがクリックされました');

        
        // フォームバリデーション
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }
        const payMethod = document.querySelector('input[name="payMethod"]:checked');
        if (!payMethod) {
            alert("決済方法を選択してください");
            return;
        }
        const selectedMethod = payMethod.value;  // "cod" または "bank"

        let latestCartItems = [];
        try {
            const response = await fetch(`${API_BASE}/cart`, {
                method: 'GET',
                credentials: 'include'
            });              
            const data = await response.json();
            latestCartItems = Object.values(data.items).map(item => ({
                productId: item.productId,
                quantity: item.quantity
            }));
            
            if (latestCartItems.length === 0) {
                alert('注文商品は必須です');
                return;
            }
        } catch (err) {
            console.error('カート情報の取得に失敗しました', err);
            alert('カート情報の取得に失敗しました');
            return;
        }

        const orderData = {
            customerInfo: {
                customerName: document.getElementById('customerName').value,
                customerAddress: document.getElementById('customerAddress').value,                
                customerEmail: document.getElementById('customerEmail').value,
                shippingName: document.getElementById('shippingName').value,
                shippingAddress: document.getElementById('shippingAddress').value,
                payMethod: selectedMethod
            },
            items: latestCartItems
        };
        
        console.log('注文データ（送信前）:', orderData);
        try {
            console.log('送信データ:', orderData);

            const response = await fetch(`${API_BASE}/orders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify(orderData)
            });
          
            console.log('APIレスポンスのstatus:', response.status);
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('注文確定APIのエラー内容:', errorText);
                throw new Error('注文の確定に失敗しました');
            }
            
            const order = await response.json();
            displayOrderComplete(order);
            
            checkoutModal.hide();
            orderCompleteModal.show();
            
            

            // カート表示をリセット
            updateCartBadge(0);
            
            // フォームリセット
            form.reset();
            form.classList.remove('was-validated');
        } catch (error) {
            console.error('Error:', error);
            alert('注文の確定に失敗しました');
        }
    }

  };

async function initMember() {
    //この中に会員処理
    //ログアウト
  document.getElementById('logout-btn').addEventListener('click', async function () {
    const response = await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
    if (response.ok) {
      window.location.href = '/index.html';
    } else {
      alert('ログアウトに失敗しました');
    }
  });

  //カートの更新  
    updateUserCartDisplay();
  document.getElementById('cart-btn').addEventListener('click', function () {
    updateUserCartModalContent();
    cartModal.show();
  });
      async function updateUserCartDisplay() {
        const userId = localStorage.getItem('userId');
  try {
    const response = await fetch(`${API_BASE}/usercart?userId=${userId}`);
    if (!response.ok) throw new Error('カート情報の取得に失敗しました');
    const cart = await response.json();
    updateCartBadge(cart.totalQuantity);
  } catch (error) {
    console.error('Error:', error);
  }
}

async function updateUserCartModalContent() {
    const userId = localStorage.getItem('userId');
    try{
            const response = await fetch(`${API_BASE}/usercart?userId=${userId}`);
    if (!response.ok) throw new Error('カート情報の取得に失敗しました');
    const cart = await response.json();
    cartItems = cart.items;
    displayUserCart(cart);
  } catch (error) {
    console.error('Error:', error);
    alert('カート情報の読み込みに失敗しました');
  }
}
//カートの表示
function displayUserCart(cart) {
  const modalBody = document.getElementById('cartModalBody');
  if (!cart.items || Object.keys(cart.items).length === 0) {
    modalBody.innerHTML = '<p class="text-center">カートは空です</p>';
    document.getElementById('checkout-btn').disabled = true;
    return;
  }
else {
  let html = '<table class="table"><thead><tr><th>商品</th><th>単価</th><th>数量</th><th>小計</th><th></th></tr></thead><tbody>';
  Object.values(cart.items).forEach(item => {
    html += `<tr><td>${item.name}</td><td>¥${item.price.toLocaleString()}</td><td><input type="number" class="form-control form-control-sm update-quantity" data-id="${item.id}" value="${item.quantity}" min="1" style="width: 70px"></td><td>¥${item.subtotal.toLocaleString()}</td><td><button class="btn btn-sm btn-danger remove-item" data-id="${item.id}">削除</button></td></tr>`;
  });
  html += `</tbody><tfoot><tr><th colspan="3" class="text-end">合計:</th><th>¥${cart.totalPrice.toLocaleString()}</th><th></th></tr></tfoot></table>`;

  modalBody.innerHTML = html;
  document.querySelectorAll('.update-quantity').forEach(input => {
    input.addEventListener('change', () => updateUserItemQuantity(input.dataset.id, input.value));
  });
  document.querySelectorAll('.remove-item').forEach(button => {
    button.addEventListener('click', () => removeUserItem(button.dataset.id));
  });

  document.getElementById('checkout-btn').disabled = false;
}}

async function updateUserItemQuantity(itemId, quantity) {
    const userId = localStorage.getItem('userId');
  try {
    const response = await fetch(`${API_BASE}/usercart/items/${itemId}?userId=${userId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ quantity: parseInt(quantity) })
    });
    if (!response.ok) throw new Error('数量の更新に失敗しました');
    const cart = await response.json();
    displayUserCart(cart);
    updateCartBadge(cart.totalQuantity);
  } catch (error) {
    console.error('Error:', error);
    alert('数量の更新に失敗しました');
    updateUserCartModalContent();
  }
}

async function removeUserItem(itemId) {
    const userId = localStorage.getItem('userId');
  try {
    const response = await fetch(`${API_BASE}/cart/useritems/${itemId}`, { method: 'DELETE' });
    if (!response.ok) throw new Error('商品の削除に失敗しました');
    const cart = await response.json();
    displayUserCart(cart);
    updateCartBadge(cart.totalQuantity);
  } catch (error) {
    console.error('Error:', error);
    alert('商品の削除に失敗しました');
  }
}

  //購入へからの会員情報自動入力
async function prefillUserInfo() {
  const token = localStorage.getItem('accessToken');
  console.log("accessToken:", token);
  if (!token) return;
  
try{
  const resp = await fetch(`${API_BASE}/user/me`,{
    headers: { 'Authorization': `Bearer ${token}` }
  });

  console.log("status:", resp.status);
  if (!resp.ok) return;

  const user = await resp.json();
  console.log("user data:", user);

  document.getElementById('customerName').value = user.name;
  document.getElementById('customerEmail').value = user.email;
  document.getElementById('customerAddress').value = user.address;
} catch(e){
  console.error("prefill error:", e);
}
}

  document.getElementById('checkout-btn').addEventListener('click', function () {
    cartModal.hide();
    prefillUserInfo();
    checkoutModal.show();
  });

  //注文確定ボタン
  document.getElementById('confirm-order-btn').addEventListener('click', function () {
    submitUserOrder();
  });

async function submitUserOrder() {
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
  //ここからいらない？
  const token = localStorage.getItem('accessToken');
  let url = token ? '/api/user/orders' : '/api/orders';
  let headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  //ここまで要らない？
  let latestCartItems = [];
  const userId = localStorage.getItem('userId');
  try {
    const response = await fetch(`${API_BASE}/usercart?userId=${userId}`, {
        method: 'GET',
        credentials: 'include'
    });
    const data = await response.json();
    latestCartItems = Object.values(data.items).map(item => ({
        productId: item.productId,
        quantity: item.quantity
    }));
    if (latestCartItems.length === 0) {
        alert('注文情報は必須です');
        return;
    }
  } catch (err) {
        console.error('カート情報の取得に失敗しました', err);
        alert('カート情報の取得に失敗しました');
        return;
    }
    
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
    items: latestCartItems
  };
console.log('注文データ（送信前）:', orderData);
  try {
    console.log('送信データ:', orderData);

    const response = await fetch(`${API_BASE}/user/orders`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'
      },
      credentials:'include',
      body: JSON.stringify(orderData)
    });

    console.log('APIレスポンスのstatus:', response.status);

    if (!response.ok) {
        console.error('注文確定ＡＰＩのエラー内容:', errorText);
        throw new Error('注文の確定に失敗しました');
    }
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
}

//ここに分岐外に書くべき共通処理
function showTab(tabName) {
  document.querySelectorAll('.tab-pane').forEach(pane => {
    pane.classList.remove('show', 'active');
    pane.style.display = 'none';
  });

//詳細ボタンの処理
  [allProductsContainer.parentElement, kitchenContainer.parentElement, interiorContainer.parentElement]
    .forEach(el => el.style.display = 'none');

  let container;
  switch (tabName) {
    case 'all':
      container = allProductsContainer.parentElement;
      document.getElementById('all-products').classList.add('show', 'active');
      break;
    case 'kitchen':
      container = kitchenContainer.parentElement;
      document.getElementById('kitchen').classList.add('show', 'active');
      break;
    case 'interior':
      container = interiorContainer.parentElement;
      document.getElementById('interior').classList.add('show', 'active');
      break;
  }
  if (container) container.style.display = 'flex';

  document.querySelectorAll('.nav-link').forEach(tab => tab.classList.remove('active'));
  document.getElementById(`${tabName}-tab`).classList.add('active');
}

document.getElementById('interior-tab').addEventListener('click', function () {
  // 全タブの中身（.tab-pane）を非表示に
  document.querySelectorAll('.tab-pane').forEach(pane => {
    pane.classList.remove('show', 'active');
  });

  // インテリアの商品一覧を表示
  document.getElementById('interior').classList.add('show', 'active');


  // tab切り替え共通部分
  document.querySelectorAll('.nav-link').forEach(tab => {
    tab.classList.remove('active');
  });
  this.classList.add('active'); 

});


  // 商品取得処理
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
    if (isGuest) {
    addToCart(product.productId, quantity);
    }
    else {
    addToUserCart(product.productId, quantity)
    }
  });

  productModal.show();
}

    //ゲストのカートに追加
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

    //会員のカートに追加
    async function addToUserCart(productId,quantity) {
    const userId = localStorage.getItem('userId'); 
    try {
        const response = await fetch(`${API_BASE}/usercart?userId=${userId}`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
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

function updateCartBadge(count) {
  document.getElementById('cart-count').textContent = count;
}


function displayOrderComplete(order) {
  document.getElementById('orderCompleteBody').innerHTML = `
    <p>ご注文ありがとうございます。注文番号は <strong>${order.orderId}</strong> です。</p>
    <p>ご注文日時: ${new Date(order.orderDate).toLocaleString()}</p>
    <p>後ほどお客様のメールアドレスに注文確認メールをお送りいたします。</p>`;
}

//初期カート読み込み
  document.addEventListener('DOMContentLoaded', function () {
    if (isGuest){
    loadCartItems();
}
else {
    loadUserCartItems();
}
});

async function loadCartItems() {
    try {
        const response = await fetch(`${API_BASE}/cart`);
        const data = await response.json();
        console.log("APIのカート情報:", data);
        if (data.items) {
            if (Array.isArray(data.items)) {
                cartItems = data.items;
            } else {
                cartItems = Object.values(data.items);
            }
        } else {
            cartItems = [];
        }
        console.log("カートの中身:", cartItems);
    } catch (error) {
        console.error("カートの読み込みに失敗しました", error);
    }
}

async function loadUserCartItems() {
    const userId = localStorage.getItem('userId');
    try {
        const response = await fetch(`${API_BASE}/usercart?userId=${userId}`);
        const data = await response.json();
        console.log("APIのカート情報:", data);
        if (data.items) {
            if (Array.isArray(data.items)) {
                cartItems = data.items;
            } else {
                cartItems = Object.values(data.items);
            }
        } else {
            cartItems = [];
        }
        console.log("カートの中身:", cartItems);
    } catch (error) {
        console.error("カートの読み込みに失敗しました", error);
    }
}