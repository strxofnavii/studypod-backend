package com.studypod.studypod_backend.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RoomService {

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no 0/O/1/I to avoid confusion
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomMemberRepository roomMemberRepository;

    // ---------- Room creation ----------

    public Room createRoom(String name, String description, String creatorUserId) {
        Room room = new Room();
        room.setName(name);
        room.setDescription(description);
        room.setCreatedBy(creatorUserId);
        room.setJoinCode(generateUniqueJoinCode());
        Room savedRoom = roomRepository.save(room);

        RoomMember host = new RoomMember();
        host.setRoomId(savedRoom.getId());
        host.setUserId(creatorUserId);
        host.setRole("HOST");
        host.setActive(true);
        roomMemberRepository.save(host);

        return savedRoom;
    }

    private String generateUniqueJoinCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (roomRepository.existsByJoinCode(code));
        return code;
    }

    // ---------- Membership ----------

    public boolean isActiveMember(String roomId, String userId) {
        return roomMemberRepository.findByRoomIdAndUserId(roomId, userId)
                .map(RoomMember::isActive)
                .orElse(false);
    }

    public Optional<RoomMember> getMembership(String roomId, String userId) {
        return roomMemberRepository.findByRoomIdAndUserId(roomId, userId);
    }

    public Room joinRoom(String joinCode, String userId) {
        Room room = roomRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room code"));

        Optional<RoomMember> existing = roomMemberRepository.findByRoomIdAndUserId(room.getId(), userId);

        if (existing.isPresent()) {
            RoomMember member = existing.get();
            if (!member.isActive()) {
                member.setActive(true);
                member.setJoinedAt(LocalDateTime.now());
                roomMemberRepository.save(member);
            }
        } else {
            RoomMember member = new RoomMember();
            member.setRoomId(room.getId());
            member.setUserId(userId);
            member.setRole("MEMBER");
            member.setActive(true);
            roomMemberRepository.save(member);
        }

        return room;
    }

    public boolean leaveRoom(String roomId, String userId) {
        Optional<RoomMember> existing = roomMemberRepository.findByRoomIdAndUserId(roomId, userId);
        if (existing.isEmpty() || !existing.get().isActive()) {
            return false;
        }
        RoomMember member = existing.get();
        member.setActive(false);
        roomMemberRepository.save(member);
        return true;
    }
}