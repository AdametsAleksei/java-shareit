package ru.practicum.shareit.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(name = "description")
    @NotEmpty
    private String description;

    @Column(name = "available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Transient
    private LocalDateTime lastBookingDate;

    @Transient
    private LocalDateTime nextBookingDate;
}
