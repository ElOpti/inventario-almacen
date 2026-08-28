package com.almacen.inventario.service;

import com.almacen.inventario.dto.UsuarioDto;
import com.almacen.inventario.model.Rol;
import com.almacen.inventario.model.Usuario;
import com.almacen.inventario.repository.RolRepository;
import com.almacen.inventario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    public Usuario crearUsuario(UsuarioDto dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername().trim())) {
            throw new IllegalArgumentException("El nombre de usuario '" + dto.getUsername().trim() + "' ya se encuentra registrado.");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }

        Rol rol = null;
        if (dto.getRolId() != null) {
            rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + dto.getRolId()));
        } else {
            rol = rolRepository.findByNombre("ROLE_ALMACENISTA")
                    .orElseThrow(() -> new IllegalArgumentException("Rol por defecto no configurado en el sistema."));
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername().trim().toLowerCase());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setRol(rol);
        usuario.setActivo(dto.isActivo());
        usuario.setFechaRegistro(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, UsuarioDto dto) {
        Usuario usuario = obtenerPorId(id);

        Usuario existente = usuarioRepository.findByUsername(dto.getUsername().trim().toLowerCase()).orElse(null);
        if (existente != null && !existente.getId().equals(id)) {
            throw new IllegalArgumentException("El nombre de usuario '" + dto.getUsername().trim() + "' ya está en uso por otra cuenta.");
        }

        if (dto.getRolId() != null) {
            Rol rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + dto.getRolId()));
            usuario.setRol(rol);
        }

        usuario.setUsername(dto.getUsername().trim().toLowerCase());
        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setActivo(dto.isActivo());

        // Si se proporcionó una nueva contraseña, actualizarla
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            if (dto.getPassword().trim().length() < 4) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 4 caracteres.");
            }
            usuario.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarEstado(Long id, boolean nuevoEstado, String currentUsername) {
        Usuario usuario = obtenerPorId(id);

        if (usuario.getUsername().equalsIgnoreCase(currentUsername) && !nuevoEstado) {
            throw new IllegalArgumentException("No es posible desactivar su propia cuenta de Administrador mientras está en sesión activa.");
        }

        usuario.setActivo(nuevoEstado);
        return usuarioRepository.save(usuario);
    }
}
