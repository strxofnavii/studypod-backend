package com.studypod.studypod_backend.room;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {
    Optional<Room> findByJoinCode(String joinCode);
    boolean existsByJoinCode(String joinCode);
}