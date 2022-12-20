package ru.practicum.shareit.booking;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByEndDesc(Long userId);
    @Query("SELECT b FROM bookings AS b " +
            "WHERE b.booker.id = ?1 AND ?2 BETWEEN b.start AND b.end " +
            "ORDER BY b.end DESC")
    List<Booking> findByBooker_IdAndDateCurrent(Long userId, LocalDateTime now);
    List<Booking>findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId,LocalDateTime now);
    List<Booking>findByBooker_IdAndStartGreaterThanOrderByEndDesc(Long userId, LocalDateTime now);
    List<Booking>findAllByBooker_IdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status);
    List<Booking>findByItem_Owner_IdOrderByEnd_BookingDesc(Long userId);
    @Query("SELECT b FROM bookings AS b " +
            "WHERE b.item.owner.id = ?1 AND ?2 BETWEEN b.start AND b.end " +
            "ORDER BY b.end DESC")
    List<Booking>findByItem_Owner_idAndBetweenStartAndEnd(Long userId, LocalDateTime now);
    List<Booking>findByItem_Owner_idAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);
    List<Booking>findByItem_Owner_idAndStartGreaterThanOrderByEndDesc(Long userId, LocalDateTime now);
    List<Booking>findByItem_Owner_idAndStatusIsOrderByStartDesc( Long userId, BookingStatus status);

}
