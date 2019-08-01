package pl.coderstrust.soap;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;
import static org.springframework.ws.test.server.ResponseMatchers.validPayload;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.ws.test.server.MockWebServiceClient;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InvoiceEndpointTest {

    @Autowired
    private WebApplicationContext applicationContext;

    private Resource xsdSchema = new ClassPathResource("invoices.xsd");
    private MockWebServiceClient mockClient;

    @BeforeEach
    public void init() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

    @Test
    @Order(1)
    void shouldAddInvoice() throws IOException {
        testComplianceOfRequestAndResponse("addInvoiceRequest", "addInvoiceResponse");
    }

    @Test
    @Order(2)
    void shouldAddAnotherInvoice() throws IOException {
        testComplianceOfRequestAndResponse("addAnotherInvoiceRequest", "addAnotherInvoiceResponse");
    }

    @Test
    @Order(3)
    void shouldAddInvoiceReturnStatusFailedWhenInvoiceAlreadyExists() throws IOException {
        testComplianceOfRequestAndResponse("addAnotherInvoiceRequest", "addInvoiceOnceMoreResponse");
    }

    @Test
    @Order(4)
    void shouldGetAllInvoices() throws IOException {
        testComplianceOfRequestAndResponse("getAllInvoicesRequest", "getAllInvoicesResponse");
    }

    @Test
    @Order(5)
    void shouldGetInvoiceById() throws IOException {
        testComplianceOfRequestAndResponse("getInvoiceByIdRequest", "getInvoiceByIdResponse");
    }

    @Test
    @Order(6)
    void shouldGetInvoiceByIdReturnFailedStatusWhenNonExistingInvoiceIsRetrieved() throws IOException {
        testComplianceOfRequestAndResponse("getNonExistingInvoiceByIdRequest", "getNonExistingInvoiceByIdResponse");
    }

    @Test
    @Order(7)
    void shouldUpdateInvoice() throws IOException {
        testComplianceOfRequestAndResponse("updateInvoiceRequest", "updateInvoiceResponse");
    }

    @Test
    @Order(8)
    void shouldUpdateInvoiceReturnFailedStatusWhenIdsAreNonCompliant() throws IOException {
        testComplianceOfRequestAndResponse("updateInvoiceWithNonCompliantIdRequest", "updateInvoiceWithNonCompliantResponse");
    }

    @Test
    @Order(9)
    void shouldUpdateInvoiceReturnFailedStatusWhenTryingToUpdateNonExistingInvoice() throws IOException {
        testComplianceOfRequestAndResponse("updateNonExistingInvoiceRequest", "updateNonExistingInvoiceResponse");
    }

    @Test
    @Order(10)
    void shouldGetInvoiceByNumber() throws IOException {
        testComplianceOfRequestAndResponse("getInvoiceByNumberRequest", "getInvoiceByNumberResponse");
    }

    @Test
    @Order(11)
    void shouldGetInvoiceByNumberReturnFailedStatusTryingToGetNonExistingInvoice() throws IOException {
        testComplianceOfRequestAndResponse("getInvoiceWithIncorrectNumberRequest", "getInvoiceWithIncorrectNumberResponse");
    }

    @Test
    @Order(12)
    void shouldDeleteInvoiceById() throws IOException {
        testComplianceOfRequestAndResponse("deleteInvoiceByIdRequest", "deleteInvoiceByIdResponse");
    }

    @Test
    @Order(13)
    void shouldDeleteInvoiceByIdReturnStatusFailedWhenInvoiceNotExists() throws IOException {
        testComplianceOfRequestAndResponse("deleteInvoiceByIdRequest", "deleteNonExistingInvoiceByIdResponse");
    }

    @Test
    @Order(14)
    void shouldAddAnotherInvoiceAgain() throws IOException {
        testComplianceOfRequestAndResponse("addAnotherInvoiceRequest", "addAnotherInvoiceAgainResponse");
    }

    @Test
    @Order(15)
    void shouldDeleteAllInvoices() throws IOException {
        testComplianceOfRequestAndResponse("deleteAllInvoicesRequest", "deleteAllInvoicesResponse");
    }

    @Test
    @Order(16)
    void shouldAddInvoiceWithoutIdSpecified() throws IOException {
        testComplianceOfRequestAndResponse("addInvoiceWithoutIdRequest", "addInvoiceWithoutIdResponse");
    }

    private void testComplianceOfRequestAndResponse(String requestFile, String responseFile) throws IOException {
        Resource requestPayload = applicationContext.getResource("classpath:soap/" + requestFile + ".xml");
        Resource responsePayload = applicationContext.getResource("classpath:soap/" + responseFile + ".xml");

        mockClient
            .sendRequest(withPayload(requestPayload))
            .andExpect(noFault())
            .andExpect(payload(responsePayload));
    }
}
