package com.studypod.studypod_backend.room;

import com.studypod.studypod_backend.room.dto.CreateRoomRequest;
import com.studypod.studypod_backend.room.dto.RoomMemberResponse;
import com.studypod.studypod_backend.room.dto.RoomResponse;
import com.studypod.studypod_backend.user.User;
import com.studypod.studypod_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomMemberRepository roomMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomService roomService;

    // ---------- Create ----------

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Room name is required");
        }

        Room room = roomService.createRoom(request.getName(), request.getDescription(), userId);
        return ResponseEntity.ok(toRoomResponse(room, userId));
    }

    // ---------- List my rooms ----------

    @GetMapping("/my")
    public ResponseEntity<List<RoomResponse>> getMyRooms(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        List<RoomResponse> rooms = roomMemberRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(member -> roomRepository.findById(member.getRoomId()).orElse(null))
                .filter(room -> room != null)
                .map(room -> toRoomResponse(room, userId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(rooms);
    }

    // ---------- Room details ----------

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomDetails(@PathVariable String roomId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        return roomRepository.findById(roomId)
                .map(room -> ResponseEntity.ok(toRoomResponse(room, userId)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------- Join ----------

    @PostMapping("/join/{joinCode}")
    public ResponseEntity<?> joinRoom(@PathVariable String joinCode, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        try {
            Room room = roomService.joinRoom(joinCode.toUpperCase(), userId);
            return ResponseEntity.ok(toRoomResponse(room, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ---------- Leave ----------

    @PostMapping("/{roomId}/leave")
    public ResponseEntity<?> leaveRoom(@PathVariable String roomId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        boolean left = roomService.leaveRoom(roomId, userId);
        if (!left) {
            return ResponseEntity.badRequest().body("You are not an active member of this room");
        }
        return ResponseEntity.ok().build();
    }

    // ---------- Members ----------

    @GetMapping("/{roomId}/members")
    public ResponseEntity<?> getMembers(@PathVariable String roomId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        List<RoomMemberResponse> members = roomMemberRepository.findByRoomIdAndActiveTrue(roomId).stream()
                .map(member -> {
                    User user = userRepository.findById(member.getUserId()).orElse(null);
                    String name = user != null ? user.getName() : "Unknown";
                    int avatarIndex = user != null ? user.getAvatarIndex() : 0;
                    return new RoomMemberResponse(
                            member.getUserId(), name, avatarIndex, member.getRole(), member.getJoinedAt()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(members);
    }

    // ---------- Helper ----------

    private RoomResponse toRoomResponse(Room room, String requestingUserId) {
        int memberCount = roomMemberRepository.findByRoomIdAndActiveTrue(room.getId()).size();
        String myRole = roomMemberRepository.findByRoomIdAndUserId(room.getId(), requestingUserId)
                .map(RoomMember::getRole)
                .orElse(null);

        return new RoomResponse(
                room.getId(), room.getName(), room.getDescription(), room.getJoinCode(),
                room.getCreatedBy(), room.getCreatedAt(), memberCount, myRole
        );
    }
}