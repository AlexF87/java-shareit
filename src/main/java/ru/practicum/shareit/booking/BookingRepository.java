package ru.practicum.shareit.booking;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByEndDesc(Long userId);

    List<Booking> findByBooker_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc(Long userId, LocalDateTime now,
                                                                                      LocalDateTime now2);

    List<Booking> findByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc(Long userId,
                                                                                          LocalDateTime now,
                                                                                          LocalDateTime now2);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByBooker_IdAndStartAfterOrderByEndDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByItem_Owner_IdOrderByEndDesc(Long userId);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByEndDesc(Long userId, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByItem_IdAndStartIsAfterOrderByStartDesc(Long itemIds, LocalDateTime now);

    List<Booking> findByItem_IdAndEndIsBeforeOrderByEndDesc(Long items, LocalDateTime now);

    List<Booking> findAllBookingsByItem_IdAndBooker_IdAndEndBeforeAndStatus(Long itemId, Long userId, LocalDateTime now,
                                                                            BookingStatus status, Sort sort);

}
