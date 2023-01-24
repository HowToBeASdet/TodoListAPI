package com.howtobeasdet.todolistapi.repository;

import com.howtobeasdet.todolistapi.model.ERole;
import com.howtobeasdet.todolistapi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
