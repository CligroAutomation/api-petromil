package com.example.demo.domain;

import com.example.demo.config.auditor.Auditable;
import com.example.demo.enums.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identification", unique = true)
    @NotNull
    private String identification;

    @Column(name = "email", unique = true)
    @Email
    @NotNull
    private String email;

    @Column(name = "password")
    @NotNull
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "account_No_Expired")
    private boolean accountNoExpired;

    @Column(name = "account_No_Locked")
    private boolean accountNoLocked;

    @Column(name = "credential_No_Expired")
    private boolean credentialNoExpired;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // Relación de muchos a muchos (Muchos usuarios
                                                                    // pueden tener muchos roles, y viceversa)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Relación opcional hacia Dueno
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    private Owner owner;

    @Column(name = "state")
    private State state;

    /*
     * 
     * //fetch = FetchType.EAGER: Especifica que, cuando se cargue una entidad que
     * contiene
     * // esta relación, también se deben cargar todos los objetos relacionados (en
     * este caso, los roles) inmediatamente.
     * 
     * //Cascade: si se guarda, actualiza o elimina un objeto User, los objetos
     * relacionados en la colección roles también se guardarán,
     * // actualizarán o eliminarán automáticamente.
     * 
     * 
     * Propiedades de @JoinTable:
     * name = "user_roles": Especifica el nombre de la tabla intermedia que se usará
     * en la base de datos para mapear la relación.
     * joinColumns = @JoinColumn(name = "user_id"): Define la columna en la tabla
     * intermedia que apunta a la entidad actual (probablemente una entidad de
     * usuario).
     * inverseJoinColumns = @JoinColumn(name = "role_id"): Define la columna en la
     * tabla intermedia que apunta a la entidad relacionada (en este caso, los
     * roles).
     * 
     */

}
