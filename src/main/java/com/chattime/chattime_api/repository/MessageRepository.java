package com.chattime.chattime_api.repository;

import com.chattime.chattime_api.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByGroupId(Long groupId);

    @Query("""
      SELECT m
        FROM Message m
       WHERE m.group.id = :groupId
    """)
    List<Message> paginateByGroupId(
        @Param("groupId") Long groupId,
        Pageable pageable
    );
}
