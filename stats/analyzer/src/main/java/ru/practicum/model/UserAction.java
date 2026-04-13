package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_actions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long userId;
    Long eventId;
    @Enumerated(EnumType.STRING)
    ActionType actionType;
    Instant timestamp;
}
