package com.studypod.studypod_backend.room;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomTaskRepository extends JpaRepository<RoomTask, String> {
    List<RoomTask> findByRoomIdOrderByCreatedAtAsc(String roomId);
}