// ==================== CONFIGURACIÓN ====================
const DOM = {
    init() {
        Object.assign(this, {
            menuCategorias: document.getElementById('menu-categorias'),
            iconToggle: document.getElementById('icon-toggle'),
            btnToggle: document.getElementById('btn-toggle-menu'),
            gridProductos: document.getElementById('grid-productos'),
            cartSidebar: document.getElementById('cart-sidebar'),
            cartOverlay: document.getElementById('cart-overlay'),
            cartItemsContainer: document.getElementById('cart-items-container'),
            cartTotalPrice: document.getElementById('cart-total-price'),
            modalDetalles: document.getElementById('modal-detalles'),
            contadorNavbar: document.getElementById('carrito-count'),
            contadorSidebar: document.getElementById('cart-items-count')
        });
    }
};

let carrito = JSON.parse(localStorage.getItem('carrito')) || [];
let todosLosProductos = [];
let categoriaSeleccionada = 'todos';

const debounce = (func, wait) => {
    let timeout;
    return (...args) => {
        clearTimeout(timeout);
        timeout = setTimeout(() => func(...args), wait);
    };
};

const $ = (sel) => document.querySelector(sel);
const $$ = (sel) => document.querySelectorAll(sel);

// ==================== FILTROS ====================

// ==================== NAVEGACIÓN ====================
function toggleMenu() {
    if (!DOM.menuCategorias) return;
    const isHidden = DOM.menuCategorias.classList.toggle('hidden');
    DOM.iconToggle?.classList.toggle('rotate-180');
    DOM.btnToggle?.setAttribute('aria-expanded', !isHidden);
}

// Filtrar por categoría (clic en menú)
function filtrarAjax(event, element) {
    event.preventDefault();
    const idCategoria = element.dataset.id;
    const url = element.getAttribute('href');

    if (window.innerWidth < 768 && DOM.menuCategorias && !DOM.menuCategorias.classList.contains('hidden')) {
        DOM.menuCategorias.classList.add('hidden');
        DOM.iconToggle?.classList.remove('rotate-180');
        DOM.btnToggle?.setAttribute('aria-expanded', 'false');
    }

    categoriaSeleccionada = idCategoria; // ✅ actualizar categoría global
    actualizarBotonesActivos(element);
    window.history.pushState({}, '', url);

    aplicarFiltros(); // ✅ aplica filtros combinados
}


// Actualiza el botón activo en el menú
function actualizarBotonesActivos(activo) {
    $$('#filtro-nav a').forEach(a => {
        a.className = 'text-slate-600 hover:bg-gray-50 px-5 py-3 rounded-2xl transition-all hover:translate-x-1';
        a.setAttribute('aria-current', 'false');
    });

    if (activo) {
        activo.className = 'bg-blue-600 text-white px-5 py-3 rounded-2xl font-bold shadow-lg shadow-blue-200 transition-all';
        activo.setAttribute('aria-current', 'page');
    }
}

// Actualiza el label del precio
function actualizarPrecioLabel(valor) {
    const el = $('#price-val');
    if (el) el.innerText = 'S/ ' + valor;
}

function aplicarFiltros() {
    // 1. Obtenemos los valores de los inputs
    const texto = document.getElementById('search-input')?.value || '';
    const precioMax = parseFloat(document.getElementById('price-range')?.value || 99999);

    const normalizar = str => str
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLowerCase();

    const filtrados = todosLosProductos.filter(p => {

        const nombreNormalizado = normalizar(p.nombre || '');
        const textoNormalizado = normalizar(texto);

        const dentroDeNombre = nombreNormalizado.includes(textoNormalizado);
        const dentroDePrecio = p.precio <= precioMax;
        const dentroDeCategoria = categoriaSeleccionada === 'todos' 
            ? true 
            : String(p.categoria?.idCategoria) === categoriaSeleccionada;

        return dentroDeNombre && dentroDePrecio && dentroDeCategoria;
    });

    renderizarCards(filtrados);
}

// ==================== INICIALIZACIÓN ====================
document.addEventListener('DOMContentLoaded', async () => {
    DOM.init();
    actualizarContadores();

    try {
        // Cargar todos los productos
        const response = await fetch('/api/productos');
        todosLosProductos = await response.json();

        // Detectar si Thymeleaf ya renderizó productos
        const grid = DOM.gridProductos;
        const articulosExistentes = grid ? grid.querySelectorAll('article') : [];

        if (articulosExistentes.length > 0) {
            // Agregar eventos a las cards existentes
            agregarEventosACardsExistentes(articulosExistentes);
        } else {
            // Renderizar todos los productos desde JS
            renderizarCards(todosLosProductos);
        }

        // ==================== FILTRO INICIAL SI HAY CATEGORÍA EN URL ====================
        const params = new URLSearchParams(window.location.search);
        const idCat = params.get('idCat');
        if (idCat) {
            categoriaSeleccionada = idCat;
            const boton = $(`#filtro-nav a[data-id="${idCat}"]`);
            if (boton) actualizarBotonesActivos(boton);
            filtrarProductos();
        }

    } catch (error) {
        console.error('Error al cargar productos:', error);
        if (DOM.gridProductos) {
            DOM.gridProductos.innerHTML = `
                <div class="col-span-full py-16 text-center">
                    <h3 class="text-xl font-bold text-red-500 mb-2">Error al cargar productos</h3>
                    <button onclick="location.reload()" class="bg-blue-600 text-white px-6 py-3 rounded-xl font-bold hover:bg-blue-700">Recargar</button>
                </div>`;
        }
    }

    DOM.cartOverlay?.addEventListener('click', toggleCart);
});
// ==================== RESET DE FILTROS ====================
function resetearFiltros() {
    categoriaSeleccionada = 'todos';

    const inputBusqueda = $('#search-input');
    const inputPrecio = $('#price-range');

    if (inputBusqueda) inputBusqueda.value = '';
    if (inputPrecio) inputPrecio.value = inputPrecio.max || 99999;

    actualizarPrecioLabel(inputPrecio?.value || 99999);

    actualizarBotonesActivos($('#filtro-nav a[data-id="todos"]'));

    aplicarFiltros(); // usar aplicarFiltros en lugar de renderizarCards directo
}
// ==================== RENDERIZADO ====================
function renderizarCards(lista) {
    const contenedor = DOM.gridProductos;
    const template = $('#producto-template');

    if (!contenedor || !template) return;

	if (lista.length === 0) {
	    contenedor.innerHTML = `
	        <div class="col-span-full py-16 md:py-24 text-center">
	            <h3 class="text-xl md:text-2xl font-bold text-slate-400 mb-2">No hay productos aquí</h3>
	            <button 
	                class="text-blue-600 font-bold hover:underline" 
	                onclick="resetearFiltros()">
	                Ver todos los productos
	            </button>
	        </div>`;
	    return;
	}

    const fragment = document.createDocumentFragment();

    lista.forEach((p, i) => {
        const clon = template.content.cloneNode(true);
        const card = clon.querySelector('.producto-card');

        // AOS delay escalonado
        card.setAttribute('data-aos-delay', i * 50);

        const imagen = p.imagenes?.find(i => i.principal)?.urlImagen 
            || p.imagenes?.[0]?.urlImagen 
            || '/img/placeholder.png';

        const img = clon.querySelector('.producto-img');
        const nombre = clon.querySelector('.producto-nombre');
        const precio = clon.querySelector('.producto-precio');
        const categoria = clon.querySelector('.producto-categoria');
        const marca = clon.querySelector('.producto-marca');

        img.src = imagen;
        img.alt = p.nombre;
        nombre.textContent = p.nombre;
        precio.textContent = 'S/ ' + p.precio.toFixed(2);
        categoria.textContent = p.categoria?.nombre ?? '';
        marca.textContent = p.marca ?? '';

        const abrirDetalles = (e) => {
            e.stopPropagation();
            verDetalles(p.idProducto, p.nombre, p.precio, imagen, p.marca, p.categoria?.nombre ?? '', p.descripcion ?? '');
        };

        card.addEventListener('click', abrirDetalles);
        clon.querySelector('.producto-btn-vista').addEventListener('click', abrirDetalles);
        clon.querySelector('.producto-btn-carrito').addEventListener('click', e => 
            agregarAlCarrito(e, p.idProducto, p.nombre, p.precio, imagen)
        );

        fragment.appendChild(clon);
    });

    contenedor.innerHTML = '';
    contenedor.appendChild(fragment);

    // Re-inicializar AOS para nuevas cards
    if (typeof AOS !== 'undefined') AOS.refresh();
}

// ==================== MODAL ====================
function verDetalles(id, nombre, precio, imagen, marca, categoria, descripcion) {
    if (!DOM.modalDetalles) return;
    
    $('#modal-titulo').textContent = nombre;
    $('#modal-precio').textContent = 'S/ ' + parseFloat(precio).toFixed(2);
    $('#modal-img').src = imagen;
    $('#modal-img').alt = nombre;
    $('#modal-marca').textContent = marca || '';
    $('#modal-categoria').textContent = categoria || '';
    $('#modal-descripcion').textContent = descripcion || 'No hay descripción disponible.';

    const btnAgregar = $('#modal-btn-agregar');
    if (btnAgregar) {
        btnAgregar.onclick = () => {
            agregarAlCarrito(null, id, nombre, precio, imagen);
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

// ==================== CARRITO ====================
function toggleCart() {
    if (!DOM.cartSidebar || !DOM.cartOverlay) return;

    const isOpening = DOM.cartSidebar.classList.toggle('translate-x-full');
    DOM.cartOverlay.classList.toggle('hidden');
    
    requestAnimationFrame(() => DOM.cartOverlay.classList.toggle('opacity-0'));
    
    document.body.style.overflow = isOpening ? '' : 'hidden';
    if (!isOpening) renderizarCarrito();
}

function agregarAlCarrito(e, id, nombre, precio, imagen) {
    if (e) e.stopPropagation();

    const item = carrito.find(p => p.id === id);
    
    if (item) {
        item.cantidad++;
    } else {
        carrito.push({
            id, nombre,
            precio: parseFloat(precio),
            imagen: imagen || '/img/placeholder.png',
            cantidad: 1
        });
    }

    localStorage.setItem('carrito', JSON.stringify(carrito));
    actualizarContadores();

    // Feedback visual
    const btn = e?.currentTarget;
    if (btn?.tagName === 'BUTTON') {
        const original = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg><span>¡Listo!</span>';
        btn.style.backgroundColor = '#10b981';

        setTimeout(() => {
            btn.innerHTML = original;
            btn.style.backgroundColor = '';
            btn.disabled = false;
        }, 1200);
    }

    mostrarNotificacion('✓ Agregado al carrito');
}

function cambiarCantidad(productId, delta) {
    const item = carrito.find(p => p.id === productId);
    if (!item) return;
    
    item.cantidad += delta;
    
    if (item.cantidad <= 0) {
        if (confirm('¿Eliminar este producto?')) {
            carrito = carrito.filter(p => p.id !== productId);
        } else {
            item.cantidad = 1;
        }
    }
    
    localStorage.setItem('carrito', JSON.stringify(carrito));
    renderizarCarrito();
    actualizarContadores();
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
            </div>`;
        DOM.cartTotalPrice.textContent = 'S/ 0.00';
        return;
    }

    const fragment = document.createDocumentFragment();
    let total = 0;
    
    carrito.forEach(item => {
        const subtotal = item.precio * item.cantidad;
        total += subtotal;
        
        const div = document.createElement('div');
        div.className = 'flex gap-4 border-b border-gray-100 pb-4 items-start';
        div.innerHTML = `
            <img src="${item.imagen}" alt="${item.nombre}" loading="lazy"
                 class="w-20 h-20 object-contain rounded-xl bg-gray-50 p-2 border border-gray-100"
                 onerror="this.src='/img/placeholder.png'">
            <div class="flex-1 min-w-0">
                <h4 class="text-sm font-bold text-slate-800 mb-1 line-clamp-2">${item.nombre}</h4>
                <p class="text-xs text-slate-400 mb-3">S/ ${item.precio.toFixed(2)} c/u</p>
                <div class="flex justify-between items-center">
                    <div class="flex items-center border border-gray-200 rounded-lg bg-white shadow-sm">
                        <button class="btn-restar px-3 py-1.5 hover:bg-gray-100 font-bold text-slate-600">−</button>
                        <span class="px-4 py-1.5 text-sm font-black text-slate-900 min-w-[2.5rem] text-center">${item.cantidad}</span>
                        <button class="btn-sumar px-3 py-1.5 hover:bg-gray-100 font-bold text-slate-600">+</button>
                    </div>
                    <span class="font-black text-slate-900">S/ ${subtotal.toFixed(2)}</span>
                </div>
            </div>`;
        
        div.querySelector('.btn-restar').onclick = () => cambiarCantidad(item.id, -1);
        div.querySelector('.btn-sumar').onclick = () => cambiarCantidad(item.id, 1);
        
        fragment.appendChild(div);
    });
    
    DOM.cartItemsContainer.innerHTML = '';
    DOM.cartItemsContainer.appendChild(fragment);
    DOM.cartTotalPrice.textContent = `S/ ${total.toFixed(2)}`;
}

function limpiarCarrito() {
    if (carrito.length === 0) return mostrarNotificacion('El carrito ya está vacío');
    if (!confirm('¿Vaciar el carrito?')) return;
    
    carrito = [];
    localStorage.removeItem('carrito');
    actualizarContadores();
    renderizarCarrito();
    mostrarNotificacion('Carrito vaciado');
}

function actualizarContadores() {
    const total = carrito.reduce((acc, p) => acc + p.cantidad, 0);
    if (DOM.contadorNavbar) DOM.contadorNavbar.textContent = total;
    if (DOM.contadorSidebar) DOM.contadorSidebar.textContent = total;
}

// ==================== PAYPAL ====================
async function verificarYMostrarPaypal() {
    if (carrito.length === 0) return alert('Tu carrito está vacío.');

    try {
        const response = await fetch('/api/ventas/check-auth', {
            headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
        });

        if (response.ok) {
            $('#btn-pre-finalizar').classList.add('hidden');
            $('#paypal-button-container').classList.remove('hidden');
            renderizarBotonPaypal();
        } else if (response.status === 401) {
            if(confirm('Debes iniciar sesión. ¿Ir al login?')) {
                window.location.href = '/login';
            }
        } else {
            alert('No se pudo verificar la sesión.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión.');
    }
}

function renderizarBotonPaypal() {
    if ($('#paypal-button-container').children.length > 0) return;

    paypal.Buttons({
        style: { layout: 'vertical', color: 'blue', shape: 'rect' },
        createOrder: (data, actions) => {
            // Obtenemos el total del DOM
            const total = $('#cart-total-price').innerText.replace('S/ ', '').trim();
            return actions.order.create({ 
                purchase_units: [{ amount: { value: total } }] 
            });
        },
        onApprove: function(data, actions) {
            return actions.order.capture().then(function(details) {
                // LLAMADA CLAVE: Enviamos el ID de la orden de PayPal a nuestra función de backend
                finalizarVentaBackend(details.id);
            });
        },
        onError: function(err) {
            console.error('Error en PayPal:', err);
            alert('Ocurrió un error con el pago de PayPal.');
        }
    }).render('#paypal-button-container');
}

function finalizarVentaBackend(orderId) {
    fetch('/api/ventas/finalizar', { 
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            orderId: orderId,
            items: carrito.map(item => ({ 
                id: item.id, 
                cantidad: item.cantidad, 
                precio: item.precio 
            }))
        })
    })
	.then(async response => {
		const data = await response.json();

		   if (!response.ok) {
		       throw new Error(
		           data.error || data.mensaje || 'Error al procesar la venta'
		       );
		   }

		   return data;
	})
    .then(data => {
        // --- 1. LIMPIEZA ---
        carrito = [];
        localStorage.removeItem('carrito');
        actualizarContadores();
        renderizarCarrito();

        // --- 2. NOTIFICACIÓN CON BOTÓN ---
        // Reemplazamos el alert/toast simple por la versión con botón de acción
		const urlPedidos = new URL('/usuario/panel', window.location.origin);
		urlPedidos.searchParams.set('tab', 'pedidos');

		mostrarNotificacion(
		    data.mensaje,
		    urlPedidos.toString(),
		    'Ver mis pedidos'
		);

        // --- 3. REDIRECCIÓN DE SEGURIDAD (OPCIONAL) ---
        // Si el usuario no hace clic, lo llevamos suavemente a la tienda tras 8 segundos
		setTimeout(() => {
		    if (window.location.pathname.includes('/productos')) {
		        window.location.href = '/usuario/panel?tab=pedidos';
		    }
		}, 8000);
    })
	.catch(err => {
	    console.error("Error:", err);

	    mostrarNotificacion('❌ ' + err.message); 
	});
}

// ==================== NOTIFICACIONES ====================
let notificationTimeout;

function mostrarNotificacion(mensaje, urlAccion = null, textoAccion = null) {
    clearTimeout(notificationTimeout);
    
    let toast = document.getElementById('toast-notification');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast-notification';
        // z-[9999] para que siempre esté por encima de todo
        toast.className = 'fixed top-24 right-5 bg-slate-900 text-white px-6 py-4 rounded-xl shadow-2xl z-[9999] transition-all duration-500 translate-x-[600px] border border-slate-700 min-w-[280px]';
        document.body.appendChild(toast);
    }
    
    // Si hay una acción (URL), creamos la estructura con botón, si no, solo texto
    if (urlAccion && textoAccion) {
        toast.innerHTML = `
            <div class="flex flex-col gap-3">
                <div class="flex items-center gap-2">
                    <p class="font-medium">${mensaje}</p>
                </div>
                <a href="${urlAccion}" class="bg-indigo-500 hover:bg-indigo-600 text-white text-center text-xs font-bold py-2 px-4 rounded-lg transition-colors no-underline">
                    ${textoAccion}
                </a>
            </div>
        `;
    } else {
        // Mensaje simple (para "Agregado al carrito")
        toast.innerHTML = `<p class="font-medium text-sm">${mensaje}</p>`;
    }
    
    // Animación de entrada
    requestAnimationFrame(() => {
        toast.style.transform = 'translateX(0)';
    });

    // Duración: Si tiene botón, 8 segundos. Si es simple, 2.5 segundos.
    const duracion = urlAccion ? 8000 : 2500;

    notificationTimeout = setTimeout(() => {
        toast.style.transform = 'translateX(600px)';
    }, duracion);
}

// ==================== EVENTOS PARA CARDS RENDERIZADAS POR THYMELEAF ====================
function agregarEventosACardsExistentes(articles) {
    articles.forEach(card => {
        // Extraer datos del DOM (datos que Thymeleaf ya puso)
        const nombre = card.querySelector('.producto-nombre')?.textContent || '';
        const precioText = card.querySelector('.producto-precio')?.textContent || 'S/ 0';
        const precio = parseFloat(precioText.replace('S/ ', '').replace(',', ''));
        const imagen = card.querySelector('.producto-img')?.src || '/img/placeholder.png';
        const marca = card.querySelector('.producto-marca')?.textContent || '';
        const categoria = card.querySelector('.producto-categoria')?.textContent || '';
        
        // Buscar el producto completo en todosLosProductos para obtener el ID
        const productoCompleto = todosLosProductos.find(p => 
            p.nombre === nombre && Math.abs(p.precio - precio) < 0.01
        );
        
        if (!productoCompleto) {
            console.warn('No se encontró producto completo para:', nombre);
            return;
        }
        
        const id = productoCompleto.idProducto;
        const descripcion = productoCompleto.descripcion || '';
        
        // Agregar eventos de clic
        const abrirDetalles = (e) => {
            e.stopPropagation();
            verDetalles(id, nombre, precio, imagen, marca, categoria, descripcion);
        };
        
        card.addEventListener('click', abrirDetalles);
        
        const btnVista = card.querySelector('.producto-btn-vista');
        if (btnVista) {
            btnVista.addEventListener('click', abrirDetalles);
        }
        
        const btnCarrito = card.querySelector('.producto-btn-carrito');
        if (btnCarrito) {
            btnCarrito.addEventListener('click', e => 
                agregarAlCarrito(e, id, nombre, precio, imagen)
            );
        }
    });
}

// ==================== EVENTOS ====================
window.addEventListener('resize', debounce(() => {
    if (window.innerWidth >= 768) {
        document.body.style.overflow = '';
        DOM.menuCategorias?.classList.remove('hidden');
    }
}, 250));

window.addEventListener('popstate', () => location.reload());

document.addEventListener('keydown', (e) => {
    if (e.key !== 'Escape') return;
    if (DOM.modalDetalles && !DOM.modalDetalles.classList.contains('hidden')) cerrarModal();
    if (DOM.cartSidebar && !DOM.cartSidebar.classList.contains('translate-x-full')) toggleCart();
});

document.addEventListener('DOMContentLoaded', async () => {
    DOM.init();
    actualizarContadores();
    
    try {
        // Cargar todos los productos
        const response = await fetch('/api/productos');
        todosLosProductos = await response.json();
        
        // Verificar si Thymeleaf ya renderizó productos
        const grid = DOM.gridProductos;
        const articulosExistentes = grid ? grid.querySelectorAll('article') : [];
        
        if (articulosExistentes.length > 0) {
            console.log('✓ Productos renderizados por servidor (Thymeleaf):', articulosExistentes.length);
            agregarEventosACardsExistentes(articulosExistentes);
        } else {
            console.log('✓ Renderizando productos desde JavaScript');
            renderizarCards(todosLosProductos);
        }

        // Detectar categoría activa inicial
        const categoriaActiva = document.querySelector('#filtro-nav a[aria-current="page"]');
        if (categoriaActiva) {
            categoriaSeleccionada = categoriaActiva.dataset.id;
        }

        // Renderizar según categoría seleccionada
        aplicarFiltros();

        // ==================== EVENTOS DE FILTRO EN INPUTS ====================
        $('#search-input')?.addEventListener('input', aplicarFiltros);
        $('#price-range')?.addEventListener('input', aplicarFiltros);

    } catch (error) {
        console.error('Error al cargar productos:', error);
        if (DOM.gridProductos) {
            DOM.gridProductos.innerHTML = '<div class="col-span-full py-16 text-center"><h3 class="text-xl font-bold text-red-500 mb-2">Error al cargar productos</h3><button onclick="location.reload()" class="bg-blue-600 text-white px-6 py-3 rounded-xl font-bold hover:bg-blue-700">Recargar</button></div>';
        }
    }
    DOM.cartOverlay?.addEventListener('click', toggleCart);
    console.log('✓ Sistema cargado');
});