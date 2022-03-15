package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.OrderModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    List<OrderModel> findByUserId(long id, Pageable pageable);

    Optional<OrderModel> findByUserIdAndId(long userId, long orderId);

}
