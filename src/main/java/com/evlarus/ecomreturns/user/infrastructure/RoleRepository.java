package com.evlarus.ecomreturns.user.infrastructure;

import com.evlarus.ecomreturns.user.domain.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
