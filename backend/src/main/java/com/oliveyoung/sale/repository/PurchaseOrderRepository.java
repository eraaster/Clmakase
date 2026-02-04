package com.oliveyoung.sale.repository;

import com.oliveyoung.sale.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findBySessionId(String sessionId);
}
