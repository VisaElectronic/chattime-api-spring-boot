package com.chattime.chattime_api.repository;

import com.chattime.chattime_api.model.Group;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByKey(String key);

    @Query("SELECT g FROM Group g JOIN g.channels c WHERE c.key IN (:keys) GROUP BY g HAVING COUNT(DISTINCT c.key) = :keyCount")
    List<Group> findGroupsContainingKeys(@Param("keys") List<String> keys, @Param("keyCount") long keyCount);

    @Query("SELECT g FROM Group g JOIN g.channels c WHERE c.key = :key")
    List<Group> findByChannelsKey(@Param("key") String key);

    @Query("SELECT g FROM Group g JOIN g.channels c JOIN FETCH g.channels WHERE c.key = :key")
    List<Group> findByChannelsKeyAndFetchChannels(@Param("key") String key);

    @Query("SELECT g FROM Group g JOIN g.channels c JOIN FETCH g.channels WHERE g.key = :key")
    List<Group> findByKeyAndFetchChannels(@Param("key") String key);
}
