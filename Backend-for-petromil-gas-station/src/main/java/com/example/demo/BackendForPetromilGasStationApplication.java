package com.example.demo;

import com.example.demo.dao.OwnerRepository;
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
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class BackendForPetromilGasStationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendForPetromilGasStationApplication.class, args);
	}


	@Bean
	CommandLineRunner init(UserRepository userRepository, OwnerRepository ownerRepository) {
		return args -> {
			/* Create PERMISSIONS */
			Permission createPermission = Permission.builder()
					.name("CREATE")
					.build();

			Permission readPermission = Permission.builder()
					.name("READ")
					.build();

			Permission updatePermission = Permission.builder()
					.name("UPDATE")
					.build();

			Permission deletePermission = Permission.builder()
					.name("DELETE")
					.build();

			Permission refactorPermission = Permission.builder()
					.name("REFACTOR")
					.build();

			/* Create ROLES */
			Role roleAdmin = Role.builder()
					.roleEnum(RoleEnum.ADMIN)
					.permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission))
					.build();

			Role roleSuperAdmin = Role.builder()
					.roleEnum(RoleEnum.SUPERADMIN)
					.permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission))
					.build();



			/* CREATE USERS */
			UserEntity userIsack = UserEntity.builder()
					.identification("1137974875")
					.email("isack2910@gmail.com")
					.password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.state(State.ACTIVE)
					.roles(Set.of(roleAdmin))
					.build();

			Owner owner = Owner.builder()
					.name("Isack Padilla")
					.phone("3117589284")
					.user(userIsack)
					.build();

			UserEntity userGaspar = UserEntity.builder()
					.identification("10064980429")
					.email("jgasparlopez29@gmail.com")
					.password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.state(State.ACTIVE)
					.roles(Set.of(roleSuperAdmin))
					.build();



			userRepository.saveAll(List.of(userIsack, userGaspar));
			ownerRepository.save(owner);
		};
	}
}
