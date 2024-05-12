package org.hackncrypt.userservice.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"friend1_id", "friend2_id"}))
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long friendId;

    @ManyToOne
    @JoinColumn(name = "friend1_id", referencedColumnName = "user_id")
    private User friend1;

    @ManyToOne
    @JoinColumn(name = "friend2_id", referencedColumnName = "user_id")
    private User friend2;

    @CreationTimestamp
    private LocalDateTime localDateTime;
}
