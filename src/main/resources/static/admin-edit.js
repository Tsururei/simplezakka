
    
    const admins = [
      {
        name: "つるれい",
        email: "tsururei@example.com",
        password: "******",
        registered: "2025-07-14",
      },
    
    ];

    const tbody = document.querySelector("#admin-edit-table tbody");

    function displayAdmins() {
      tbody.innerHTML = "";
      admins.forEach((admin, index) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${admin.name}</td>
          <td>${admin.email}</td>
          <td>${admin.password}</td>
          <td>${admin.registered}</td>
          <td><button data-index="${index}" class="delete-btn">削除</button></td>
        `;
        tbody.appendChild(tr);
      });
    }

    tbody.addEventListener("click", e => {
      if (e.target.classList.contains("delete-btn")) {
        const index = e.target.getAttribute("data-index");
        admins.splice(index, 1); 
        displayAdmins(); 
      }
    });

    displayAdmins();


