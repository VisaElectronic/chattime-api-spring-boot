package com.chattime.chattime_api.repository;

import com.chattime.chattime_api.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByKey(String key);
}
