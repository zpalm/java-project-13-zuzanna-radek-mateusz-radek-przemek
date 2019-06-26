package pl.coderstrust.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileHelperIT {
    private static final String INPUT_FILE = "src/test/resources/helpers/input_file.txt";
    private static final String EXPECTED_FILE = "src/test/resources/helpers/expected_file.txt";
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
    void shouldCreateAnEmptyFile() throws IOException {
        fileHelper.create(INPUT_FILE);
        Assertions.assertTrue(Files.exists(Paths.get(INPUT_FILE)));
    }

    @Test
    void shouldReturnTrueForExistingFile() throws IOException {
        inputFile.createNewFile();
        Assertions.assertTrue(fileHelper.exists(INPUT_FILE));
    }

    @Test
    void shouldWriteToAFile() throws IOException {
        List<String> testInput = Arrays.asList(
            "ID",
            "22/2019",
            "2019-06-25",
            "2019-07-25",
            "Seller's details",
            "Buyer's details"
        );
        for (String input : testInput) {
            fileHelper.writeLine(INPUT_FILE, input);
        }
        FileUtils.writeLines(expectedFile, "UTF-8", testInput, false);
        List<String> result = Files.readAllLines(Paths.get(INPUT_FILE));
        List<String> expected = Files.readAllLines(Paths.get(EXPECTED_FILE));
        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldReturnFalseForFileWithContent() throws IOException {
        FileUtils.write(inputFile, "Input", "UTF-8");
        Assertions.assertFalse(fileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void shouldReadLastLineOfAFile() throws IOException {
        List<String> testInput = Arrays.asList(
            "ID",
            "22/2019",
            "2019-06-25",
            "2019-07-25",
            "Seller's details",
            "Buyer's details"
        );
        FileUtils.writeLines(inputFile, "UTF-8", testInput, false);
        String lastLine = fileHelper.readLastLine(INPUT_FILE);
        Assertions.assertEquals("Buyer's details", lastLine);
    }

    @Test
    void shouldRemoveCertainLineFromAFile() throws IOException {
        List<String> testInput = new ArrayList<>(Arrays.asList(
            "ID",
            "22/2019",
            "2019-06-25",
            "2019-07-25",
            "Seller's details",
            "Buyer's details"
        ));
        FileUtils.writeLines(inputFile, "UTF-8", testInput, false);
        testInput.remove(3);
        FileUtils.writeLines(expectedFile, "UTF-8", testInput, false);
        fileHelper.removeLine(INPUT_FILE, 4);
        List<String> result = Files.readAllLines(Paths.get(INPUT_FILE));
        List<String> expected = Files.readAllLines(Paths.get(EXPECTED_FILE));
        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldReadAllLinesFromFile() throws IOException {
        List<String> testInput = Arrays.asList(
            "ID",
            "22/2019",
            "2019-06-25",
            "2019-07-25",
            "Seller's details",
            "Buyer's details"
        );
        FileUtils.writeLines(inputFile, "UTF-8", testInput, false);
        FileUtils.writeLines(expectedFile, "UTF-8", testInput, false);
        List<String> result = fileHelper.readLines(INPUT_FILE);
        List<String> expected = Files.readAllLines(Paths.get(EXPECTED_FILE));
        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldClearContentOfAFile() throws IOException {
        FileUtils.write(inputFile, "Input", "UTF-8");
        fileHelper.clear(INPUT_FILE);
        List<String> expected = new ArrayList<>();
        List<String> result = Files.readAllLines(Paths.get(INPUT_FILE));
        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldReturnTrueForAnEmptyFile() throws IOException {
        inputFile.createNewFile();
        Assertions.assertTrue(fileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void shouldDeleteAFile() throws IOException {
        inputFile.createNewFile();
        fileHelper.delete(INPUT_FILE);
        Assertions.assertFalse(Files.exists(Paths.get(INPUT_FILE)));
    }

    @Test
    void shouldReturnFalseForNonExistingFile() {
        Assertions.assertFalse(fileHelper.exists(INPUT_FILE));
    }

    @Test
    public void createMethodShouldThrowExceptionForNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileHelper.create(null));
    }

    @Test
    public void deleteMethodShouldThrowExceptionForNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileHelper.delete(null));
    }

    @Test
    public void existsMethodShouldThrowExceptionForNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileHelper.exists(null));
    }

    @Test
    public void isEmptyMethodShouldThrowExceptionForNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileHelper.isEmpty(null));
    }

    @Test
    public void clearMethodShouldThrowExceptionForNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileHelper.clear(null));
    }

    @Test
    public void writeLineMethodShouldThrowExceptionForNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileHelper.writeLine(null, null));
    }

    @Test
    public void readLinesMethodShouldThrowExceptionForNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileHelper.readLines(null));
    }

    @Test
    public void readLastLineMethodShouldThrowExceptionForNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileHelper.readLastLine(null));
    }

    @Test
    public void removeLineMethodShouldThrowExceptionForNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileHelper.removeLine(null, 1));
    }

    @Test
    public void createMethodShouldThrowExceptionForExistingFile() throws IOException {
        inputFile.createNewFile();
        Assertions.assertThrows(FileAlreadyExistsException.class, () -> fileHelper.create(INPUT_FILE));
    }

    @Test
    public void deleteMethodShouldThrowExceptionForNonExistingFile() {
        Assertions.assertThrows(FileNotFoundException.class, () -> fileHelper.delete(INPUT_FILE));
    }

    @Test
    public void isEmptyMethodShouldThrowExceptionForNonExistingFile() {
        Assertions.assertThrows(FileNotFoundException.class, () -> fileHelper.isEmpty(INPUT_FILE));
    }
}
