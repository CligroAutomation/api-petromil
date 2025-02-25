package com.example.demo.service;

import com.example.demo.Util.JwtUtils;
import com.example.demo.dao.RoleRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.domain.Role;
import com.example.demo.domain.UserEntity;
import com.example.demo.domain.dto.AuthCreatedUserRequest;
import com.example.demo.domain.dto.AuthLoginRequest;
import com.example.demo.domain.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RoleRepository roleRepository;

    // Obtiene usuario por el nombre
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + email + " no existe.")); // Busca al
                                                                                                          // usuario en
                                                                                                          // la base de
                                                                                                          // datos
                                                                                                          // mediante su
                                                                                                          // nombre de
                                                                                                          // usuario.

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>(); // Crea una lista para almacenar las autoridades
                                                                        // del usuario (roles y permisos) con
                                                                        // SimpleGrantedAuthority

        // Itera sobre los roles asociados al usuario

        // Por cada rol, crea una autoridad con el prefijo "ROLE_" seguido del nombre
        // del rol, como lo requiere Spring Security.
        userEntity.getRoles()
                .forEach(role -> authorityList
                        .add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        // Itera sobre los roles del usuario y luego extrae los permisos asociados a
        // cada rol.

        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream()) // Usa flatMap para aplanar la lista de permisos
                                                                    // dentro de los roles.
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName()))); // Por cada
                                                                                                             // permiso,
                                                                                                             // agrega
                                                                                                             // una
                                                                                                             // autoridad
                                                                                                             // a la
                                                                                                             // lista.

        // Crea y devuelve un objeto de tipo User (implementación de UserDetails de
        // Spring Security).
        return new User(userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String email = authLoginRequest.email();
        String password = authLoginRequest.password();

        try {
            // Autenticar al usuario
            Authentication authentication = this.authenticate(email, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar token de acceso
            String accessToken = jwtUtils.createToken(authentication);
            UserEntity u = userRepository.findUserEntityByEmail(email).get();

            // Obtener ownerId de manera segura
            Long ownerId = (u.getOwner() != null) ? u.getOwner().getId() : null;

            // Construir el UserDTO con ownerId
            AuthResponse.UserDTO userDTO = new AuthResponse.UserDTO(
                    u.getId(),
                    u.getEmail(),
                    u.getRoles().stream()
                            .map(role -> new AuthResponse.UserDTO.Role(role.getRoleEnum().name()))
                            .toArray(AuthResponse.UserDTO.Role[]::new),
                    ownerId // Incluir ownerId, que puede ser null
            );
            return new AuthResponse(accessToken, userDTO);
        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            return null; // Retornar null si ocurre un error de autenticación
        }
    }

    // Verifica si el usuario ya existe en la base de datos
    public Authentication authenticate(String username, String password) {

        UserDetails userDetails = this.loadUserByUsername(username); // Carga los detalles del usuario

        // Comprueba si el usuario fue encontrado en el sistema.
        if (userDetails == null) {

            throw new BadCredentialsException("Ivalid username or password.");

        }

        if(!userDetails.isEnabled()) {
            throw new BadCredentialsException("Ivalid user status.");
        }

        // Compara la contraseña proporcionada por el usuario con la contraseña
        // almacenada en la base de datos.
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Ivalid  password.");

        }

        // Si las credenciales son válidas, crea un objeto de tipo
        // UsernamePasswordAuthenticationToken que representa al usuario autenticado.
        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(),
                userDetails.getAuthorities());

    }

    public AuthResponse registerUser(AuthCreatedUserRequest authCreateUserRequest) {

        // Obtenemos usaurio y contraseña
        String email = authCreateUserRequest.email();
        String password = authCreateUserRequest.password();
        String identification = authCreateUserRequest.identification();

        if (password.length() < 8) {
            throw new RuntimeException("The password must be at least 8 characters long.");
            // Si la contraseña es menor a 8 caracteres, retorna null
        }

        // Validar si el usuario ya existe
        if (userRepository.findUserEntityByEmail(email).isPresent()) {
            throw new RuntimeException("The email is already registered.");

        }

        // Obtengo los roles
        List<String> roleRequest = authCreateUserRequest.roleRequest().roleListName();

        // Busco los roles en la base de datos
        Set<Role> roleEntitySet = roleRepository.findRoleEntitiesByRoleEnumIn(roleRequest).stream()
                .collect(Collectors.toSet());

        if (roleEntitySet.isEmpty()) {
            throw new RuntimeException("The roles specified does not exist");
        }

        UserEntity userEntity = UserEntity.builder()
                .identification(identification)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(roleEntitySet)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .isEnabled(true)
                .build();

        UserEntity userCreated = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // Obtenemos los roles
        userCreated.getRoles().forEach(
                role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        // Setear permisos
        userCreated.getRoles() // Devuelve un conjunto de roles asociados al usuario.
                .stream()
                .flatMap(role -> role.getPermissionList().stream()) // Convierte la lista de permisos de cada rol en un
                                                                    // flujo único de permisos.
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName()))); // Agrega
                                                                                                             // cada
                                                                                                             // permiso
                                                                                                             // a la
                                                                                                             // lista de
                                                                                                             // autoridades.

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getEmail(),
                userCreated.getPassword(), authorityList);
        String accesToken = jwtUtils.createToken(authentication);
        Long ownerId = (userCreated.getOwner() != null) ? userCreated.getOwner().getId() : null;
        AuthResponse.UserDTO userDTO = new AuthResponse.UserDTO(userCreated.getId(), userCreated.getEmail(),
                userCreated.getRoles().stream()
                        .map(role -> new AuthResponse.UserDTO.Role(role.getRoleEnum().name()))
                        .toArray(AuthResponse.UserDTO.Role[]::new),
                ownerId);

        AuthResponse authResponse = new AuthResponse(accesToken, userDTO);
        return authResponse;

    }

    public AuthResponse getMe() {
        // Obtener el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si no hay un usuario autenticado, lanzar una excepción
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("El usuario no está autenticado.");
        }

        // Obtener el email del usuario autenticado desde el contexto
        String email = authentication.getName();

        // Buscar el usuario en la base de datos utilizando su email
        UserEntity userEntity = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new RuntimeException("El usuario no fue encontrado en la base de datos."));

        Long ownerId = (userEntity.getOwner() != null) ? userEntity.getOwner().getId() : null;

        AuthResponse.UserDTO userDTO = new AuthResponse.UserDTO(userEntity.getId(), userEntity.getEmail(),
                userEntity.getRoles().stream()
                        .map(role -> new AuthResponse.UserDTO.Role(role.getRoleEnum().name()))
                        .toArray(AuthResponse.UserDTO.Role[]::new),
                ownerId);

        return new AuthResponse(null, userDTO); // Return AuthResponse with userDTO and null token
    }

}
