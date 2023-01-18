package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Long requestId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "created", nullable = false)
    private LocalDateTime creationTime;
    @Column(name = "description", nullable = false)
    private String description;
    @OneToMany
    @JoinColumn(name = "request_id")
    private List<Item> items;
}
