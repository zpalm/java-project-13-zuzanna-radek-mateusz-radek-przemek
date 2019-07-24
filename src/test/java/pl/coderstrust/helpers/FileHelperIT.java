package pl.coderstrust.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileHelperIT {

    private static final String INPUT_FILE = "src/test/resources/helpers/input_file.txt";
    private static final String EXPECTED_FILE = "src/test/resources/helpers/expected_file.txt";
    private static final String ENCODING = "UTF-8";
    private FileHelper fileHelper;
    private File inputFile;
    private File expectedFile;

    @BeforeEach
    void beforeEach() {
        fileHelper = new FileHelper();
        inputFile = new File(INPUT_FILE);
        if (inputFile.exists()) {
            inputFile.delete();
        }
        expectedFile = new File(EXPECTED_FILE);
        if (expectedFile.exists()) {
            expectedFile.delete();
        }
    }

    @Test
    void shouldCreateFile() throws IOException {
        fileHelper.create(INPUT_FILE);
        assertTrue(Files.exists(Paths.get(INPUT_FILE)));
    }

    @Test
    void shouldReturnTrueIfFileExists() throws IOException {
        inputFile.createNewFile();
        assertTrue(fileHelper.exists(INPUT_FILE));
    }

    @Test
    void shouldWriteLineToFile() throws IOException {
        FileUtils.writeLines(expectedFile, ENCODING, Collections.singleton("test test"), true);
        fileHelper.writeLine(INPUT_FILE, "test test");
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void shouldReplaceLineInFile() throws IOException {
        FileUtils.writeLines(inputFile, ENCODING, Arrays.asList("bla1", "blabla", "bla3"), true);
        FileUtils.writeLines(expectedFile, ENCODING, Arrays.asList("bla1", "bla2", "bla3"), true);
        fileHelper.replaceLine(INPUT_FILE, "bla2", 2);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void shouldReturnFalseIfFileIsNotEmpty() throws IOException {
        FileUtils.write(inputFile, "test", ENCODING);
        assertFalse(fileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void shouldReadLastLineFromFile() throws IOException {
        FileUtils.writeLines(inputFile, ENCODING, Arrays.asList("Seller's details", "2019-06-25", "Buyer's details"), false);
        String result = fileHelper.readLastLine(INPUT_FILE);
        assertEquals("Buyer's details", result);
    }

    @Test
    void shouldRemoveLineFromFile() throws IOException {
        FileUtils.writeLines(inputFile, ENCODING, Arrays.asList("bla1", "bla2", "bla3"), true);
        FileUtils.writeLines(expectedFile, ENCODING, Arrays.asList("bla1", "bla3"), true);
        fileHelper.removeLine(INPUT_FILE, 2);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void shouldReadLinesFromFile() throws IOException {
        List<String> lines = Arrays.asList(
            "ID",
            "22/2019",
            "2019-06-25",
            "2019-07-25",
            "Seller's details",
            "Buyer's details"
        );
        FileUtils.writeLines(inputFile, ENCODING, lines, true);
        List<String> result = fileHelper.readLines(INPUT_FILE);
        assertEquals(lines, result);
    }

    @Test
    void shouldClearFile() throws IOException {
        expectedFile.createNewFile();
        FileUtils.writeLines(inputFile, Collections.singleton("bla bla bla"), ENCODING, true);
        fileHelper.clear(INPUT_FILE);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void shouldReturnTrueIfFileIsEmpty() throws IOException {
        inputFile.createNewFile();
        assertTrue(fileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void shouldDeleteExistingFile() throws IOException {
        inputFile.createNewFile();
        fileHelper.delete(INPUT_FILE);
        assertFalse(Files.exists(Paths.get(INPUT_FILE)));
    }

    @Test
    void shouldReturnFalseIfFileDoesNotExist() {
        assertFalse(fileHelper.exists(INPUT_FILE));
    }

    @Test
    void createMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.create(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.delete(null));
    }

    @Test
    void existsMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.exists(null));
    }

    @Test
    void isEmptyMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.isEmpty(null));
    }

    @Test
    void clearMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.clear(null));
    }

    @Test
    void writeLineMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.writeLine(null, "test"));
    }

    @Test
    void writeLineMethodShouldThrowExceptionForNullLineArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.writeLine(INPUT_FILE, null));
    }

    @Test
    void readLinesMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.readLines(null));
    }

    @Test
    void readLastLineMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.readLastLine(null));
    }

    @Test
    void removeLineMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.removeLine(null, 1));
    }

    @Test
    void removeLineMethodShouldThrowExceptionForLineNumberSmallerThanOneArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.removeLine(INPUT_FILE, 0));
    }

    @Test
    void createMethodShouldThrowExceptionForExistingFile() throws IOException {
        inputFile.createNewFile();
        assertThrows(FileAlreadyExistsException.class, () -> fileHelper.create(INPUT_FILE));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNonExistingFile() {
        assertThrows(NoSuchFileException.class, () -> fileHelper.delete(INPUT_FILE));
    }

    @Test
    void isEmptyMethodShouldThrowExceptionForNonExistingFile() {
        assertThrows(FileNotFoundException.class, () -> fileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void replaceLineMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.replaceLine(null, "bla", 1));
    }

    @Test
    void replaceLineMethodShouldThrowExceptionForNullLineArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.replaceLine(INPUT_FILE, null, 1));
    }

    @Test
    void replaceLineMethodShouldThrowExceptionForForLineNumberSmallerThanOneArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.replaceLine(INPUT_FILE, "bla", 0));
    }
}
