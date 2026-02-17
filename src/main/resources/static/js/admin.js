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