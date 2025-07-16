// グローバル変数宣言
let productModal, cartModal, checkoutModal, orderCompleteModal;
let allProductsContainer, kitchenContainer, interiorContainer;
let cartItems = [];

const API_BASE = '/api';

document.addEventListener('DOMContentLoaded', function () {
    // カテゴリタブ押下時のイベント登録
    loadCartItems(); 
document.getElementById('kitchen-tab').addEventListener('click', function () {
  // 全タブの中身（.tab-pane）を非表示に
  document.querySelectorAll('.tab-pane').forEach(pane => {
    pane.classList.remove('show', 'active');
  });

  // キッチン用の商品一覧を表示
  document.getElementById('kitchen').classList.add('show', 'active');

  // タブの active 状態も切り替え
  document.getElementById('interior-tab').classList.remove('active');
  this.classList.add('active');

  // キッチンタブを押したときの表示
allProductsContainer.style.display = 'none';
kitchenContainer.style.display = 'block';  
interiorContainer.style.display = 'none';
});

document.getElementById('interior-tab').addEventListener('click', function () {
  // 全タブの中身（.tab-pane）を非表示に
  document.querySelectorAll('.tab-pane').forEach(pane => {
    pane.classList.remove('show', 'active');
  });

  // インテリアの商品一覧を表示
  document.getElementById('interior').classList.add('show', 'active');

  // タブの active 状態も切り替え
  document.getElementById('kitchen-tab').classList.remove('active');
  this.classList.add('active');

  // ここで表示切替後の状態
  allProductsContainer.style.display = 'none';
  kitchenContainer.style.display = 'none';
  interiorContainer.style.display = 'block';

});

  // Bootstrapモーダル初期化
  productModal = new bootstrap.Modal(document.getElementById('productModal'));
  cartModal = new bootstrap.Modal(document.getElementById('cartModal'));
  checkoutModal = new bootstrap.Modal(document.getElementById('checkoutModal'));
  orderCompleteModal = new bootstrap.Modal(document.getElementById('orderCompleteModal'));

  // 商品一覧コンテナをグローバル変数に代入
  allProductsContainer = document.getElementById('all-products');
  kitchenContainer = document.querySelector('.kitchen-products').parentElement;
  interiorContainer = document.querySelector('.interior-products').parentElement;

  // 初期表示設定
  allProductsContainer.style.display = 'flex';
  kitchenContainer.style.display = 'none';
  interiorContainer.style.display = 'none';

  // 商品一覧の取得・表示
  fetchProducts();

  // ホームボタンのイベント登録
  const homeBtn = document.querySelector('a[href="/home.html"]');
  if (homeBtn) {
    homeBtn.addEventListener('click', (e) => {
      e.preventDefault();
      allProductsContainer.style.display = 'flex';
      kitchenContainer.style.display = 'none';
      interiorContainer.style.display = 'none';
      document.querySelectorAll('.nav-tabs button').forEach(btn => btn.classList.remove('active'));
    });
  }

  // カート情報表示
  updateCartDisplay();

  // カートボタン押下時のイベント登録
  document.getElementById('cart-btn').addEventListener('click', function() {
    updateCartModalContent();
    cartModal.show();
  });

  // 注文手続きボタン押下時のイベント登録
  document.getElementById('checkout-btn').addEventListener('click', function() {
    cartModal.hide();
    checkoutModal.show();
  });

  // 注文確定ボタン押下時のイベント登録
  document.getElementById('confirm-order-btn').addEventListener('click', function() {
    submitOrder();
  });
});

    
    // 商品一覧を取得して表示する関数
    async function fetchProducts() {
        try {
            const response = await fetch(`${API_BASE}/products`);
            if (!response.ok) {
                throw new Error('商品の取得に失敗しました');
            }
            const products = await response.json();
            displayProducts(products);
        } catch (error) {
            console.error('Error:', error);
            alert('商品の読み込みに失敗しました');
        }
    }
    
    // 商品一覧を表示する関数
function displayProducts(products) {
    const allProductsContainer = document.getElementById('all-products'); // 全商品表示用
    const kitchenContainer = document.querySelector('.kitchen-products');
    const interiorContainer = document.querySelector('.interior-products');


    allProductsContainer.innerHTML = '';
    interiorContainer.innerHTML = '';
    kitchenContainer.innerHTML = '';

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
            </div>
        `;

        allProductsContainer.insertAdjacentHTML('beforeend', cardHtml);

        if (
          product.name === 'ステンレスタンブラー' ||
          product.name === '木製コースター（4枚セット）' ||
          product.name === 'ガラス保存容器セット'
        ) {
            kitchenContainer.insertAdjacentHTML('beforeend', cardHtml);
        } else {
            interiorContainer.insertAdjacentHTML('beforeend', cardHtml);
        }
    });

    // 詳細ボタンのイベント登録
    [allProductsContainer, interiorContainer, kitchenContainer].forEach(container => {
        container.querySelectorAll('.view-product').forEach(button => {
            button.addEventListener('click', (e) => {
                const id = e.currentTarget.dataset.id;
                fetchProductDetail(id);
            });
        });
    });
}
    
    // 商品詳細を取得する関数
    async function fetchProductDetail(productId) {
        try {
            const response = await fetch(`${API_BASE}/products/${productId}`);
            if (!response.ok) {
                throw new Error('商品詳細の取得に失敗しました');
            }
            const product = await response.json();
            displayProductDetail(product);
        } catch (error) {
            console.error('Error:', error);
            alert('商品詳細の読み込みに失敗しました');
        }
    }
    
    // 商品詳細を表示する関数
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
            </div>
        `;
        
        // カートに追加ボタンのイベント設定
        modalBody.querySelector('.add-to-cart').addEventListener('click', function() {
            const quantity = parseInt(document.getElementById('quantity').value);
            addToCart(product.productId, quantity);
        });
        
        productModal.show();
    }
    
    // カートに商品を追加する関数
    async function addToCart(productId, quantity) {
        try {
            const response = await fetch(`${API_BASE}/cart`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    productId: productId,
                    quantity: quantity
                })
            });
            
            if (!response.ok) {
                throw new Error('カートへの追加に失敗しました');
            }
            
            const cart = await response.json();
            updateCartBadge(cart.totalQuantity);
            
            productModal.hide();
            alert('商品をカートに追加しました');
        } catch (error) {
            console.error('Error:', error);
            alert('カートへの追加に失敗しました');
        }
    }
    
    // カート情報を取得する関数
    async function updateCartDisplay() {
        try {
            const response = await fetch(`${API_BASE}/cart`);
            if (!response.ok) {
                throw new Error('カート情報の取得に失敗しました');
            }
            const cart = await response.json();
            updateCartBadge(cart.totalQuantity);
        } catch (error) {
            console.error('Error:', error);
        }
    }
    
    // カートバッジを更新する関数
    function updateCartBadge(count) {
        document.getElementById('cart-count').textContent = count;
    }
    
    // カートモーダルの内容を更新する関数
    async function updateCartModalContent() {
        try {
            const response = await fetch(`${API_BASE}/cart`);
            if (!response.ok) {
                throw new Error('カート情報の取得に失敗しました');
            }
            const cart = await response.json();
            displayCart(cart);
        } catch (error) {
            console.error('Error:', error);
            alert('カート情報の読み込みに失敗しました');
        }
    }

    async function loadCartItems() {
    try {
        const response = await fetch(`${API_BASE}/cart`);
        const data = await response.json();
        cartItems = data.items; // ← 必要に応じて product_id, quantity に変換
        console.log("カートの中身:", cartItems);
    } catch (error) {
        console.error("カートの読み込みに失敗しました", error);
    }
}
    
    // カート内容を表示する関数
    function displayCart(cart) {
        const modalBody = document.getElementById('cartModalBody');
        
        if (cart.items && Object.keys(cart.items).length > 0) {
            let html = `
                <table class="table">
                    <thead>
                        <tr>
                            <th>商品</th>
                            <th>単価</th>
                            <th>数量</th>
                            <th>小計</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
            `;
            
            Object.values(cart.items).forEach(item => {
                html += `
                    <tr>
                        <td>${item.name}</td>
                        <td>¥${item.price.toLocaleString()}</td>
                        <td>
                            <input type="number" class="form-control form-control-sm update-quantity" 
                                   data-id="${item.id}" value="${item.quantity}" min="1" style="width: 70px">
                        </td>
                        <td>¥${item.subtotal.toLocaleString()}</td>
                        <td>
                            <button class="btn btn-sm btn-danger remove-item" data-id="${item.id}">削除</button>
                        </td>
                    </tr>
                `;
            });
            
            html += `
                    </tbody>
                    <tfoot>
                        <tr>
                            <th colspan="3" class="text-end">合計:</th>
                            <th>¥${cart.totalPrice.toLocaleString()}</th>
                            <th></th>
                        </tr>
                    </tfoot>
                </table>
            `;
            
            modalBody.innerHTML = html;
            
            // 数量更新イベントの設定
            document.querySelectorAll('.update-quantity').forEach(input => {
                input.addEventListener('change', function() {
                    updateItemQuantity(this.dataset.id, this.value);
                });
            });
            
            // 削除ボタンイベントの設定
            document.querySelectorAll('.remove-item').forEach(button => {
                button.addEventListener('click', function() {
                    removeItem(this.dataset.id);
                });
            });
            
            // 注文ボタンの有効化
            document.getElementById('checkout-btn').disabled = false;
        } else {
            modalBody.innerHTML = '<p class="text-center">カートは空です</p>';
            document.getElementById('checkout-btn').disabled = true;
        }
    }
    
    // カート内の商品数量を更新する関数
    async function updateItemQuantity(itemId, quantity) {
        try {
            const response = await fetch(`${API_BASE}/cart/items/${itemId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    quantity: parseInt(quantity)
                })
            });
            
            if (!response.ok) {
                throw new Error('数量の更新に失敗しました');
            }
            
            const cart = await response.json();
            displayCart(cart);
            updateCartBadge(cart.totalQuantity);
        } catch (error) {
            console.error('Error:', error);
            alert('数量の更新に失敗しました');
            updateCartModalContent(); // 失敗時は元の状態に戻す
        }
    }
    
    // カート内の商品を削除する関数
    async function removeItem(itemId) {
        try {
            const response = await fetch(`${API_BASE}/cart/items/${itemId}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('商品の削除に失敗しました');
            }
            
            const cart = await response.json();
            displayCart(cart);
            updateCartBadge(cart.totalQuantity);
        } catch (error) {
            console.error('Error:', error);
            alert('商品の削除に失敗しました');
        }
    }
    
    // 注文を確定する関数
    async function submitOrder() {
        const form = document.getElementById('order-form');
        
        // フォームバリデーション
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }
        const payMethod = document.querySelector('input[name="pay_method"]:checked');
        if (!payMethod) {
        alert("決済方法を選択してください");
        return;
        }
        const selectedMethod = payMethod.value;  // "cod" または "bank"

        const orderData = {
            customerInfo: {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                address: document.getElementById('address').value,
            },
                
                shippingInfo: {
                name: document.getElementById('ship_name').value,
                address: document.getElementById('ship_address').value,
            },
            payment_method: selectedMethod,
            items: cartItems 
        };
        
        try {
            console.log('送信データ:', orderData);

            const response = await fetch(`${API_BASE}/orders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(orderData)
            });
            
            if (!response.ok) {
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
    
    // 注文完了画面を表示する関数
    function displayOrderComplete(order) {
        document.getElementById('orderCompleteBody').innerHTML = `
            <p>ご注文ありがとうございます。注文番号は <strong>${order.orderId}</strong> です。</p>
            <p>ご注文日時: ${new Date(order.orderDate).toLocaleString()}</p>
            <p>お客様のメールアドレスに注文確認メールをお送りしました。</p>
        `;
    }
;