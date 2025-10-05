package com.starter.sample.dto;

import java.util.List;

public record ShareFolderRequest(String folderpath, List<String> emails) {}