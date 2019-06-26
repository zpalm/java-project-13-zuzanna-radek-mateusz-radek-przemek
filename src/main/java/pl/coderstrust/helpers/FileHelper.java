package pl.coderstrust.helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class FileHelper {

    void create(String filePath) throws IOException {
        Files.createFile(Paths.get(filePath));
    }

    void delete(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    boolean isEmpty(String filePath) {
        File file = new File(filePath);
        return (file.length() == 0);
    }

    void clear(String filePath) throws IOException {
        Files.write(Paths.get(filePath), "".getBytes());
    }

    void writeLine(String filePath, String line) throws IOException {
        Files.write(Paths.get(filePath), (line + "\n").getBytes(), StandardOpenOption.APPEND);
    }

    List<String> readLines(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath)).collect(Collectors.toList());
    }

    String readLastLine(String filePath) throws IOException {
        File file = new File(filePath);
        StringBuilder builder = new StringBuilder();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(file.length() - 1);
            for (long pointer = file.length() - 1; pointer >= 0; pointer--) {
                randomAccessFile.seek(pointer);
                char c = (char) randomAccessFile.read();
                if (pointer == file.length() - 1 && c == '\n') {
                    continue;
                }
                if (c == '\n') {
                    break;
                }
                builder.append(c);
            }
        }
        return builder.reverse().toString();
    }

    void removeLine(String filePath, int lineNumber) throws IOException {
        File file = new File(filePath);
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < lines.size(); i++) {
                if (i == lineNumber) {
                    continue;
                }
                writer.write(lines.get(i) + "\n");
            }
        }
    }
}
