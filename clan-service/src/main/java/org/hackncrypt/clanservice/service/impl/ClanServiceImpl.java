package org.hackncrypt.clanservice.service.impl;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.clanservice.exception.InvalidInputException;
import org.hackncrypt.clanservice.exception.business.ClanException;
import org.hackncrypt.clanservice.exception.business.ClanNotFoundException;
import org.hackncrypt.clanservice.integrations.fiegn.proxy.user.UserFeignProxy;
import org.hackncrypt.clanservice.model.dto.ClanDto;
import org.hackncrypt.clanservice.model.dto.UserLevelChange;
import org.hackncrypt.clanservice.model.dto.response.AddUserClanResponse;
import org.hackncrypt.clanservice.model.entity.Clan;
import org.hackncrypt.clanservice.repository.ClanRepository;
import org.hackncrypt.clanservice.service.ClanService;
import org.hackncrypt.clanservice.service.S3Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClanServiceImpl implements ClanService {
    private final ClanRepository clanRepository;
    private final S3Service s3Service;
    private final UserFeignProxy userFeignProxy;
    @Value("app.frontend.url")
    private String frontEndUrl;

    @Override
    public List<ClanDto> searchClansContainingClanName(String name) {
        List<Clan> clanList = clanRepository.findByClanNameStartingWithAndIsDeletedFalse(name);
        return clanList.stream().map(ClanDto::new).toList();
    }

    @Override
    @Transactional
    public void createClan(String name, String description, long userId, MultipartFile image,String authHeader) throws IOException {

        if(clanRepository.existsByClanName(name)){
            throw new InvalidInputException("Clan name already exists !!!");
        }
        if(!clanRepository.findClansForUser(userId).isEmpty()){
            throw new ClanException("User already exists in a clan.. Please leave the clan before creating a clan");
        }
        String randomString = UUID.randomUUID().toString().substring(0, 8);
        String imageUrl = s3Service.uploadFile(name+"-"+randomString,image);
        Clan clan = Clan.builder()
                .joinClanUrl(frontEndUrl+"/joinClan")
                .clanName(name)
                .clanDescription(description)
                .clanXp(0L)
                .clanRank(null)
                .clanBadgeImageUrl(imageUrl)
                .clanLevel(1)
                .currentMaxXp(50L)
                .clanOwnerId(userId)
                .build();
        Clan savedClan = clanRepository.save(clan);
        userFeignProxy.addUserClan(userId,savedClan.getClanId(),authHeader);
    }

    @Override
    @Transactional
    public void joinClan(Long clanId, Long userId,String authHeader) {
        if(!clanRepository.findClansForUser(userId).isEmpty()){
            throw new ClanException("User already exists in a clan.. Please leave the clan before creating a clan");
        }
        Clan clan = clanRepository.findById(clanId).orElseThrow(() -> new ClanNotFoundException("Clan ("+clanId+") does not exists !!!"));
        clan.getMembers().add(userId);
        AddUserClanResponse clanResponse = userFeignProxy.addUserClan(userId,clan.getClanId(),authHeader).getBody();
        log.info("Response : {}",clanResponse);
        increaseXp(clan,clanResponse.getClanXp());
        clanRepository.save(clan);
    }
    @Override
    public List<LeaderboardDto> fetchGlobalClanLeaderboardInfos() {
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "clanLevel", "clanXp"));
        Page<Clan> topUsers = clanRepository.findByIsDeletedFalseOrderByClanLevelDescClanXpDesc(pageRequest);
        return topUsers.stream().map(LeaderboardDto::new).toList();
    }

    @Override
    @Transactional
    public void leaveClan(Long clanId, long userId,String authHeader) {
        Clan clan = clanRepository.findById(clanId).orElseThrow(() -> new ClanNotFoundException("Clan ("+clanId+") does not exists !!!"));
        if (clan.getClanOwnerId().equals(userId)) {
            throw new ClanException("Clan owner cannot leave the clan. Please disband the clan instead or give the ownership to another user before leaving.");
        }
        if(!clan.getMembers().contains(userId)){
            throw new ClanException("User is not present in this clan to leave this clan...");
        }
        clan.getMembers().remove(userId);
        AddUserClanResponse clanResponse = userFeignProxy.removeUserClan(userId,clan.getClanId(),authHeader).getBody();
        decreaseXp(clan,clanResponse.getClanXp());
        clanRepository.save(clan);
    }

    @Override
    public void switchOwnership(Long clanId, Long toUserId, long userId, String authHeader) {
        Clan clan = clanRepository.findById(clanId).orElseThrow(() -> new ClanNotFoundException("Clan ("+clanId+") does not exists !!!"));
        if (toUserId.equals(userId)) {
            throw new ClanException("Cannot transfer ownership to the same user");
        }
        if (!clan.getClanOwnerId().equals(userId)) {
            throw new ClanException("User "+userId+"is not the owner of the clan");
        }
        if(!clan.getMembers().contains(toUserId)){
            throw new ClanException("User ("+toUserId+") is not present in this clan to give the ownership to...");
        }
        clan.setClanOwnerId(toUserId);
        clan.getMembers().remove(toUserId);
        clan.getMembers().add(userId);
        clanRepository.save(clan);
    }

    @Override
    public ClanDto getClanInfo(Long clanId) {
        Clan clan =  clanRepository.findById(clanId).orElseThrow(() -> new ClanNotFoundException("Clan with ("+clanId+") does not exists"));
        return new ClanDto(clan);
    }

    @Override
    @Transactional
    public void disband(Long clanId, long userId, String authHeader) {
        Clan clan = clanRepository.findById(clanId).orElseThrow(() -> new ClanNotFoundException("Clan ("+clanId+") does not exists !!!"));
        if (!clan.getClanOwnerId().equals(userId)) {
            throw new ClanException("Only clan owners can disband the clan !!!");
        }
        userFeignProxy.removeClanFromUsers(clan.getClanId(),authHeader);
        clanRepository.delete(clan);
    }
    @RabbitListener(queues = "clan_level_queue", ackMode = "MANUAL")
    public   void changeUserPremiumStatus(UserLevelChange response, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            Optional<Clan> clanOptional = clanRepository.findById(response.getClan());
            Supplier<? extends RuntimeException> noClanException =
                    () -> new ClanNotFoundException("User not found with ID: " + response.getClan());
            log.info("Increasing clan Level.. : {}",response);
            Clan clan = clanOptional.orElseThrow(noClanException);
            increaseXp(clan, Long.valueOf(response.getXp()));
            channel.basicAck(tag, false);
        }
        catch (ClanNotFoundException e){
            log.warn("User not found ");
            throw e;
        }
        catch (Exception e){
            log.error(e.getMessage());
            channel.basicNack(tag, false, true);
        }
    }
    protected void increaseXp(Clan clan,Long incrementXp){
        long newXp = clan.getClanXp() + incrementXp;
        while (newXp >= clan.getCurrentMaxXp()) {
            long prevMaxXp = clan.getCurrentMaxXp();
            levelUp(clan);
            newXp = Math.abs(newXp - prevMaxXp);
        }
        clan.setClanXp(newXp);
        clanRepository.save(clan);
        updateClanRank(clan);
    }
    protected void decreaseXp(Clan clan, Long decrementXp) {
        long newXp =  decrementXp - clan.getClanXp();
        while (newXp < 0) {
            levelDown(clan);
            newXp += clan.getClanXp() - 50;
        }
        clan.setClanXp(newXp);
        clanRepository.save(clan);
        updateClanRank(clan);
    }

    private void levelDown(Clan clan) {
        clan.setClanLevel(clan.getClanLevel() - 1);
        clan.setCurrentMaxXp(clan.getClanLevel() * 50L);
    }
    private void updateClanRank(Clan clan) {
        clanRepository.updateAllClanRank();
    }
    private void levelUp(Clan clan) {
        clan.setClanLevel(clan.getClanLevel() + 1);
        clan.setCurrentMaxXp(clan.getClanLevel() * 50L);
        clan.setClanXp(0L);
    }

}
