package com.starter.sample.repository;

import com.starter.sample.domain.File;
import com.starter.sample.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface FileRepository extends JpaRepository<File, Long> {
    Set<File> findByFolder(Folder folder);
}


