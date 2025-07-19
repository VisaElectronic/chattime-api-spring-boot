package com.chattime.chattime_api.repository;

import com.chattime.chattime_api.model.Channel;
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

    @Query("""
      SELECT g
        FROM Group g
        JOIN g.channels c
       WHERE c.key IN (:keys)
         AND g.isGroup = :isGroup
    GROUP BY g
      HAVING COUNT(DISTINCT c.key) = 2
    """)
    List<Group> findGroupsContainingKeys(@Param("keys") List<String> keys, int isGroup);

    @Query("SELECT g FROM Group g JOIN g.channels c WHERE c.key = :key")
    List<Group> findByChannelsKey(@Param("key") String key);

    @Query("SELECT g FROM Group g JOIN g.channels c JOIN FETCH g.channels WHERE c.key = :key")
    List<Group> findByChannelsKeyAndFetchChannels(@Param("key") String key);

    @Query("""
      SELECT DISTINCT g
      FROM   Group g
      LEFT   JOIN FETCH g.channels
      WHERE  g.key = :key
    """)
    List<Group> findByKeyAndFetchChannels(@Param("key") String key);

    @Query("""
      SELECT DISTINCT g
      FROM Group g
      JOIN g.channels c1
      JOIN c1.createdBy u1
      JOIN g.channels c2
      JOIN c2.createdBy u2
      WHERE u1.id = :userId
        AND (
          LOWER(u2.username)  LIKE LOWER(CONCAT('%', :searchText, '%'))
          OR LOWER(u2.firstname) LIKE LOWER(CONCAT('%', :searchText, '%'))
          OR LOWER(u2.lastname)  LIKE LOWER(CONCAT('%', :searchText, '%'))
        )
    """)
    List<Group> findByUserAndSearchText(
            @Param("userId") Long userId,
            @Param("searchText") String searchText
    );

    @Query("""
      SELECT DISTINCT g
      FROM   Group g
      LEFT   JOIN FETCH g.members
      WHERE  g.key = :key
    """)
    List<Group> findByKeyAndFetchMembers(@Param("key") String key);
}
