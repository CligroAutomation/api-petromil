package com.example.demo.service;

import com.example.demo.dao.OwnerRepository;
import com.example.demo.dao.PermissionRepository;
import com.example.demo.dao.RoleRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.domain.Owner;
import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.UserEntity;

import com.example.demo.domain.dto.OwnerResponse;
import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Service
public class OwnerServiceImpl {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    public OwnerResponse createOwner(OwnerResponse ownerResponse) {
        // Verificar si el usuario ya existe

        Optional<UserEntity> existingUserByIdentification = userRepository
                .findUserEntityByIdentification(ownerResponse.identification());

        if (existingUserByIdentification.isPresent() && existingUserByIdentification.get().getState() == State.ACTIVE) {

            throw new RuntimeException("No puedes agregar dos usuarios con la misma cedula");

        }

        Optional<UserEntity> existingUser = userRepository.findUserEntityByEmail(ownerResponse.email());

        if (existingUser.isPresent()) {

            // Verificar si el usuario está inactivo
            if (existingUser.get().getState() == State.INACTIVE) {
                System.out.println("Entra al if donde verifica si el estado del usuario está inactivo");

                UserEntity user = existingUser.get();

                // Actualizar los datos del usuario
                user.setIdentification(ownerResponse.identification());
                user.setEmail(ownerResponse.email());
                user.setPassword(passwordEncoder.encode(ownerResponse.password())); // Encriptar la nueva contraseña
                user.setEnabled(true);
                user.setAccountNoExpired(true);
                user.setAccountNoLocked(true);
                user.setCredentialNoExpired(true);
                user.setState(State.ACTIVE);

                // Buscar el rol ADMIN existente
                Role adminRole = roleRepository.findByRoleEnum(RoleEnum.OWNER)
                        .orElseThrow(() -> new IllegalArgumentException("El rol OWNER no existe"));

                // Buscar el permiso READ y asociarlo al rol ADMIN
                Permission p = permissionRepository.findByName("READ")
                        .orElseThrow(() -> new IllegalArgumentException("El permiso READ ya existe"));

                // Crear un HashSet mutable de permisos
                Set<Permission> permissions = new HashSet<>();
                permissions.add(p);
                adminRole.setPermissionList(permissions);

                // Crear un HashSet mutable de roles
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                user.setRoles(roles); // Asociar el rol ADMIN al usuario

                // Actualizar el propietario asociado al usuario
                Owner owner = user.getOwner();
                owner.setUser(user);
                owner.setName(ownerResponse.name());
                owner.setPhone(ownerResponse.phone());

                userRepository.save(user); // Guardar el usuario con sus cambios

                return new OwnerResponse(
                        owner.getId(),
                        owner.getUser().getIdentification(),
                        owner.getName(),
                        owner.getPhone(),
                        owner.getUser().getEmail(),
                        owner.getUser().getPassword());
            }
        }

        // Si el usuario no existe, crear uno nuevo
        UserEntity newUser = new UserEntity();
        newUser.setIdentification(ownerResponse.identification());
        newUser.setEmail(ownerResponse.email());
        newUser.setPassword(passwordEncoder.encode(ownerResponse.password())); // Encriptar la contraseña
        newUser.setEnabled(true);
        newUser.setAccountNoExpired(true);
        newUser.setAccountNoLocked(true);
        newUser.setCredentialNoExpired(true);
        newUser.setState(State.ACTIVE);

        // Buscar el rol ADMIN existente o lanzar excepción si no está definido
        Role adminRole = roleRepository.findByRoleEnum(RoleEnum.OWNER)
                .orElseThrow(() -> new IllegalArgumentException("El rol ADMIN no existe"));

        // Buscar el permiso READ y asociarlo al rol ADMIN
        Permission p = permissionRepository.findByName("READ")
                .orElseThrow(() -> new IllegalArgumentException("El permiso READ ya existe"));

        // Crear un HashSet mutable de permisos
        Set<Permission> permissions = new HashSet<>();
        permissions.add(p);
        adminRole.setPermissionList(permissions);

        // Crear un HashSet mutable de roles
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        newUser.setRoles(roles);

        // Crear un nuevo propietario (Owner) y asociarlo al usuario
        Owner owner = new Owner();
        owner.setUser(newUser);
        owner.setName(ownerResponse.name());
        owner.setPhone(ownerResponse.phone());
        newUser.setOwner(owner);

        // Guardar el usuario en la base de datos
        userRepository.save(newUser);

        // Guardar el propietario en la base de datos
        ownerRepository.save(owner);

        // Retornar la respuesta del propietario creado
        return new OwnerResponse(
                owner.getId(),
                owner.getUser().getIdentification(),
                owner.getName(),
                owner.getPhone(),
                owner.getUser().getEmail(),
                owner.getUser().getPassword());

    }

    public OwnerResponse getOwner(Long id) {

        System.out.println("Entro al getOwner");

        // Optional<UserEntity> existingUser = userRepository.findUserEntityById(id);

        Optional<Owner> o = ownerRepository.findById(id);

        if (o.isPresent()) {

            Owner own = o.get();

            if (own.getUser().getState() == State.INACTIVE) {
                throw new RuntimeException("propietario con estado inactivo");

            }

            // Asignamos los datos de Owner solo si user.getOwner() no es null
            OwnerResponse owr = new OwnerResponse(
                    own.getId(),
                    own.getUser().getIdentification(),
                    own.getName(),
                    own.getPhone(),
                    own.getUser().getEmail(),
                    own.getUser().getPassword()

            );
            return owr; // Retornamos el objeto OwnerResponse

        }
        // El usuario no fue encontrado
        throw new RuntimeException("propietario no encontrado");

        // Retorna null si no se encuentra un owner o no es admin
    }

    public OwnerResponse updateOwner(Long idPropietario, OwnerResponse ownerResponse) {

        if (idPropietario == null) {
            throw new RuntimeException("Id del propietario no puede ser nula");

        }

        Optional<Owner> owner = ownerRepository.findById(idPropietario);

        if (!owner.isPresent()) {
            System.out.println("Este propietario no existe");
            throw new RuntimeException("Este propietario no existe");

        }
        Owner own = owner.get();

        if (own.getUser().getState() == State.INACTIVE) {
            throw new RuntimeException("El propietario el cual intentas editar, está inactivo");

        }

        if (own.getUser().getEmail() != ownerResponse.email()) {
            System.out.println("email diferente");

            own.setUser(own.getUser());
            own.setName(ownerResponse.name());
            own.setPhone(ownerResponse.phone());
            own.getUser().setIdentification(ownerResponse.identification());
            // Seteando la nueva contraseña
            own.getUser().setPassword(passwordEncoder.encode(ownerResponse.password()));
            own.getUser().setEmail(ownerResponse.email());
            ownerRepository.save(own);

        }

        own.setUser(own.getUser());
        own.setName(ownerResponse.name());
        own.setPhone(ownerResponse.phone());
        own.getUser().setIdentification(ownerResponse.identification());
        // Seteando la nueva contraseña
        own.getUser().setPassword(passwordEncoder.encode(ownerResponse.password()));
        ownerRepository.save(own);

        return new OwnerResponse(
                own.getId(),
                own.getUser().getIdentification(),
                own.getName(),
                own.getPhone(),
                own.getUser().getEmail(),
                own.getUser().getPassword());

    }

    public OwnerResponse deleteOwner(Long id) {

        Optional<Owner> owner = ownerRepository.findById(id);

        if (!owner.isPresent()) {
            throw new RuntimeException("El propietario el cual intentas eliminar no existe");

        }
        Owner own = owner.get();

        if (own.getUser().getState() == State.INACTIVE) {
            throw new RuntimeException("El propietario el cual intentas eliminar no existe || ya está inactivo");
        }

        UserEntity user = own.getUser();

        user.setState(State.INACTIVE);
        userRepository.save(user);
        OwnerResponse or = new OwnerResponse(user.getOwner().getId(), user.getIdentification(),
                user.getOwner().getName(), user.getOwner().getPhone(), user.getEmail(), user.getPassword());

        return or;

    }

    public List<Owner> getAllOwnerByUserState(Pageable pageable) {

        Page<Owner> ownersPage = ownerRepository.findByUserState(State.ACTIVE, pageable);

        System.out.println(ownersPage.getContent());

        List<Owner> owners = ownersPage.getContent();


        if (owners.isEmpty()) {
            throw new RuntimeException("No hay propietarios activos");
        }

        return owners;

    }



}
