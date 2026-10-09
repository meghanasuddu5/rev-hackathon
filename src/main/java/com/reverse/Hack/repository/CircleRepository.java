package com.reverse.Hack.repository;

import com.reverse.Hack.model.Circle;
import com.reverse.Hack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CircleRepository extends JpaRepository<Circle, Long> {

    List<Circle> findByOwnerOrderByCreatedAtAsc(User owner);

    boolean existsByNameAndOwner(String name, User owner);
}
