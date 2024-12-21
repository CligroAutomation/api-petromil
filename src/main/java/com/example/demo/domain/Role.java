package com.example.demo.domain;

import com.example.demo.config.auditor.Auditable;
import com.example.demo.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends Auditable {

    // Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // columna que contiene el rol
    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum;

    // @ManyToMany: Define una relación muchos a muchos entre las entidades Role y
    // Permission
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissionList = new HashSet<>();

    /*
     * 
     * fetch = FetchType.EAGER: Indica que, al cargar un objeto Role, también se
     * cargarán automáticamente todos los objetos relacionados en permissionList
     * 
     * cascade = CascadeType.ALL: Aplica operaciones en cascada (guardar,
     * actualizar, eliminar) a los permisos relacionados cuando se realizan en el
     * objeto Role
     * 
     * @JoinTable
     * Esta anotación define la tabla intermedia que se usará en la base de datos
     * para manejar la relación muchos a muchos entre Roles y Permisos.
     * Propiedades:
     * name = "role_permissions": Define el nombre de la tabla intermedia en la base
     * de datos.
     * joinColumns = @JoinColumn(name = "role_id"): Especifica la columna en la
     * tabla intermedia que se relaciona con la entidad actual (Role).
     * inverseJoinColumns = @JoinColumn(name = "permission_id"): Especifica la
     * columna en la tabla intermedia que se relaciona con la entidad opuesta
     * (Permission).
     * 
     * 
     */

}
