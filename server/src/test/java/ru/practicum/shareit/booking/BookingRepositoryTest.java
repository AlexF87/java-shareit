package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    User user;
    User owner;
    Item item;

    @BeforeEach
    private void setUp() {
        user = User.builder()
                .name("Barak")
                .email("barak@mail.test")
                .build();

        userRepository.save(user);

        owner = User.builder()
                .name("John")
                .email("john@mail.tets")
                .build();

        userRepository.save(owner);

        item = Item.builder()
                .name("Book")
                .description("Java and JavaScript")
                .available(true)
                .owner(owner)
                .build();

        itemRepository.save(item);
    }

    @Test
    void findByBooker_IdOrderByEndDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .build();
        bookingRepository.save(booking2);
        List<Booking> bookingList = bookingRepository.findByBooker_IdOrderByEndDesc(user.getId(),
                CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking.getEnd(), bookingList.stream().findFirst().get().getEnd());
        assertEquals(booking.getId(), bookingList.stream().findFirst().get().getId());
    }

    @Test
    void findByBooker_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList =
                bookingRepository.findByBooker_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc(user.getId(),
                        LocalDateTime.now(), LocalDateTime.now(), CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList =
                bookingRepository.findByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc(owner.getId(),
                        LocalDateTime.now(), LocalDateTime.now(), CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByBooker_IdAndEndBeforeOrderByStartDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.now(), CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking2.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findByBooker_IdAndStartAfterOrderByEndDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByBooker_IdAndStartAfterOrderByEndDesc(user.getId(),
                LocalDateTime.now(), CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByBooker_IdAndStatusIsOrderByStartDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(user.getId(),
                BookingStatus.WAITING, CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(booking.getStatus(), bookingList.stream().findFirst().get().getStatus());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findByItem_Owner_IdOrderByEndDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdOrderByEndDesc(owner.getId(),
                CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(booking.getStatus(), bookingList.stream().findFirst().get().getStatus());
        assertEquals(2, bookingList.size());

    }

    @Test
    void findByItem_Owner_IdAndEndBeforeOrderByStartDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(owner.getId(),
                LocalDateTime.now(), CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking2.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(booking2.getStatus(), bookingList.stream().findFirst().get().getStatus());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findByItem_Owner_IdAndStartAfterOrderByEndDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdAndStartAfterOrderByEndDesc(owner.getId(),
                LocalDateTime.now(), CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(booking.getStatus(), bookingList.stream().findFirst().get().getStatus());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findByItem_Owner_IdAndStatusIsOrderByStartDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdAndStatusIsOrderByStartDesc(owner.getId(),
                BookingStatus.APPROVED, CustomPageRequest.of(0, 10));

        assertNotNull(bookingList);
        assertEquals(booking2.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(booking2.getStatus(), bookingList.stream().findFirst().get().getStatus());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findByItem_IdAndStartIsAfterOrderByStartDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItem_IdAndStartIsAfterOrderByStartDesc(item.getId(),
                LocalDateTime.now());

        assertNotNull(bookingList);
        assertEquals(booking.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(booking.getStatus(), bookingList.stream().findFirst().get().getStatus());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findByItem_IdAndEndIsBeforeOrderByEndDesc() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItem_IdAndEndIsBeforeOrderByEndDesc(item.getId(),
                LocalDateTime.now());

        assertNotNull(bookingList);
        assertEquals(booking2.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(booking2.getStatus(), bookingList.stream().findFirst().get().getStatus());
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllBookingsByItem_IdAndBooker_IdAndEndBeforeAndStatus() {
        Booking booking = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        Booking booking2 = Booking.builder()
                .booker(user)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking2);

        List<Booking> bookingList =
                bookingRepository.findAllBookingsByItem_IdAndBooker_IdAndEndBeforeAndStatus(item.getId(), user.getId(),
                        LocalDateTime.now(), BookingStatus.APPROVED, Sort.unsorted());

        assertNotNull(bookingList);
        assertEquals(booking2.getId(), bookingList.stream().findFirst().get().getId());
        assertEquals(booking2.getStatus(), bookingList.stream().findFirst().get().getStatus());
        assertEquals(1, bookingList.size());
    }

    @AfterEach
    private void deleteItems() {
        bookingRepository.deleteAll();
    }
}