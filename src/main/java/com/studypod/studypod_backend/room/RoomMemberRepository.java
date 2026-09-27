package com.studypod.studypod_backend.room;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoomMemberRepository extends JpaRepository<RoomMember, String> {
    Optional<RoomMember> findByRoomIdAndUserId(String roomId, String userId);
    List<RoomMember> findByRoomIdAndActiveTrue(String roomId);
    List<RoomMember> findByUserIdAndActiveTrue(String userId);
}