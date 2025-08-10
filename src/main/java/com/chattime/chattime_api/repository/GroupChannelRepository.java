package com.chattime.chattime_api.repository;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.GroupChannel;
import com.chattime.chattime_api.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface GroupChannelRepository extends JpaRepository<GroupChannel, Long> {

    /**
     * Finds a GroupsChannels entry by group ID and channel ID.
     *
     * @param group The ID of the group.
     * @param channel The ID of the channel.
     * @return An Optional containing the GroupsChannels entity if found, otherwise empty.
     */
    GroupChannel findByGroupAndChannel(Group group, Channel channel);

    @Modifying
    @Query("update GroupChannel gc set gc.displayOrder = gc.displayOrder + :shift " +
            "where gc.displayOrder >= :start and gc.displayOrder <= :end and gc.channel = :channel")
    void updateDisplayOrderInBatch(
        @Param("channel") Channel channel,
        @Param("start") int start,
        @Param("end") int end,
        @Param("shift") int shift
    );

    @Query("SELECT MAX(gc.displayOrder) FROM GroupChannel gc " +
            "WHERE gc.channel = :channel")
    Integer findMaxDisplayOrder(
            @Param("channel") Channel channel
    );
}
