package com.example.demo.dao;

import com.example.demo.domain.Role;
import com.example.demo.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findRoleEntitiesByRoleEnumIn(List<String> roleName);



    Optional<Role> findByRoleEnum(RoleEnum roleEnum);


}
