package com.studypod.studypod_backend.room;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomMessageRepository extends JpaRepository<RoomMessage, String> {
    List<RoomMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);
}