package com.starter.sample.repository;

import com.starter.sample.domain.Folder;
import com.starter.sample.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByPathAndOwner(String path, User owner);
    boolean existsByPathAndOwner(String path, User owner);
    Set<Folder> findBySharedWithContaining(User user);
}

