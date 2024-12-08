package com.example.demo;

import com.example.demo.dao.OwnerRepository;
import com.example.demo.dao.PermissionRepository;
import com.example.demo.dao.RoleRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.domain.Owner;
import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.UserEntity;
import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.State;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class BackendForPetromilGasStationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendForPetromilGasStationApplication.class, args);
	}



}
