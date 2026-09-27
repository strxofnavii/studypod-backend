package com.studypod.studypod_backend.room;

import com.studypod.studypod_backend.room.dto.RoomMessageResponse;
import com.studypod.studypod_backend.room.dto.SendMessageRequest;
import com.studypod.studypod_backend.user.User;
import com.studypod.studypod_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms/{roomId}/chat")
public class RoomChatController {

    @Autowired
    private RoomMessageRepository roomMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomService roomService;

    // ---------- Get chat history ----------

    @GetMapping
    public ResponseEntity<?> getMessages(@PathVariable String roomId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        List<RoomMessageResponse> messages = roomMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId).stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(messages);
    }

    // ---------- Send a message ----------

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @PathVariable String roomId,
            @RequestBody SendMessageRequest request,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        if (request.getContent() == null || request.getContent().isBlank()) {
            return ResponseEntity.badRequest().body("Message content cannot be empty");
        }

        RoomMessage message = new RoomMessage();
        message.setRoomId(roomId);
        message.setUserId(userId);
        message.setContent(request.getContent());

        RoomMessage saved = roomMessageRepository.save(message);
        return ResponseEntity.ok(toMessageResponse(saved));
    }

    // ---------- Helper ----------

    private RoomMessageResponse toMessageResponse(RoomMessage message) {
        User user = userRepository.findById(message.getUserId()).orElse(null);
        String senderName = user != null ? user.getName() : "Unknown";
        int avatarIndex = user != null ? user.getAvatarIndex() : 0;

        return new RoomMessageResponse(
                message.getId(), message.getUserId(), senderName, avatarIndex,
                message.getContent(), message.getCreatedAt()
        );
    }
}