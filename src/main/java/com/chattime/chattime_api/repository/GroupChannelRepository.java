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

    /**
     * Updates the last message ID and increments the unread count for a specific group-channel entry.
     *
     * @param lastMessage The ID of the new last message.
     * @param group The ID of the group.
     * @param channel The ID of the channel.
     * @return The number of entities updated.
     */
    @Modifying
    @Transactional
    @Query("UPDATE GroupChannel gc SET gc.lastMessage = :lastMessage, gc.unread = gc.unread + 1 " +
            "WHERE gc.group = :group AND gc.channel = :channel")
    int updateGroupChannel(@Param("lastMessage") Message lastMessage,
                                            @Param("group") Group group,
                                            @Param("channel") Channel channel);
}
