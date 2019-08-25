package pl.coderstrust.helpers;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.stereotype.Component;

@Component
public class FileHelper {

    private static final Charset ENCODING = UTF_8;

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
            throw new FileNotFoundException("File does not exist.");
        }
        return (new File(filePath).length() == 0);
    }

    public void clear(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        FileUtils.write(new File(filePath), "", ENCODING);
    }

    public void writeLine(String filePath, String line) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }
        FileUtils.writeLines(new File(filePath), ENCODING.name(), Collections.singleton(line), true);
    }

    public Stream<String> readLines(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        return Files.lines(Paths.get(filePath), ENCODING);
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
        List<String> lines = FileUtils.readLines(file, ENCODING);
        lines.remove(lineNumber - 1);
        FileUtils.writeLines(file, ENCODING.name(), lines, false);
    }

    public void replaceLine(String filePath, String line, int lineNumber) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File's path cannot be null.");
        }
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }
        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number cannot be lower than one.");
        }
        File file = new File(filePath);
        List<String> lines = FileUtils.readLines(file, ENCODING);
        lines.set(lineNumber - 1, line);
        FileUtils.writeLines(file, ENCODING.name(), lines, false);
    }
}
