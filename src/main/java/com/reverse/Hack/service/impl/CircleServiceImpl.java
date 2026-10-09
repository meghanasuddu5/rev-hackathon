package com.reverse.Hack.service.impl;

import com.reverse.Hack.dto.CircleDTO;
import com.reverse.Hack.model.Circle;
import com.reverse.Hack.model.User;
import com.reverse.Hack.repository.CircleRepository;
import com.reverse.Hack.repository.UserRepository;
import com.reverse.Hack.service.CircleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CircleServiceImpl implements CircleService {

    private final CircleRepository circleRepository;
    private final UserRepository userRepository;

    public CircleServiceImpl(CircleRepository circleRepository, UserRepository userRepository) {
        this.circleRepository = circleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Circle createCircle(User owner, CircleDTO dto) {
        if (circleRepository.existsByNameAndOwner(dto.getName(), owner)) {
            throw new IllegalArgumentException("You already have a circle with that name");
        }
        Circle circle = Circle.builder().name(dto.getName()).description(dto.getDescription()).owner(owner).build();
        return circleRepository.save(circle);
    }

    @Override
    public Circle updateCircle(User owner, Long circleId, CircleDTO dto) {
        Circle circle = getOwnedCircle(owner, circleId);
        circle.setName(dto.getName());
        circle.setDescription(dto.getDescription());
        return circleRepository.save(circle);
    }

    @Override
    public void deleteCircle(User owner, Long circleId) {
        circleRepository.delete(getOwnedCircle(owner, circleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Circle> getCirclesForUser(User owner) {
        return circleRepository.findByOwnerOrderByCreatedAtAsc(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Circle> findById(Long id) {
        return circleRepository.findById(id);
    }

    @Override
    public void addMember(User owner, Long circleId, Long userId) {
        Circle circle = getOwnedCircle(owner, circleId);
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (target.getId().equals(owner.getId())) throw new IllegalArgumentException("Cannot add yourself");
        circle.getMembers().add(target);
        circleRepository.save(circle);
    }

    @Override
    public void removeMember(User owner, Long circleId, Long userId) {
        Circle circle = getOwnedCircle(owner, circleId);
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        circle.getMembers().remove(target);
        circleRepository.save(circle);
    }

    private Circle getOwnedCircle(User owner, Long circleId) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new IllegalArgumentException("Circle not found"));
        if (!circle.getOwner().getId().equals(owner.getId())) {
            throw new SecurityException("Not authorized");
        }
        return circle;
    }
}
