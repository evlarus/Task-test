package com.evlarus.ecomreturns.returns.infrastructure;

import com.evlarus.ecomreturns.returns.domain.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    Page<ReturnRequest> findByUserId(Long userId, Pageable pageable);
}
