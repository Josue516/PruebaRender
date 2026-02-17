function abrirModal(btn) {
        const id = btn.dataset.id;
        
        // Hacer petición al servidor para obtener los detalles
        fetch(`/gestor/pedidos/${id}/detalles`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cargar los detalles');
                }
                return response.json();
            })
            .then(data => {
                // Llenar información general
                document.getElementById('modalIdPedido').textContent = `#${data.idVenta}`;
                document.getElementById('modalFecha').textContent = data.fechaVenta;
                document.getElementById('modalCliente').textContent = data.cliente.nombre;
                document.getElementById('modalCorreo').textContent = data.cliente.correo;
                document.getElementById('modalTotal').textContent = `$${parseFloat(data.total).toFixed(2)}`;
                
                // Estado con color
                const modalEstado = document.getElementById('modalEstado');
                modalEstado.textContent = data.estado;
                modalEstado.className = getEstadoClasses(data.estado) + ' px-3 py-1 rounded-full text-sm font-semibold inline-block';
                
                // Productos
                const productosHtml = data.productos.map(prod => `
                    <div class="flex justify-between items-center bg-white p-4 rounded-lg border border-gray-200 hover:border-blue-300 transition">
                        <div class="flex-1">
                            <p class="font-semibold text-gray-900 mb-1">${prod.nombre}</p>
                            <p class="text-sm text-gray-600">
                                <span class="font-medium">Cantidad:</span> ${prod.cantidad} × 
                                <span class="font-medium">$${parseFloat(prod.precioUnitario).toFixed(2)}</span>
                            </p>
                        </div>
                        <div class="text-right">
                            <p class="text-lg font-bold text-blue-600">$${parseFloat(prod.subtotal).toFixed(2)}</p>
                        </div>
                    </div>
                `).join('');
                
                document.getElementById('modalProductos').innerHTML = productosHtml;
                
                // Mostrar modal
                document.getElementById('modalDetalle').classList.remove('hidden');
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al cargar los detalles del pedido');
            });
			const modal = document.getElementById('modalDetalle');
			    modal.classList.remove('hidden');
			    
			    // Bloqueamos el scroll del fondo (la tabla de pedidos)
			    document.body.style.overflow = 'hidden';
    }

    function cerrarModal() {
        document.getElementById('modalDetalle').classList.add('hidden');
		const modal = document.getElementById('modalDetalle');
		    modal.classList.add('hidden');
		    
		    // Devolvemos el scroll al fondo
		    document.body.style.overflow = 'auto';
    }

    function getEstadoClasses(estado) {
        const clases = {
            'PAGADO': 'bg-yellow-100 text-yellow-800',
            'EN_PREPARACION': 'bg-blue-100 text-blue-800',
            'ENVIADO': 'bg-purple-100 text-purple-800',
            'ENTREGADO': 'bg-green-100 text-green-800',
            'CANCELADO': 'bg-red-100 text-red-800'
        };
        return clases[estado] || 'bg-gray-100 text-gray-800';
    }

    // Cerrar modal al hacer clic fuera de él
    document.getElementById('modalDetalle')?.addEventListener('click', function(e) {
        if (e.target === this) {
            cerrarModal();
        }
    });

    // Cerrar modal con tecla ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            cerrarModal();
        }
    });