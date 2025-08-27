package com.musicplatform.resource.repository;

import com.musicplatform.resource.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // Custom query methods can be added here if needed
    List<Resource> findByFilenameContaining(String filename);
}
