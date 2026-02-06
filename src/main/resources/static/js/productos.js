// ====================================
// PRODUCTOS.JS - VERSIÓN OPTIMIZADA CON DATA-ID
// ====================================

// --- CACHE DE ELEMENTOS DOM ---
const DOM = {
    menuCategorias: null,
    iconToggle: null,
    btnToggle: null,
    contenedorProductos: null,
    cartSidebar: null,
    cartOverlay: null,
    cartItemsContainer: null,
    cartTotalPrice: null,
    modalDetalles: null,
    contadorNavbar: null,
    contadorSidebar: null,
    
    init() {
        this.menuCategorias = document.getElementById('menu-categorias');
        this.iconToggle = document.getElementById('icon-toggle');
        this.btnToggle = document.getElementById('btn-toggle-menu');
        this.contenedorProductos = document.getElementById('contenedor-productos');
        this.cartSidebar = document.getElementById('cart-sidebar');
        this.cartOverlay = document.getElementById('cart-overlay');
        this.cartItemsContainer = document.getElementById('cart-items-container');
        this.cartTotalPrice = document.getElementById('cart-total-price');
        this.modalDetalles = document.getElementById('modal-detalles');
        this.contadorNavbar = document.getElementById('carrito-count');
        this.contadorSidebar = document.getElementById('cart-items-count');
    }
};

// --- VARIABLES GLOBALES ---
let carrito = JSON.parse(localStorage.getItem('carrito')) || [];

// --- UTILIDADES DE PERFORMANCE ---
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function throttle(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// --- NAVEGACIÓN Y FILTROS ---
function toggleMenu() {
    if (!DOM.menuCategorias || !DOM.iconToggle || !DOM.btnToggle) return;

    const isHidden = DOM.menuCategorias.classList.contains('hidden');
    
    DOM.menuCategorias.classList.toggle('hidden');
    DOM.iconToggle.classList.toggle('rotate-180');
    DOM.btnToggle.setAttribute('aria-expanded', isHidden);
}

function filtrarAjax(event, element) {
    event.preventDefault();
    
    const url = element.getAttribute('href');
    
    if (!DOM.contenedorProductos) return;

    DOM.contenedorProductos.setAttribute('aria-busy', 'true');
    mostrarSkeleton();
    
    // Cerrar menú en móvil
    if (window.innerWidth < 768 && DOM.menuCategorias && !DOM.menuCategorias.classList.contains('hidden')) {
        DOM.menuCategorias.classList.add('hidden');
        if (DOM.iconToggle) DOM.iconToggle.classList.remove('rotate-180');
        if (DOM.btnToggle) DOM.btnToggle.setAttribute('aria-expanded', 'false');
    }

    fetch(url, {
        headers: { "X-Requested-With": "XMLHttpRequest" }
    })
    .then(response => {
        if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);
        return response.text();
    })
    .then(html => {
        DOM.contenedorProductos.innerHTML = html;
        DOM.contenedorProductos.style.opacity = '1';
        DOM.contenedorProductos.setAttribute('aria-busy', 'false');
        
        // Scroll suave en móvil
        if (window.innerWidth < 768) {
            requestAnimationFrame(() => {
                DOM.contenedorProductos.scrollIntoView({ behavior: 'smooth', block: 'start' });
            });
        }
        
        window.history.pushState({}, '', url);
        actualizarBotonesActivos(element);
    })
    .catch(err => {
        console.error("Error:", err);
        mostrarError();
        DOM.contenedorProductos.setAttribute('aria-busy', 'false');
    });
}

function mostrarSkeleton() {
    const template = document.getElementById('skeleton-template');
    if (!DOM.contenedorProductos || !template) return;
    
    DOM.contenedorProductos.style.opacity = '0';
    
    requestAnimationFrame(() => {
        DOM.contenedorProductos.innerHTML = template.innerHTML;
        requestAnimationFrame(() => {
            DOM.contenedorProductos.style.opacity = '1';
        });
    });
}

function mostrarError() {
    const template = document.getElementById('error-template');
    if (!DOM.contenedorProductos || !template) return;
    
    DOM.contenedorProductos.innerHTML = template.innerHTML;
    DOM.contenedorProductos.style.opacity = '1';
}

function actualizarBotonesActivos(elementoActivo) {
    const botones = document.querySelectorAll('#filtro-nav a');
    
    botones.forEach(a => {
        a.className = 'text-slate-600 hover:bg-gray-50 px-5 py-3 rounded-2xl transition-all hover:translate-x-1';
        a.setAttribute('aria-current', 'false');
    });
    
    if (elementoActivo) {
        elementoActivo.className = 'bg-blue-600 text-white px-5 py-3 rounded-2xl font-bold shadow-lg shadow-blue-200 transition-all';
        elementoActivo.setAttribute('aria-current', 'page');
    }
}

// --- MODAL DE DETALLES ---
function verDetalles(id, nombre, precio, imagen, marca, categoria, descripcion) {
    if (window.event) window.event.stopPropagation();
    
    if (!DOM.modalDetalles) return;
    
    const elements = {
        titulo: DOM.modalDetalles.querySelector('#modal-titulo'),
        precio: DOM.modalDetalles.querySelector('#modal-precio'),
        img: DOM.modalDetalles.querySelector('#modal-img'),
        marca: DOM.modalDetalles.querySelector('#modal-marca'),
        categoria: DOM.modalDetalles.querySelector('#modal-categoria'),
        descripcion: DOM.modalDetalles.querySelector('#modal-descripcion'),
        btnAgregar: DOM.modalDetalles.querySelector('#modal-btn-agregar')
    };
    
    if (elements.titulo) elements.titulo.textContent = nombre;
    if (elements.precio) elements.precio.textContent = 'S/ ' + parseFloat(precio).toFixed(2);
    if (elements.img) {
        elements.img.src = imagen;
        elements.img.alt = nombre;
    }
    if (elements.marca) elements.marca.textContent = marca;
    if (elements.categoria) elements.categoria.textContent = categoria;
    if (elements.descripcion) {
        elements.descripcion.textContent = descripcion || "No hay descripción disponible.";
    }
    
    if (elements.btnAgregar) {
        elements.btnAgregar.onclick = () => {
            agregarAlCarrito(id, nombre, precio, imagen);
            cerrarModal();
        };
    }

    DOM.modalDetalles.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function cerrarModal() {
    if (!DOM.modalDetalles) return;
    
    DOM.modalDetalles.classList.add('hidden');
    document.body.style.overflow = '';
}

// --- LÓGICA DEL CARRITO ---
function toggleCart() {
    if (!DOM.cartSidebar || !DOM.cartOverlay) return;

    const isOpening = DOM.cartSidebar.classList.contains('translate-x-full');
    
    DOM.cartSidebar.classList.toggle('translate-x-full');
    DOM.cartOverlay.classList.toggle('hidden');
    
    requestAnimationFrame(() => {
        DOM.cartOverlay.classList.toggle('opacity-0');
    });
    
    if (isOpening) {
        renderizarCarrito();
        document.body.style.overflow = 'hidden';
    } else {
        document.body.style.overflow = '';
    }
}

function agregarAlCarrito(id, nombre, precio, imagen) {
    if (window.event) window.event.stopPropagation();
    
    const itemExistente = carrito.find(p => p.id === id);
    
    if (itemExistente) {
        itemExistente.cantidad += 1;
    } else {
        carrito.push({ id, nombre, precio: parseFloat(precio), imagen, cantidad: 1 });
    }
    
    localStorage.setItem('carrito', JSON.stringify(carrito));
    actualizarContadores();
    
    // Feedback visual
    const btn = window.event?.currentTarget;
    
    if (btn?.tagName === 'BUTTON') {
        const iconOriginal = btn.innerHTML;
        btn.disabled = true;
        
        btn.innerHTML = `
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/>
            </svg>
            <span>¡Listo!</span>
        `;
        btn.style.backgroundColor = '#10b981';
        
        setTimeout(() => {
            btn.innerHTML = iconOriginal;
            btn.style.backgroundColor = '';
            btn.disabled = false;
        }, 1200);
    }
    
    mostrarNotificacion(`✓ Agregado al carrito`);
}

function cambiarCantidad(productId, delta) {
    const item = carrito.find(p => p.id === productId);
    if (!item) return;
    
    item.cantidad += delta;
    
    if (item.cantidad <= 0) {
        if (confirm("¿Eliminar este producto?")) {
            const index = carrito.findIndex(p => p.id === productId);
            carrito.splice(index, 1);
        } else {
            item.cantidad = 1;
        }
    }
    
    localStorage.setItem('carrito', JSON.stringify(carrito));
    actualizarProductoEnCarrito(productId);
    actualizarContadores();
}

function actualizarProductoEnCarrito(productId) {
    const item = carrito.find(p => p.id === productId);
    
    // Si fue eliminado, render completo
    if (!item) {
        renderizarCarrito();
        return;
    }
    
    const productElement = DOM.cartItemsContainer.querySelector(`[data-product-id="${productId}"]`);
    
    if (!productElement) {
        renderizarCarrito();
        return;
    }
    
    const subtotal = item.precio * item.cantidad;
    
    // Actualizar cantidad y subtotal del producto
    const cantidadSpan = productElement.querySelector('span.text-sm.font-black');
    const subtotalSpan = productElement.querySelector('.flex-1 > div > span.font-black');
    
    if (cantidadSpan) cantidadSpan.textContent = item.cantidad;
    if (subtotalSpan) subtotalSpan.textContent = `S/ ${subtotal.toFixed(2)}`;
    
    // Actualizar total general
    const total = carrito.reduce((sum, p) => sum + (p.precio * p.cantidad), 0);
    if (DOM.cartTotalPrice) DOM.cartTotalPrice.textContent = `S/ ${total.toFixed(2)}`;
}

function renderizarCarrito() {
    if (!DOM.cartItemsContainer || !DOM.cartTotalPrice) return;

    if (carrito.length === 0) {
        DOM.cartItemsContainer.innerHTML = `
            <div class="flex flex-col items-center justify-center py-16 text-center opacity-50">
                <svg class="w-24 h-24 mb-4 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
                </svg>
                <p class="text-slate-400 font-medium">Carrito vacío</p>
            </div>
        `;
        DOM.cartTotalPrice.textContent = "S/ 0.00";
        return;
    }

    const fragment = document.createDocumentFragment();
    let total = 0;
    
    carrito.forEach((item) => {
        const subtotal = item.precio * item.cantidad;
        total += subtotal;
        
        const div = document.createElement('div');
        div.className = 'flex gap-4 border-b border-gray-100 pb-4 items-start';
        div.setAttribute('data-product-id', item.id);
        div.innerHTML = `
            <img src="${item.imagen}" 
                 alt="${item.nombre}"
                 loading="lazy"
                 class="w-20 h-20 object-contain rounded-xl bg-gray-50 p-2 border border-gray-100"
                 onerror="this.src='/img/placeholder.png'">
            <div class="flex-1 min-w-0">
                <h4 class="text-sm font-bold text-slate-800 mb-1 line-clamp-2">${item.nombre}</h4>
                <p class="text-xs text-slate-400 mb-3">S/ ${item.precio.toFixed(2)} c/u</p>
                <div class="flex justify-between items-center">
                    <div class="flex items-center border border-gray-200 rounded-lg bg-white shadow-sm">
                        <button onclick="cambiarCantidad('${item.id}', -1)" 
                                class="px-3 py-1.5 hover:bg-gray-100 transition-colors font-bold text-slate-600"
                                aria-label="Disminuir cantidad">
                            −
                        </button>
                        <span class="px-4 py-1.5 text-sm font-black text-slate-900 min-w-[2.5rem] text-center">${item.cantidad}</span>
                        <button onclick="cambiarCantidad('${item.id}', 1)" 
                                class="px-3 py-1.5 hover:bg-gray-100 transition-colors font-bold text-slate-600"
                                aria-label="Aumentar cantidad">
                            +
                        </button>
                    </div>
                    <span class="font-black text-slate-900">S/ ${subtotal.toFixed(2)}</span>
                </div>
            </div>
        `;
        
        fragment.appendChild(div);
    });
    
    DOM.cartItemsContainer.innerHTML = '';
    DOM.cartItemsContainer.appendChild(fragment);
    DOM.cartTotalPrice.textContent = `S/ ${total.toFixed(2)}`;
}

function limpiarCarrito() {
    // Si ya está vacío, no hacer nada
    if (carrito.length === 0) {
        mostrarNotificacion("El carrito ya está vacío");
        return;
    }
    
    if (!confirm("¿Vaciar el carrito?")) return;
    
    carrito = [];
    localStorage.removeItem('carrito');
    actualizarContadores();
    renderizarCarrito();
    mostrarNotificacion("Carrito vaciado");
}

// --- UTILIDADES ---
function actualizarContadores() {
    const totalItems = carrito.reduce((acc, p) => acc + p.cantidad, 0);
    
    if (DOM.contadorNavbar) DOM.contadorNavbar.textContent = totalItems;
    if (DOM.contadorSidebar) DOM.contadorSidebar.textContent = totalItems;
}

let notificationTimeout;
function mostrarNotificacion(mensaje) {
    clearTimeout(notificationTimeout);
    
    let toast = document.getElementById('toast-notification');
    
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast-notification';
        toast.className = 'fixed top-24 right-5 bg-slate-900 text-white px-5 py-3 rounded-xl shadow-xl z-[100] transition-all duration-300 translate-x-[500px]';
        document.body.appendChild(toast);
    }
    
    toast.textContent = mensaje;
    
    requestAnimationFrame(() => {
        toast.style.transform = 'translateX(0)';
    });
    
    notificationTimeout = setTimeout(() => {
        toast.style.transform = 'translateX(500px)';
    }, 2000);
}

// --- EVENTOS GLOBALES ---
const handleResize = debounce(() => {
    if (window.innerWidth >= 768) {
        document.body.style.overflow = '';
        if (DOM.menuCategorias) DOM.menuCategorias.classList.remove('hidden');
    }
}, 250);

window.addEventListener('resize', handleResize);
window.addEventListener('popstate', () => location.reload());

document.addEventListener('keydown', (e) => {
    if (e.key !== 'Escape') return;
    
    if (DOM.modalDetalles && !DOM.modalDetalles.classList.contains('hidden')) {
        cerrarModal();
    }
    
    if (DOM.cartSidebar && !DOM.cartSidebar.classList.contains('translate-x-full')) {
        toggleCart();
    }
});

// --- INICIALIZACIÓN ---
document.addEventListener('DOMContentLoaded', () => {
    DOM.init();
    actualizarContadores();
    
    if (DOM.cartOverlay) {
        DOM.cartOverlay.addEventListener('click', toggleCart);
    }
    
    document.addEventListener('click', throttle((event) => {
        if (window.innerWidth >= 768 || !DOM.menuCategorias || !DOM.btnToggle) return;
        
        const clickedOutside = !DOM.menuCategorias.contains(event.target) && 
                               !DOM.btnToggle.contains(event.target);
        const menuIsOpen = !DOM.menuCategorias.classList.contains('hidden');
        
        if (clickedOutside && menuIsOpen) toggleMenu();
    }, 100));
    
    console.log('✓ Sistema optimizado cargado');
});