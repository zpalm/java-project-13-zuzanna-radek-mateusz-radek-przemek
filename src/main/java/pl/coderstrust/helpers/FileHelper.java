package pl.coderstrust.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;

public class FileHelper {

    public void create(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        Files.createFile(Paths.get(filePath));
    }

    public void delete(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        Files.delete(Paths.get(filePath));
    }

    public boolean exists(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        return Files.exists(Paths.get(filePath));
    }

    public boolean isEmpty(String filePath) throws FileNotFoundException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        if (!Files.exists(Paths.get(filePath))) {
            throw new FileNotFoundException("File doesn't exist.");
        }
        return (new File(filePath).length() == 0);
    }

    public void clear(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        FileUtils.write(new File(filePath), "", "UTF-8");
    }

    public void writeLine(String filePath, String line) throws IOException {
        if (filePath == null || line == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        FileUtils.writeLines(new File(filePath), "UTF-8", Collections.singleton(line), true);
    }

    public List<String> readLines(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        return FileUtils.readLines(new File(filePath), "UTF-8");
    }

    public String readLastLine(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(filePath), Charset.defaultCharset())) {
            return reader.readLine();
        }
    }

    public void removeLine(String filePath, int lineNumber) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number cannot be lower than one.");
        }
        File file = new File(filePath);
        List<String> lines = FileUtils.readLines(file, "UTF-8");
        lines.remove(lineNumber - 1);
        FileUtils.writeLines(file, "UTF-8", lines, false);
    }
}
