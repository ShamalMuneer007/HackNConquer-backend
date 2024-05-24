package org.hackncrypt.chatservice.repository;

import org.hackncrypt.chatservice.entity.ChatMessage;
import org.hackncrypt.chatservice.model.dto.UnreadMessageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE (m.receiverId = :user1 OR m.senderId = :user1) AND (m.receiverId = :user2 OR m.senderId = :user2) "+
    " order by m.timestamp ASC")
    List<ChatMessage> findUserMessages(@Param("user1") Long user1, @Param("user2") Long user2);

    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampAsc(Long senderId, Long receiverId);

    @Query("SELECT new org.hackncrypt.chatservice.model.dto.UnreadMessageDto(m.senderId, COUNT(m)) FROM ChatMessage m WHERE m.receiverId = :receiverId AND m.senderId IN :senders AND m.read = false GROUP BY m.senderId")
    List<UnreadMessageDto> findCountOfUnreadMessagesGroupedBySender(@Param("senders") List<Long> senders, @Param("receiverId") Long receiverId);
}