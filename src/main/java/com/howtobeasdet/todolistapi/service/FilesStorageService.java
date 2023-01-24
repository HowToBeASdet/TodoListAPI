package com.howtobeasdet.todolistapi.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {

    public void init();

    public void save(String userId, MultipartFile file);

    public Resource load(String filename);

    public void deleteAll();

    public void delete(String userId);

    public Stream<Path> loadAll();
}
