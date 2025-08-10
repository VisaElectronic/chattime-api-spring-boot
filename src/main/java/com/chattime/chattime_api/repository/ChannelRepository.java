package com.chattime.chattime_api.repository;

import com.chattime.chattime_api.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Channel findByName(String name);

    Channel findByKey(String key);

    List<Channel> findAllByStatus(int status);

    List<Channel> findByGroupsId(Long id);

    @Query("SELECT c FROM Channel c JOIN c.groups g ON g.id = :id WHERE c.key != :key")
    List<Channel> findByGroupsIdAndNotKey(@Param("id") Long id, @Param("key") String key);

    List<Channel> findAllByKeyIn(Collection<String> keys);

    @Query("""
      SELECT c
      FROM   Channel c
      WHERE  c NOT IN (
        SELECT ch
        FROM   Group g
        JOIN   g.channels ch
        WHERE  g.key = :key
      )
    """)
    List<Channel> findAllNotInGroup(@Param("key") String key);

    @Query("SELECT c FROM Channel c JOIN c.groups g ON g.id = :groupId")
    List<Channel> findAllByGroupId(@Param("groupId") Long groupId);
}
