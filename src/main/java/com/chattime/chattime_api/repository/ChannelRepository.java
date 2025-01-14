package com.chattime.chattime_api.repository;

import com.chattime.chattime_api.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Channel findByName(String name);
}
