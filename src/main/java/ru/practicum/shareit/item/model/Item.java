package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Setter
@Getter
@Builder
@Entity
@Table(name = "items", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_name", nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Comment> comments = new ArrayList<>();
    @Column(name = "request_id")
    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(getId(), item.getId()) &&
                Objects.equals(getName(), item.getName()) &&
                Objects.equals(getDescription(), item.getDescription()) &&
                Objects.equals(getAvailable(), item.getAvailable()) &&
                Objects.equals(getOwner(), item.getOwner()) &&
                Objects.equals(getComments(), item.getComments()) &&
                Objects.equals(getRequestId(), item.getRequestId());
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (getId() != null) {
            hash = hash + getId().hashCode();
        }
        hash = hash * 31;
        if (getName() != null) {
            hash = hash + getName().hashCode();
        }
        if (getDescription() != null) {
            hash = hash + getDescription().hashCode();
        }
        if (getAvailable() != null) {
            hash = hash + getAvailable().hashCode();
        }
        if (getOwner() != null) {
            hash = hash + getOwner().hashCode();
        }
        if (getComments() != null) {
            hash = hash + getComments().hashCode();
        }
        if (getRequestId() != null) {
            hash = hash + getRequestId().hashCode();
        }
        return hash;
    }
}
