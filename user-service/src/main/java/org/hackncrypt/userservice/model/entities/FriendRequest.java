//package org.hackncrypt.userservice.model.entities;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.hackncrypt.userservice.enums.FriendStatus;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class FriendRequest {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "sender_id")
//    private User sender;
//
//    @ManyToOne
//    @JoinColumn(name = "receiver_id")
//    private User receiver;
//
//    @Enumerated(EnumType.STRING)
//    private FriendStatus status;
//
//    private LocalDateTime createdAt;
//}