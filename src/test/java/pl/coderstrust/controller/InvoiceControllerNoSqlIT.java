package pl.coderstrust.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.coderstrust.generators.CompanyGenerator;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoicePdfService;
import pl.coderstrust.service.ServiceOperationException;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-nosql-test.properties")
class InvoiceControllerNoSqlIT {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private String testDirPath = "src/test/resources/rest-integration-test/";
    private String expectedFilePath = String.format(testDirPath + "%s", "expected.pdf");
    private String receivedFilePath = String.format(testDirPath + "%s", "received.pdf");

    @BeforeEach
    public void cleanDestinationDirectory() throws IOException {
        if (Files.exists(Paths.get(testDirPath))) {
            FileUtils.cleanDirectory(FileUtils.getFile(testDirPath));
        }
    }

    @Test
    public void shouldReturnAllInvoices() throws Exception {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(2L);
        Invoice invoice3 = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(3L);
        Invoice invoice4 = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(4L);

        List<Invoice> invoices = List.of(invoice1, invoice2, invoice3, invoice4);

        addInvoices(invoices);

        String url = "/invoices";

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(invoices)));
    }

    @Test
    public void shouldReturnEmptyListOfInvoicesWhenThereAreNoInvoicesInTheDatabase() throws Exception {
        Collection<Invoice> invoices = new ArrayList<>();
        String url = "/invoices";
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(invoices)));
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingAllInvoicesWithNotSupportedMediaType() throws Exception {
        String url = "/invoices";
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_XML))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldReturnInvoiceById() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));
        String url = "/invoices/1";

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(invoice)));
    }

    @Test
    public void shouldReturnInvoiceByIdAsJsonIfIsPriorToOtherAcceptedHeaders() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));
        String url = "/invoices/1";

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_PDF))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(invoice)));
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingInvoiceByIdWithNotSupportedMediaType() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));
        String url = "/invoices/1";
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_XML))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringGettingInvoiceByIdWhenInvoiceWithSpecificIdDoesNotExist() throws Exception {
        String url = "/invoices/1";
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnBadRequestStatusDuringGettingInvoiceByNumberWhenNumberIsNull() throws Exception {
        String url = "/invoices/byNumber";
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnInvoiceByNumber() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));
        String url = String.format("/invoices/byNumber?number=%s", invoice.getNumber());

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(invoice)));
    }

    @Test
    public void shouldReturnInvoiceByNumberAsJsonIfIsPriorToOtherAcceptedHeaders() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));
        String url = String.format("/invoices/byNumber?number=%s", invoice.getNumber());

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_PDF))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(invoice)));
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingInvoiceByNumberWithNotSupportedMediaType() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        String url = String.format("/invoices/byNumber?number=%s", invoice.getNumber());
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_XML))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringGettingInvoiceByNumberWhenInvoiceWithSpecificNumberDoesNotExist() throws Exception {
        String number = "1a";
        String url = String.format("/invoices/byNumber?number=%s", number);

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldAddInvoice() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));
    }

    @Test
    void shouldReturnUnsupportedMediaTypeStatusDuringAddingInvoiceWithNotSupportedMediaType() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        String url = "/invoices";

        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_XML)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void shouldReturnConflictStatusDuringAddingInvoiceWhenInvoiceExistsInDatabase() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        String url = "/invoices";
        addInvoices(List.of(invoice));

        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturnBadRequestStatusDuringAddingNullAsInvoice() throws Exception {
        String url = "/invoices";
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(null))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateInvoice() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));
        String url = "/invoices/1";

        Invoice updatedInvoice = Invoice.builder()
            .withId(invoice.getId())
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate().plusDays(2L))
            .withDueDate(invoice.getDueDate().plusMonths(1L))
            .withBuyer(CompanyGenerator.getRandomCompanyWithIdEqualZero())
            .withSeller(CompanyGenerator.getRandomCompanyWithIdEqualZero())
            .withEntries(invoice.getEntries())
            .build();

        mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(updatedInvoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(updatedInvoice)));
    }

    @Test
    void shouldReturnUnsupportedMediaTypeStatusDuringUpdatingInvoiceWithNotSupportedMediaType() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        String url = "/invoices/1";

        mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_XML)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void shouldReturnBadRequestStatusDuringUpdatingNullAsInvoice() throws Exception {
        String url = "/invoices/1";

        mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(null))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestStatusDuringUpdatingInvoiceWhenPassedIdIsDifferentThanInvoiceId() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));
        String urlToUpdate = "/invoices/2";

        mockMvc.perform(put(urlToUpdate)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringUpdatingInvoiceWhenInvoiceDoesNotExistInDatabase() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        String url = "/invoices/1";

        mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldRemoveInvoice() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));
        String url = "/invoices/1";

        mockMvc.perform(delete(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringRemovingInvoiceWhenInvoiceDoesNotExistInDatabase() throws Exception {
        String url = "/invoices/1";
        mockMvc.perform(delete(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnInvoiceAsPdfById() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));

        createExpectedFile(invoice, expectedFilePath);

        String url = "/invoices/1";

        MvcResult mvcResult = mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andReturn();

        writeBytesToFile(mvcResult.getResponse().getContentAsByteArray(), receivedFilePath);

        assertTrue(arePdfFilesEqual(expectedFilePath, receivedFilePath));
    }

    @Test
    public void shouldReturnInvoiceAsPdByIdfWhenPdfIsPriorToOtherAcceptedHeaders() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));

        createExpectedFile(invoice, expectedFilePath);

        String url = "/invoices/1";

        MvcResult mvcResult = mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_PDF, MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andReturn();

        writeBytesToFile(mvcResult.getResponse().getContentAsByteArray(), receivedFilePath);

        assertTrue(arePdfFilesEqual(expectedFilePath, receivedFilePath));
    }

    @Test
    public void shouldReturnNotFoundDuringGettingInvoiceAsPdfWhenInvoiceDoesNotExist() throws Exception {
        String url = "/invoices/1";
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_PDF))
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnInvoiceAsPdfByNumber() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));

        createExpectedFile(invoice, expectedFilePath);

        String url = String.format("/invoices/byNumber?number=%s", invoice.getNumber());

        MvcResult mvcResult = mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andReturn();

        writeBytesToFile(mvcResult.getResponse().getContentAsByteArray(), receivedFilePath);

        assertTrue(arePdfFilesEqual(expectedFilePath, receivedFilePath));
    }

    @Test
    public void shouldReturnInvoiceAsPdfByNumberWhenPdfIsPriorToOtherAcceptedHeaders() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(1L);
        addInvoices(List.of(invoice));

        createExpectedFile(invoice, expectedFilePath);

        String url = String.format("/invoices/byNumber?number=%s", invoice.getNumber());

        MvcResult mvcResult = mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_PDF, MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andReturn();

        writeBytesToFile(mvcResult.getResponse().getContentAsByteArray(), receivedFilePath);

        assertTrue(arePdfFilesEqual(expectedFilePath, receivedFilePath));
    }

    @Test
    public void shouldReturnInvoicesFilteredByIssuedDates() throws Exception {
        LocalDate startDate = LocalDate.of(2019, 8, 26);
        LocalDate endDate = startDate.plusDays(2L);
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithFixedIdsAndIssuedDate(1L, startDate);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithFixedIdsAndIssuedDate(2L, startDate.plusDays(1L));
        Invoice invoice3 = InvoiceGenerator.getRandomInvoiceWithFixedIdsAndIssuedDate(3L, endDate);
        Invoice invoice4 = InvoiceGenerator.getRandomInvoiceWithFixedIdsAndIssuedDate(4L, endDate.plusDays(1L));
        Invoice invoice5 = InvoiceGenerator.getRandomInvoiceWithFixedIdsAndIssuedDate(5L, endDate.plusDays(1L));

        List<Invoice> filteredInvoices = List.of(invoice1, invoice2, invoice3);
        List<Invoice> allInvoices = List.of(invoice1, invoice2, invoice3, invoice4, invoice5);

        addInvoices(allInvoices);

        String url = "/invoices/byIssuedDate";

        mockMvc.perform(get(url)
            .param("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
            .param("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(filteredInvoices)));
    }

    @ParameterizedTest
    @MethodSource("startDatesLaterThanEndDates")
    void getByIssuedDateMethodShouldReturnBadRequestStatusWhenInvalidArgumentsArePassed(LocalDate startDate, LocalDate endDate) throws Exception {
        String url = "/invoices/byIssuedDate";
        mockMvc.perform(get(url)
            .param("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
            .param("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> startDatesLaterThanEndDates() {
        return Stream.of(
            Arguments.of(LocalDate.of(2019, 8, 22), LocalDate.of(2018, 8, 31)),
            Arguments.of(LocalDate.of(2019, 2, 28), LocalDate.of(2019, 1, 31)),
            Arguments.of(LocalDate.of(2019, 2, 28), LocalDate.of(2009, 3, 31))
        );
    }

    @ParameterizedTest
    @MethodSource("nullIssuedDates")
    void getByIssuedDateMethodShouldReturnBadRequestStatusWhenIssuedDateIsNull(String url) throws Exception {
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> nullIssuedDates() {
        return Stream.of(
            Arguments.of("/invoices/byIssuedDate"),
            Arguments.of("/invoices/byIssuedDate?startDate=2019-02-28"),
            Arguments.of("/invoices/byIssuedDate?endDate=2019-03-31")
        );
    }

    @Test
    void getByIssuedDateMethodShouldReturnNotAcceptableStatusWhenNotSupportedMediaTypeRequested() throws Exception {
        LocalDate startDate = LocalDate.of(2019, 8, 26);
        LocalDate endDate = startDate.plusDays(2L);

        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithFixedIdsAndIssuedDate(1L, startDate);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithFixedIdsAndIssuedDate(2L, startDate.plusDays(1L));
        Invoice invoice3 = InvoiceGenerator.getRandomInvoiceWithFixedIdsAndIssuedDate(3L, endDate);
        List<Invoice> invoices = List.of(invoice1, invoice2, invoice3);
        addInvoices(invoices);

        String url = "/invoices/byIssuedDate";

        mockMvc.perform(get(url)
            .param("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
            .param("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
            .accept(MediaType.APPLICATION_XML))
            .andExpect(status().isNotAcceptable());
    }

    private void createExpectedFile(Invoice invoice, String expectedFilePath) throws ServiceOperationException, IOException {
        InvoicePdfService pdfService = new InvoicePdfService();
        byte[] expectedByteArray = pdfService.createPdf(invoice);
        writeBytesToFile(expectedByteArray, expectedFilePath);
    }

    private void addInvoices(List<Invoice> invoices) throws Exception {
        invoices.stream().forEach(invoice -> {
            try {
                mockMvc.perform(post("/invoices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(invoice))
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(header().stringValues("location", String.format("/invoices/%d", invoice.getId())))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(mapper.writeValueAsString(invoice)));
            } catch (Exception e) {
                throw new IllegalStateException("An unexpected error occurred during adding invoices");
            }
        });
    }

    private boolean arePdfFilesEqual(String filePath1, String filePath2) throws IOException {
        PdfReader reader1 = new PdfReader(filePath1);
        PdfReader reader2 = new PdfReader(filePath2);
        int numberOfPages = reader1.getNumberOfPages();
        if (numberOfPages != reader2.getNumberOfPages()) {
            return false;
        }
        for (int i = 1; i <= numberOfPages; i++) {
            if (!Arrays.equals(reader1.getPageContent(i), reader2.getPageContent(i))) {
                return false;
            }
        }
        return true;
    }

    private static void writeBytesToFile(byte[] array, String filePath) throws IOException {
        FileUtils.writeByteArrayToFile(new File(filePath), array);
    }
}
