function usuariosManager() {
    return {
        openModal: false,
        usuario: {},
        isEdit: false,
        usuarioDuplicado: false,
        correoDuplicado: false,
        
        async validarCampos() {
            if (!this.usuario.nombreUsuario && !this.usuario.correo) return;
            
            const params = new URLSearchParams({
                nombreUsuario: this.usuario.nombreUsuario || '',
                correo: this.usuario.correo || '',
                idUsuario: this.usuario.idUsuario || ''
            });
            
            const { usuarioExiste, correoExiste } = await fetch(`/admin/usuarios/validar?${params}`)
                .then(res => res.json());
            
            this.usuarioDuplicado = usuarioExiste;
            this.correoDuplicado = correoExiste;
        },
        
        nuevoUsuario() {
            this.usuario = { activo: true };
            this.isEdit = false;
            this.openModal = true;
        },
        
        editarUsuario(el) {
            this.isEdit = true;
            this.usuario = {
                idUsuario: el.dataset.id,
                nombreUsuario: el.dataset.nombre,
                correo: el.dataset.correo,
                activo: el.dataset.activo === 'true',
                rolId: el.dataset.rol
            };
            this.openModal = true;
        }
    }
}
function categoriasManager(){
    return{
        openModal:false,
        isEdit:false,
        categoria:{}
    }
}
function productosManager() {
    return {
        openModal: false,
        isEdit: false,
        producto: {
            idProducto: null,
            nombre: '',
            marca: '',
            precio: '',
            categoriaId: '',
            descripcion: '', 
            urlImagen: ''
        },

        editarProducto(id, nombre, marca, precio, categoriaId, descripcion, urlImagen) {
            this.producto = {
                idProducto: id,
                nombre: nombre,
                marca: marca,
                precio: precio,
                categoriaId: categoriaId,
                descripcion: descripcion,
                urlImagen: urlImagen
            };
            this.isEdit = true;
            this.openModal = true;
        },

        nuevoProducto() {
            this.producto = {
                idProducto: null,
                nombre: '',
                marca: '',
                precio: '',
                categoriaId: '',
                descripcion: '',  // ← AGREGA ESTO
                urlImagen: ''
            };
            this.isEdit = false;
            this.openModal = true;
        }
    }
}
function inventarioManager() {
    return {
        openModal: false,
        inventario: {
            idInventario: null,
            stock: 0,
            stockMinimo: 0,
            nombreProducto: ''
        },

        editarInventario(id, stock, stockMinimo, nombreProducto) {
            this.inventario = {
                idInventario: id,
                stock: stock,
                stockMinimo: stockMinimo,
                nombreProducto: nombreProducto
            };
            this.openModal = true;
        }
    }
}
const ctx = document.getElementById('chartVentas');
if (ctx) {
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Pagado',
                    data: seriePagado,
                    borderColor: 'rgb(255, 205, 86)',
                    backgroundColor: 'rgba(255, 205, 86, 0.1)',
                    tension: 0.35,
                    fill: true,
                    borderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'En Preparación',
                    data: serieEnPreparacion,
                    borderColor: 'rgb(54, 162, 235)',
                    backgroundColor: 'rgba(54, 162, 235, 0.1)',
                    tension: 0.35,
                    fill: true,
                    borderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Enviado',
                    data: serieEnviado,
                    borderColor: 'rgb(153, 102, 255)',
                    backgroundColor: 'rgba(153, 102, 255, 0.1)',
                    tension: 0.35,
                    fill: true,
                    borderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Entregado',
                    data: serieEntregado,
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.1)',
                    tension: 0.35,
                    fill: true,
                    borderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 15,
                        font: {
                            size: 12,
                            weight: 'bold'
                        }
                    }
                },
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: {
                        size: 14,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 13
                    },
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            label += 'S/ ' + context.parsed.y.toFixed(2);
                            return label;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return 'S/ ' + value.toFixed(0);
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
}