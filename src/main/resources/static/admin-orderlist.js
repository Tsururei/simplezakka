const API_BASE = '/api';

const STATUS_LABELS = {
  PENDING: '処理中',
  PAID: '決済済み',
  SHIPPED: '発送済み',
  CANCELLED: 'キャンセル',
  COMPLETED: '完了'
};

document.addEventListener("DOMContentLoaded", function (){
   
});

async function fetchOrders() {
    const response = await fetch(`${API_BASE}/admin/order`);
    const orders = await response.json();
    window.orders = orders;
    displayOrders(orders);
  }

  const tbody = document.querySelector("#orders-table tbody");
  const modal = document.getElementById("order-modal");
  const modalBody = document.getElementById("modal-body");


  function displayOrders(orders) {
    tbody.innerHTML = "";
    orders.forEach(order => {
      const statusLabel = STATUS_LABELS[order.status] || '不明';
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td><button class="order-id-btn" data-id="${order.orderId}">${order.orderId}</button></td>
        <td>${order.buyerName}</td>
        <td>${order.orderDate}</td>
        <td>¥${order.totalPrice.toLocaleString()}</td>
        <td>${statusLabel}</td>
      `;
      tbody.appendChild(tr);
    });
  }


  async function showModal(orderId) {
    const response = await fetch(`${API_BASE}/admin/order/${orderId}`);
    const order = await response.json();
    if (!order) return;

    const itemsTableHtml = `
    <table class="table table-sm">
      <thead>
        <tr>
          <th>商品名</th>
          <th>数量</th>
        </tr>
      </thead>
      <tbody>
        ${order.items.map(item => `
          <tr>
            <td>${item.productName}</td>
            <td>${item.quantity}</td>
          </tr>
        `).join('')}
      </tbody>
    </table>
  `;

    modalBody.innerHTML = `
      <p><strong>注文ID:</strong> ${order.orderId}</p>
      <p><strong>購入者名:</strong> ${order.buyerName}</p>
      <p><strong>配送先住所:</strong> ${order.shippingAddress}</p>
      <p><strong>購入代金:</strong> ¥${order.totalPrice.toLocaleString()}</p>
      <p>
       <strong>注文ステータス:</strong>
       <select id="status-select">
         <option value="PENDING" ${order.orderStatus === "PENDING" ? "selected" : ""}>処理中</option>
         <option value="PAID" ${order.orderStatus === "PAID" ? "selected" : ""}>決済済み</option>
         <option value="SHIPPED" ${order.orderStatus === "SHIPPED" ? "selected" : ""}>発送済み</option>
         <option value="CANCELLED" ${order.orderStatus === "CANCELLED" ? "selected" : ""}>キャンセル</option>
         <option value="COMPLETED" ${order.orderStatus === "COMPLETED" ? "selected" : ""}>完了</option>
       </select>
      </p>
      <p><strong>メールアドレス:</strong> ${order.customerEmail}</p>
      <p><strong>注文日時:</strong> ${order.orderDate}</p>
      <p><strong>詳細:</strong></p> ${itemsTableHtml}
      <button id="update-status-btn">ステータス更新</button>
    `;
    document.getElementById("update-status-btn").addEventListener("click", async () => {
    const newStatus = document.getElementById("status-select").value;

    await fetch(`${API_BASE}/admin/order/${orderId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ newStatus: newStatus })
    });

    alert("ステータスを更新しました");
    modal.style.display = "none";
    await fetchOrders(); // リストを再取得して反映
  });

    modal.style.display = "flex";
  }

  tbody.addEventListener('click', async e => {
    if (e.target.classList.contains("order-id-btn")) {
      const id = e.target.getAttribute("data-id");
      showModal(id);
    }
  });


  fetchOrders();


  window.addEventListener("click", e => {
    if (e.target === modal) {
      modal.style.display = "none";
    }
  });

 
  