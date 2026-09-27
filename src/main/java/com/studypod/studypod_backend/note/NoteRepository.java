package com.studypod.studypod_backend.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, String> {
    List<Note> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Note> findByUserIdAndFolderOrderByCreatedAtDesc(String userId, String folder);

    List<Note> findByUserIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(String userId, String title);

    @Query("SELECT DISTINCT n.folder FROM Note n WHERE n.userId = :userId ORDER BY n.folder")
    List<String> findDistinctFoldersByUserId(@Param("userId") String userId);
}