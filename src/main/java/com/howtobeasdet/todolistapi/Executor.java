package com.howtobeasdet.todolistapi;

import com.howtobeasdet.todolistapi.model.Role;
import com.howtobeasdet.todolistapi.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.howtobeasdet.todolistapi.model.ERole.ROLE_USER;

@Component
@Order(1)
public class Executor implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        Role r = new Role();
        r.setName(ROLE_USER);
        roleRepository.save(r);
    }
}
