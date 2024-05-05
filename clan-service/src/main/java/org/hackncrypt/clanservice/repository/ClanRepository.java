package org.hackncrypt.clanservice.repository;

import org.hackncrypt.clanservice.model.entity.Clan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClanRepository extends JpaRepository<Clan,Long> {
}
