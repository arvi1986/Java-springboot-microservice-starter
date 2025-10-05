package com.starter.sample.service;

import com.starter.sample.domain.Folder;
import com.starter.sample.domain.User;
import com.starter.sample.dto.FolderDto;
import com.starter.sample.dto.UserDto;
import com.starter.sample.repository.FolderRepository;
import com.starter.sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FolderService {

    private static final Logger logger = LoggerFactory.getLogger(FolderService.class);

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FolderDto> getAllFolders() {
        return folderRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public FolderDto getFolderById(Long id) {
        return folderRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
    }

    @Transactional
    public FolderDto createFolder(FolderDto folderDto) {
        Folder folder = Folder.builder()
            .path(folderDto.path())
            .owner(folderDto.owner() != null ? convertToUserEntity(folderDto.owner()) : null)
            .sharedWith(folderDto.sharedWith() != null ? Set.copyOf(folderDto.sharedWith().stream().map(this::convertToUserEntity).toList()) : Set.of())
            .files(Set.of())
            .createdAt(folderDto.createdAt())
            .updatedAt(folderDto.updatedAt())
            .build();
        folder = folderRepository.save(folder);
        return convertToDto(folder);
    }

    @Transactional
    public FolderDto updateFolder(Long id, FolderDto folderDto) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        Folder updated = Folder.builder()
            .id(folder.getId())
            .path(folderDto.path())
            .owner(folderDto.owner() != null ? convertToUserEntity(folderDto.owner()) : folder.getOwner())
            .sharedWith(folderDto.sharedWith() != null ? Set.copyOf(folderDto.sharedWith().stream().map(this::convertToUserEntity).toList()) : folder.getSharedWith())
            .files(folder.getFiles())
            .createdAt(folder.getCreatedAt())
            .updatedAt(folderDto.updatedAt() != null ? folderDto.updatedAt() : folder.getUpdatedAt())
            .build();
        updated = folderRepository.save(updated);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteFolder(Long id) {
        folderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public byte[] downloadFolderAsZip(String folderPath, String requesterEmail) {
        if (folderPath == null || folderPath.isBlank()) {
            throw new IllegalArgumentException("Folder path must not be blank");
        }
        final User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        final Folder folder = folderRepository.findByPathAndOwner(folderPath, requester)
                .orElseGet(() -> folderRepository.findBySharedWithContaining(requester).stream()
                        .filter(f -> f.getPath().equals(folderPath))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Folder not found or not accessible")));
        // Actual ZIP logic
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
             java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos)) {
            for (com.starter.sample.domain.File file : folder.getFiles()) {
                java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(file.getName());
                zos.putNextEntry(entry);
                // TODO: Read file bytes from storage and write to zos
                // Example: Read file bytes from disk or storage
                java.nio.file.Path filePathOnDisk = java.nio.file.Paths.get(file.getPath());
                if (java.nio.file.Files.exists(filePathOnDisk)) {
                    byte[] fileBytes = java.nio.file.Files.readAllBytes(filePathOnDisk);
                    zos.write(fileBytes);
                } else {
                    zos.write(new byte[0]); // If file not found, write empty
                }
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to create ZIP", e);
        }
    }

    @Transactional
    public FolderDto shareFolder(String folderPath, List<String> emails, String ownerEmail) {
        if (folderPath == null || folderPath.isBlank()) {
            throw new IllegalArgumentException("Folder path must not be blank");
        }
        if (emails == null || emails.isEmpty()) {
            throw new IllegalArgumentException("Emails list must not be empty");
        }
        final User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        final Folder folder = folderRepository.findByPathAndOwner(folderPath, owner)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found or not owned by user"));
        // Only the owner can share the folder
        if (!folder.getOwner().getId().equals(owner.getId())) {
            throw new IllegalArgumentException("Only the folder owner can share the folder");
        }
        // Validate emails and get User entities
        Set<User> sharedUsers = emails.stream()
                .map(email -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email)))
                .collect(java.util.stream.Collectors.toSet());
        // Update sharedWith set using clear/addAll pattern
        folder.getSharedWith().clear();
        folder.getSharedWith().addAll(sharedUsers);
        folderRepository.save(folder);
        return convertToDto(folder);
    }

    private FolderDto convertToDto(Folder folder) {
        return new FolderDto(
            folder.getId(),
            folder.getPath(),
            folder.getOwner() != null ? convertToUserDto(folder.getOwner()) : null,
            folder.getSharedWith() != null ? Set.copyOf(folder.getSharedWith().stream().map(this::convertToUserDto).toList()) : Set.of(),
            folder.getCreatedAt(),
            folder.getUpdatedAt()
        );
    }

    private Folder convertToEntity(FolderDto folderDto) {
        return Folder.builder()
            .id(folderDto.id())
            .path(folderDto.path())
            .owner(folderDto.owner() != null ? convertToUserEntity(folderDto.owner()) : null)
            .sharedWith(folderDto.sharedWith() != null ? Set.copyOf(folderDto.sharedWith().stream().map(this::convertToUserEntity).toList()) : Set.of())
            .files(Set.of())
            .createdAt(folderDto.createdAt())
            .updatedAt(folderDto.updatedAt())
            .build();
    }

    private User convertToUserEntity(UserDto userDto) {
        return User.builder()
            .id(userDto.id())
            .email(userDto.email())
            .name(userDto.name())
            .roles(userDto.roles() != null ? Set.copyOf(userDto.roles()) : Set.of())
            .build();
    }

    private UserDto convertToUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName(), user.getRoles() != null ? Set.copyOf(user.getRoles()) : Set.of());
    }
}
