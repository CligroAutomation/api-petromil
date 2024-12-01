package com.example.demo.dao;

import com.example.demo.domain.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByEmail(String email);

    Optional<UserEntity> findUserEntityByIdentification(String identification);

    void deleteUserEntityByEmail(String email);




}
