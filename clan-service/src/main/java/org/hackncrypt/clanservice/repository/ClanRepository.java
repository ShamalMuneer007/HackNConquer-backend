package org.hackncrypt.clanservice.repository;

import feign.Param;
import org.hackncrypt.clanservice.model.entity.Clan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ClanRepository extends JpaRepository<Clan,Long> {
    List<Clan> findByClanNameStartingWithAndIsDeletedFalse(String username);

    @Query("SELECT c FROM Clan c WHERE :userId MEMBER OF c.members OR c.clanOwnerId = :userId")
    List<Clan> findClansForUser(@Param("userId") Long userId);

    boolean existsByClanName(String name);


    @Modifying
    @Transactional
    @Query("UPDATE Clan clan " +
            "SET clan.clanRank = (" +
            "    SELECT clan.clanRank " +
            "    FROM (" +
            "        SELECT clan.clanId AS clanId, RANK() OVER (ORDER BY u2.clanLevel DESC, u2.clanXp DESC) AS clan_rank " +
            "        FROM Clan u2 " +
            "        WHERE u2.isDeleted = false" +
            "    ) AS ru " +
            "    WHERE ru.clanId = clan.clanId" +
            ")")
    void updateAllClanRank();

    Page<Clan> findByIsDeletedFalseOrderByClanLevelDescClanXpDesc(PageRequest pageRequest);
}
