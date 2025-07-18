
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
    const response = await fetch('http://localhost:8080/admin/order');
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
      const statusLabel = STATUS_LABELS[order.orderStatus] || '不明';
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
    const response = await fetch(`http://localhost:8080/admin/order/${orderId}`);
    const order = await response.json();
    if (!order) return;

    modalBody.innerHTML = `
      <p><strong>注文ID:</strong> ${order.orderId}</p>
      <p><strong>購入者名:</strong> ${order.buyerName}</p>
      <p><strong>配送先住所:</strong> ${order.shippingAddress}</p>
      <p><strong>購入代金:</strong> ¥${order.totalPrice.toLocaleString()}</p>
      <p><strong>注文ステータス:</strong> ${order.orderStatus}</p>
      <p><strong>メールアドレス:</strong> ${order.customerEmail}</p>
      <p><strong>注文日時:</strong> ${order.orderDate}</p>
      <p><strong>詳細:</strong> ${order.items.map(d => `${d.productName} ${d.quantity}個`).join(", ")}</p>
    `;
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
