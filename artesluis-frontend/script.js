document.addEventListener('DOMContentLoaded', () => {
  const currentPage = window.location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('nav.sidebar a').forEach(link => {
    if (link.getAttribute('href') === currentPage) {
      link.classList.add('active');
    } else {
      link.classList.remove('active');
    }
  });
});


// --- Script para gestionar imágenes en admin.html ---
document.addEventListener("DOMContentLoaded", () => {
  const gallery = document.getElementById("gallery");
  const input = document.getElementById("imageInput");

  if (input && gallery) {
    input.addEventListener("change", (event) => {
      const files = event.target.files;

      // 🔹 Límite de imágenes (puedes cambiar el número)
      if (gallery.children.length >= 10) {
        alert("Solo puedes subir un máximo de 10 imágenes.");
        return;
      }

      for (const file of files) {
        const reader = new FileReader();
        reader.onload = (e) => {
          const container = document.createElement("div");
          container.classList.add("image-container");

          const img = document.createElement("img");
          img.src = e.target.result;
          img.alt = "Imagen subida";
          img.classList.add("gallery-image");

          // 🔹 Click para abrir visor emergente
          img.addEventListener("click", () => {
            const viewer = document.createElement("div");
            viewer.classList.add("image-viewer");
            viewer.innerHTML = `
              <div class="viewer-content">
                <span class="close">&times;</span>
                <img src="${img.src}" alt="Imagen ampliada">
              </div>
            `;
            document.body.appendChild(viewer);

            viewer.querySelector(".close").addEventListener("click", () => {
              viewer.remove();
            });
          });

          // 🔹 Botones editar / eliminar
          const btnDelete = document.createElement("button");
          btnDelete.textContent = "Eliminar";
          btnDelete.classList.add("delete-btn");
          btnDelete.addEventListener("click", () => container.remove());

          const btnEdit = document.createElement("button");
          btnEdit.textContent = "Editar";
          btnEdit.classList.add("edit-btn");
          btnEdit.addEventListener("click", () => {
            const newSrc = prompt("Pega la nueva URL de la imagen:");
            if (newSrc) img.src = newSrc;
          });

          container.appendChild(img);
          container.appendChild(btnEdit);
          container.appendChild(btnDelete);
          gallery.appendChild(container);
        };
        reader.readAsDataURL(file);
      }
    });
  }
});



// --- LOGIN ADMINISTRADOR ---
document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");

  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const username = document.getElementById("username").value.trim();
      const password = document.getElementById("password").value.trim();
      const message = document.getElementById("loginMessage");

      // 🔹 Usuario y contraseña predefinidos
      const USER = "admin";
      const PASS = "1234";

      if (username === USER && password === PASS) {
        localStorage.setItem("isLoggedIn", "true");
        window.location.href = "admin.html";
      } else {
        message.textContent = "Usuario o contraseña incorrectos.";
      }
    });
  }

  // --- Protección del panel admin ---
  if (window.location.pathname.includes("admin.html")) {
    const logged = localStorage.getItem("isLoggedIn");
    if (logged !== "true") {
      window.location.href = "login.html";
    }
  }
});


// --- LOGIN ADMINISTRADOR ---
document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");

  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const username = document.getElementById("username").value.trim();
      const password = document.getElementById("password").value.trim();
      const message = document.getElementById("loginMessage");

      const USER = "admin";
      const PASS = "1234";

      if (username === USER && password === PASS) {
        localStorage.setItem("isLoggedIn", "true");
        window.location.href = "admin.html";
      } else {
        message.textContent = "Usuario o contraseña incorrectos.";
      }
    });
  }

  // Protección de acceso al admin
  if (window.location.pathname.includes("admin.html")) {
    const logged = localStorage.getItem("isLoggedIn");
    if (logged !== "true") {
      window.location.href = "login.html";
    }
  }

  // --- Cerrar sesión ---
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      localStorage.removeItem("isLoggedIn");
      window.location.href = "login.html";
    });
  }
});

function logout() {
  localStorage.removeItem('isLoggedIn');
  window.location.href = 'login.html';
}


