//package org.hackncrypt.userservice.repositories;
//
//import org.hackncrypt.userservice.enums.FriendStatus;
//import org.hackncrypt.userservice.model.entities.FriendRequest;
//import org.hackncrypt.userservice.model.entities.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface FriendRequestRepository extends JpaRepository<FriendRequest,Long> {
//    FriendRequest findBySenderAndReceiverAndStatus(User sender, User receiver, FriendStatus friendStatus);
//}
