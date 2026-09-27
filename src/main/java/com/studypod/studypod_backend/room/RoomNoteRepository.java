package com.studypod.studypod_backend.room;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomNoteRepository extends JpaRepository<RoomNote, String> {
    List<RoomNote> findByRoomIdOrderByUpdatedAtDesc(String roomId);
}