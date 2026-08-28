package com.evlarus.ecomreturns.user.infrastructure;

import com.evlarus.ecomreturns.user.domain.Address;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);
}
