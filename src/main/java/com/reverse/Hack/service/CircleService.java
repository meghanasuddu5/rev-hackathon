package com.reverse.Hack.service;

import com.reverse.Hack.dto.CircleDTO;
import com.reverse.Hack.model.Circle;
import com.reverse.Hack.model.User;

import java.util.List;
import java.util.Optional;

public interface CircleService {

    Circle createCircle(User owner, CircleDTO dto);

    Circle updateCircle(User owner, Long circleId, CircleDTO dto);

    void deleteCircle(User owner, Long circleId);

    List<Circle> getCirclesForUser(User owner);

    Optional<Circle> findById(Long id);

    void addMember(User owner, Long circleId, Long userId);

    void removeMember(User owner, Long circleId, Long userId);
}
