package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest>findAllByUserIdOrderByCreationTimeDesc(Long userId);
    List<ItemRequest>findAllByUserIdIsNotOrderByCreationTimeDesc(Long userId, Pageable pageable);
}
