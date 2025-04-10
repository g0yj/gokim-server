package com.lms.api.common.controller;

import com.lms.api.common.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  @GetMapping("/download/{fileName}/{originalFileName}")
  public void download(@PathVariable String fileName, @PathVariable String originalFileName,
      HttpServletResponse response) throws IOException {

    File file = fileService.get(fileName);

    response.setContentType("application/download");
    response.setContentLength((int) file.length());
    response.setHeader("Content-Disposition", "attachment; filename=\"" + originalFileName + "\"");

    try (FileInputStream in = new FileInputStream(
        file); OutputStream out = response.getOutputStream()) {
      FileCopyUtils.copy(in, out);
    }
  }
}
