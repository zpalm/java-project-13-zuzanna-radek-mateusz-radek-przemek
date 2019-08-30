package pl.coderstrust.soap;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.ws.test.server.MockWebServiceClient;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-nosql-test.properties")
class NoSqlInvoiceEndpointIT {

    @Autowired
    private WebApplicationContext applicationContext;

    private Resource xsdSchema = new ClassPathResource("invoices.xsd");
    private MockWebServiceClient mockClient;

    @BeforeEach
    public void init() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

    @ParameterizedTest
    @MethodSource("setOfRequestsAndResponses")
    @Order(1)
    void shouldAddInvoice(String request, String response) throws IOException {
        testComplianceOfRequestAndResponse(request, response);
    }

    private static Stream setOfRequestsAndResponses() {
        return Stream.of(
            Arguments.of("addInvoiceRequest_1", "addInvoiceResponse_1"),
            Arguments.of("addInvoiceRequest_2", "addInvoiceResponse_2"),
            Arguments.of("addInvoiceRequest_3", "addInvoiceResponse_5")
        );
    }

    @Test
    @Order(2)
    void shouldReturnStatusFailedDuringAddingInvoiceWhenInvoiceAlreadyExists() throws IOException {
        testComplianceOfRequestAndResponse("addInvoiceRequest_2", "addInvoiceResponse_3");
    }

    @Test
    @Order(3)
    void shouldGetAllInvoices() throws IOException {
        testComplianceOfRequestAndResponse("getAllInvoicesRequest", "getAllInvoicesResponse");
    }

    @Test
    @Order(4)
    void shouldGetInvoiceById() throws IOException {
        testComplianceOfRequestAndResponse("getInvoiceByIdRequest", "getInvoiceByIdResponse");
    }

    @Test
    @Order(5)
    void shouldReturnStatusFailedDuringGettingInvoiceByIdWhenNonExistingInvoiceIsRetrieved() throws IOException {
        testComplianceOfRequestAndResponse("getNonExistingInvoiceByIdRequest", "getNonExistingInvoiceByIdResponse");
    }

    @Test
    @Order(6)
    void shouldUpdateInvoice() throws IOException {
        testComplianceOfRequestAndResponse("updateInvoiceRequest", "updateInvoiceResponse");
    }

    @Test
    @Order(7)
    void shouldReturnStatusFailedDuringUpdatingInvoiceWhenIdsAreNonCompliant() throws IOException {
        testComplianceOfRequestAndResponse("updateInvoiceWithNonCompliantIdRequest", "updateInvoiceWithNonCompliantIdResponse");
    }

    @Test
    @Order(8)
    void shouldReturnStatusFailedDuringUpdatingInvoiceWhenTryingToUpdateNonExistingInvoice() throws IOException {
        testComplianceOfRequestAndResponse("updateNonExistingInvoiceRequest", "updateNonExistingInvoiceResponse");
    }

    @Test
    @Order(9)
    void shouldGetInvoiceByNumber() throws IOException {
        testComplianceOfRequestAndResponse("getInvoiceByNumberRequest", "getInvoiceByNumberResponse");
    }

    @Test
    @Order(10)
    void shouldReturnStatusFailedDuringGettingInvoiceByNumberWhenTryingToGetNonExistingInvoice() throws IOException {
        testComplianceOfRequestAndResponse("getInvoiceWithIncorrectNumberRequest", "getInvoiceWithIncorrectNumberResponse");
    }

    @Test
    @Order(11)
    void shouldDeleteInvoiceById() throws IOException {
        testComplianceOfRequestAndResponse("deleteInvoiceByIdRequest", "deleteInvoiceByIdResponse");
    }

    @Test
    @Order(12)
    void shouldReturnStatusFailedDuringDeletingInvoiceByIdWhenInvoiceNotExists() throws IOException {
        testComplianceOfRequestAndResponse("deleteInvoiceByIdRequest", "deleteNonExistingInvoiceByIdResponse");
    }

    @Test
    @Order(13)
    void shouldAddAnotherInvoiceAgain() throws IOException {
        testComplianceOfRequestAndResponse("addInvoiceRequest_2", "addInvoiceResponse_4");
    }

    @Test
    @Order(14)
    void shouldDeleteAllInvoices() throws IOException {
        testComplianceOfRequestAndResponse("deleteAllInvoicesRequest", "deleteAllInvoicesResponse");
    }

    private void testComplianceOfRequestAndResponse(String requestFile, String responseFile) throws IOException {
        Resource requestPayload = applicationContext.getResource("classpath:soap/nosql/" + requestFile + ".xml");
        Resource responsePayload = applicationContext.getResource("classpath:soap/nosql/" + responseFile + ".xml");

        mockClient
            .sendRequest(withPayload(requestPayload))
            .andExpect(noFault())
            .andExpect(payload(responsePayload));
    }
}
