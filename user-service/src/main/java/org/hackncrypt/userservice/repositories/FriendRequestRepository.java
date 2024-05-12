package org.hackncrypt.userservice.repositories;

import feign.Param;
import org.hackncrypt.userservice.enums.FriendStatus;
import org.hackncrypt.userservice.model.entities.FriendRequest;
import org.hackncrypt.userservice.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest,Long> {
    FriendRequest findBySenderUserIdAndReceiverUserId(Long senderId, Long receiverId);

    List<FriendRequest> findByReceiverUserId(Long receiverId);

    @Query("SELECT fr FROM FriendRequest fr WHERE (fr.sender.userId = :currentUserId AND fr.receiver.userId = :userId) OR (fr.sender.userId = :userId AND fr.receiver.userId = :currentUserId)")
    List<FriendRequest> findFriendRequests(@Param("currentUserId") long currentUserId,@Param("userId") long userId);

}
