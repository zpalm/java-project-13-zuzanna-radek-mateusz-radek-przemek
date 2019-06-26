package pl.coderstrust.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileHelperTest {
    private String resourcePath = "src/test/resources/test.txt";
    private FileHelper fileHelper;

    @BeforeEach
    void beforeEach() {
        fileHelper = new FileHelper();
    }

    @Test
    @Order(1)
    void shouldCreateAnEmptyFile() throws IOException {
        fileHelper.create(resourcePath);
        Assertions.assertTrue(Files.exists(Paths.get(resourcePath)));
    }

    @Test
    @Order(2)
    void shouldReturnTrueForExistingFile() {
        Assertions.assertTrue(fileHelper.exists(resourcePath));
    }

    @Test
    @Order(3)
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
            fileHelper.writeLine(resourcePath, input);
        }
        List<String> result = Files.readAllLines(Paths.get(resourcePath));
        Assertions.assertEquals(testInput, result);
    }

    @Test
    @Order(4)
    void shouldReturnFalseForFileWithContent() {
        Assertions.assertFalse(fileHelper.isEmpty(resourcePath));
    }

    @Test
    @Order(5)
    void shouldReadLastLineOfAFile() throws IOException {
        String lastLine = fileHelper.readLastLine(resourcePath);
        Assertions.assertEquals("Buyer's details", lastLine);
    }

    @Test
    @Order(6)
    void shouldRemoveCertainLineFromAFile() throws IOException {
        fileHelper.removeLine(resourcePath, 3);
        List<String> result = Files.readAllLines(Paths.get(resourcePath));
        List<String> expected = Files.readAllLines(Paths.get("src/test/resources/expectedResult.txt"));
        for (int i = 0; i < result.size(); i++) {
            Assertions.assertEquals(expected.get(i), result.get(i));
        }
    }

    @Test
    @Order(7)
    void shouldReadAllLinesFromFile() throws IOException {
        List<String> result = fileHelper.readLines(resourcePath);
        List<String> expected = Files.readAllLines(Paths.get("src/test/resources/expectedResult.txt"));
        for (int i = 0; i < result.size(); i++) {
            Assertions.assertEquals(expected.get(i), result.get(i));
        }
    }

    @Test
    @Order(8)
    void shouldClearContentOfAFile() throws IOException {
        fileHelper.clear(resourcePath);
        List<String> expected = new ArrayList<>();
        List<String> result = Files.readAllLines(Paths.get(resourcePath));
        Assertions.assertEquals(expected, result);
    }

    @Test
    @Order(9)
    void shouldReturnTrueForAnEmptyFile() {
        Assertions.assertTrue(fileHelper.isEmpty(resourcePath));
    }

    @Test
    @Order(10)
    void shouldDeleteAFile() throws IOException {
        fileHelper.delete(resourcePath);
        Assertions.assertFalse(Files.exists(Paths.get(resourcePath)));
    }

    @Test
    @Order(11)
    void shouldReturnFalseForNonExistingFile() {
        Assertions.assertFalse(fileHelper.exists(resourcePath));
    }
}
