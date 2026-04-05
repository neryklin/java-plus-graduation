package practicum.request.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import practicum.interaction.enums.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"requester_id", "event_id"})
        }
)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime created;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "event_id")
    private Long eventId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
