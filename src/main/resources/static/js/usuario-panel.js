const perfilContent = document.getElementById('perfil');
const pedidosContent = document.getElementById('pedidos');

const btnPerfil = document.getElementById('btn-perfil');
const btnPedidos = document.getElementById('btn-pedidos');

function resetButtonStyles(button) {
  button.style.background = 'transparent';
  button.style.color = '#1e3a8a';
  button.style.boxShadow = 'none';
}

function setActiveButtonStyles(button) {
  button.style.background = 'white';
  button.style.color = '#2563eb';
  button.style.boxShadow = '0 4px 10px rgba(0,0,0,0.1)';
}

// Inicializa estado inicial
function initTabs() {
  perfilContent.hidden = false;
  pedidosContent.hidden = true;

  setActiveButtonStyles(btnPerfil);
  resetButtonStyles(btnPedidos);
}

// Cambiar pestañas
function changeTab(tabId) {
  if (tabId === 'perfil') {
    perfilContent.hidden = false;
    pedidosContent.hidden = true;

    setActiveButtonStyles(btnPerfil);
    resetButtonStyles(btnPedidos);
  } else if (tabId === 'pedidos') {
    perfilContent.hidden = true;
    pedidosContent.hidden = false;

    resetButtonStyles(btnPerfil);
    setActiveButtonStyles(btnPedidos);
  }
}

// Ejecutar inicialización al cargar la página
window.onload = initTabs;
