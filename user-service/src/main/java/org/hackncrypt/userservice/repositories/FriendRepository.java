package org.hackncrypt.userservice.repositories;

import feign.Param;
import org.hackncrypt.userservice.model.entities.Friend;
import org.hackncrypt.userservice.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend,Long> {
    @Query("SELECT f.friend1 FROM Friend f WHERE f.friend2.userId = :userId " +
            "UNION " +
            "SELECT f.friend2 FROM Friend f WHERE f.friend1.userId = :userId")
    List<User> findFriendsByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friend f " +
            "WHERE (f.friend1.userId = :user1Id AND f.friend2.userId = :user2Id) " +
            "OR (f.friend1.userId = :user2Id AND f.friend2.userId = :user1Id)")
    Friend findFriendship(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM Friend f " +
            "WHERE (f.friend1.userId = :user1Id AND f.friend2.userId = :user2Id) " +
            "OR (f.friend1.userId = :user2Id AND f.friend2.userId = :user1Id)")
    boolean areFriends(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
