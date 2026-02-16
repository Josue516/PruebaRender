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