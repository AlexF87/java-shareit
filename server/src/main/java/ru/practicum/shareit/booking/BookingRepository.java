package ru.practicum.shareit.booking;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByEndDesc(Long userId, Pageable pageable);

    List<Booking> findByBooker_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc(Long userId,
                                                                                      LocalDateTime dateForStart,
                                                                                      LocalDateTime dateForEnd,
                                                                                      Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc(Long userId,
                                                                                          LocalDateTime dateForStart,
                                                                                          LocalDateTime dateForEnd,
                                                                                          Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime date, Pageable pageable);

    List<Booking> findByBooker_IdAndStartAfterOrderByEndDesc(Long userId, LocalDateTime date, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItem_Owner_IdOrderByEndDesc(Long userId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime date, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByEndDesc(Long userId, LocalDateTime date, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItem_IdAndStartIsAfterOrderByStartDesc(Long itemIds, LocalDateTime date);

    List<Booking> findByItem_IdAndEndIsBeforeOrderByEndDesc(Long items, LocalDateTime date);

    List<Booking> findAllBookingsByItem_IdAndBooker_IdAndEndBeforeAndStatus(Long itemId, Long userId,
                                                                            LocalDateTime date,
                                                                            BookingStatus status, Sort sort);

}
