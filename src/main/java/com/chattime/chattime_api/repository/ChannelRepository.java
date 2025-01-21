package com.chattime.chattime_api.repository;

import com.chattime.chattime_api.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Channel findByName(String name);

    Channel findByKey(String key);

    List<Channel> findAllByStatus(int status);
}
