// ===== FUNCIONES GENERALES ARTESLUIS =====

document.addEventListener('DOMContentLoaded', () => {
  // Marcar enlace activo en navegación
  const currentPage = window.location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('nav.sidebar a').forEach(link => {
    if (link.getAttribute('href') === currentPage) {
      link.classList.add('active');
    } else {
      link.classList.remove('active');
    }
  });

  // Inicializar detector de imágenes rotas
  initImageErrorHandler();
});

// ===== MANEJO DE IMÁGENES ROTAS =====
function initImageErrorHandler() {
  // Detectar todas las imágenes en la página
  const images = document.querySelectorAll('img');
  const videos = document.querySelectorAll('video source');
  
  console.log(`🖼️ Verificando ${images.length} imágenes y ${videos.length} videos...`);
  
  // Manejador para imágenes
  images.forEach((img, index) => {
    img.addEventListener('error', function() {
      console.error(`❌ Error cargando imagen: ${this.src}`);
      
      // Reemplazar con imagen placeholder
      if (!this.classList.contains('placeholder-applied')) {
        this.classList.add('placeholder-applied');
        this.src = '/img/logo-artesluis.png';
        this.alt = 'Imagen no disponible';
        this.style.opacity = '0.7';
        
        // Si la imagen placeholder también falla, mostrar div con texto
        this.addEventListener('error', function() {
          const placeholder = document.createElement('div');
          placeholder.className = 'image-placeholder';
          placeholder.innerHTML = `
            <div style="
              background: #f8f9fa;
              border: 2px dashed #dee2e6;
              border-radius: 8px;
              padding: 20px;
              text-align: center;
              color: #6c757d;
              min-height: 200px;
              display: flex;
              align-items: center;
              justify-content: center;
              flex-direction: column;
            ">
              <i class="bi bi-image" style="font-size: 3rem; margin-bottom: 10px;"></i>
              <p style="margin: 0;">Imagen no disponible</p>
              <small style="opacity: 0.7;">${this.getAttribute('alt') || 'Sin descripción'}</small>
            </div>
          `;
          this.parentNode.replaceChild(placeholder, this);
        }, { once: true });
      }
    });
    
    // Verificar si la imagen se carga correctamente
    img.addEventListener('load', function() {
      console.log(`✅ Imagen cargada correctamente: ${this.src}`);
    });
  });
  
  // Manejador para videos
  videos.forEach((source) => {
    const video = source.parentElement;
    
    video.addEventListener('error', function() {
      console.error(`❌ Error cargando video: ${source.src}`);
      
      // Reemplazar video con placeholder
      const placeholder = document.createElement('div');
      placeholder.className = 'video-placeholder';
      placeholder.innerHTML = `
        <div style="
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          border-radius: 15px;
          padding: 40px;
          text-align: center;
          min-height: 400px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-direction: column;
        ">
          <i class="bi bi-play-circle" style="font-size: 4rem; margin-bottom: 20px; opacity: 0.8;"></i>
          <h3 style="margin-bottom: 10px;">Video no disponible</h3>
          <p style="margin: 0; opacity: 0.9;">El contenido de video se cargará próximamente</p>
        </div>
      `;
      
      video.parentNode.replaceChild(placeholder, video);
    });
  });
}

// Función para verificar estado de recursos
function checkResourceStatus() {
  fetch('/api/diagnostics/resources')
    .then(response => response.json())
    .then(data => {
      console.log('📊 Estado de recursos:', data);
      
      if (data.healthCheck === 'ISSUES_FOUND') {
        console.warn(`⚠️ Se encontraron ${data.missingResources} recursos faltantes de ${data.totalResources} verificados`);
      } else {
        console.log('✅ Todos los recursos están disponibles');
      }
    })
    .catch(error => {
      console.error('Error verificando recursos:', error);
    });
}

// ===== GESTIÓN DE GALERÍA DE IMÁGENES =====
document.addEventListener("DOMContentLoaded", () => {
  const gallery = document.getElementById("gallery");
  const input = document.getElementById("imageInput");

  if (input && gallery) {
    input.addEventListener("change", (event) => {
      const files = event.target.files;

      // Límite de imágenes
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

          // Click para abrir visor emergente
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

          // Botones editar / eliminar
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

// ===== SISTEMA DE LOGIN (INTEGRADO CON SPRING) =====
document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");

  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const correo = document.getElementById("correo").value.trim();
      const password = document.getElementById("password").value.trim();
      const message = document.getElementById("loginMessage");

      try {
        const response = await fetch('/api/usuarios/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ correo, password })
        });

        const data = await response.json();

        if (data.success) {
          // Guardar información del usuario en localStorage
          localStorage.setItem('usuario', JSON.stringify(data.usuario));
          localStorage.setItem('isLoggedIn', 'true');
          
          // Redirigir según el rol
          if (data.usuario.rol === 'ADMIN') {
            window.location.href = '/admin';
          } else {
            window.location.href = '/dashboard';
          }
        } else {
          message.textContent = data.message || 'Usuario o contraseña incorrectos.';
          message.className = 'error-message';
        }
      } catch (error) {
        message.textContent = 'Error de conexión. Intenta nuevamente.';
        message.className = 'error-message';
      }
    });
  }

  // Protección de rutas administrativas
  if (window.location.pathname.includes("/admin")) {
    const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
    const logged = localStorage.getItem("isLoggedIn");
    
    if (logged !== "true" || usuario.rol !== "ADMIN") {
      window.location.href = "/login";
    }
  }

  // Botón de logout
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", logout);
  }
});

// Función de logout
function logout() {
  localStorage.removeItem('isLoggedIn');
  localStorage.removeItem('usuario');
  window.location.href = '/login';
}

// ===== UTILIDADES =====

// Función para obtener usuario actual
function getCurrentUser() {
  return JSON.parse(localStorage.getItem('usuario') || '{}');
}

// Función para verificar si está autenticado
function isAuthenticated() {
  return localStorage.getItem('isLoggedIn') === 'true';
}

// Función para hacer peticiones autenticadas
async function authenticatedFetch(url, options = {}) {
  const usuario = getCurrentUser();
  
  if (!isAuthenticated()) {
    throw new Error('No autenticado');
  }

  return fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    }
  });
}