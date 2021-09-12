package com.hello.jwt.repository;

import java.util.Optional;

import com.hello.jwt.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hello.jwt.models.EnumRole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(EnumRole name);
}
