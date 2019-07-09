package pl.coderstrust.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoiceService;
import pl.coderstrust.service.ServiceOperationException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @MockBean
    private InvoiceService invoiceService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldReturnAllInvoices() throws Exception {
        Collection<Invoice> invoices = Arrays.asList(InvoiceGenerator.getRandomInvoice(), InvoiceGenerator.getRandomInvoice());
        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        String url = "/invoices/";

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(invoices)));

        verify(invoiceService).getAllInvoices();
    }

    @Test
    public void shouldReturnInternalServerErrorDuringGettingAllInvoicesWhenSomethingWentWrongOnServer() throws Exception {
        when(invoiceService.getAllInvoices()).thenThrow(new ServiceOperationException());

        String url = "/invoices/";

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(invoiceService).getAllInvoices();
    }

    @Test
    public void shouldReturnInvoiceById() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.getInvoiceById(invoice.getId())).thenReturn(Optional.of(invoice));

        String url = "/invoices/" + invoice.getId();

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(invoice)));

        verify(invoiceService).getInvoiceById(invoice.getId());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringGettingInvoiceByIdWhenPassedIdIsWrong() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        Long id = invoice.getId() + 1L;
        when(invoiceService.getInvoiceById(id)).thenReturn(Optional.empty());

        String url = "/invoices/" + id;

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(invoiceService).getInvoiceById(id);
    }

    @Test
    public void shouldReturnInternalServerErrorDuringGettingInvoiceByIdWhenSomethingWentWrongOnServer() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.getInvoiceById(invoice.getId())).thenThrow(new ServiceOperationException());

        String url = "/invoices/" + invoice.getId();

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(invoiceService).getInvoiceById(invoice.getId());
    }

    @Test
    public void shouldReturnInvoiceByNumber() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.getInvoiceByNumber(invoice.getNumber())).thenReturn(Optional.of(invoice));

        String url = "/invoices/byNumber?number=" + invoice.getNumber();

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(invoice)));

        verify(invoiceService).getInvoiceByNumber(invoice.getNumber());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringGettingInvoiceByNumberWhenPassedNumberIsWrong() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        String number = invoice.getNumber() + "a";
        when(invoiceService.getInvoiceByNumber(number)).thenReturn(Optional.empty());

        String url = "/invoices/byNumber?number=" + number;

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(invoiceService).getInvoiceByNumber(number);
    }

    @Test
    public void shouldReturnInternalServerErrorDuringGettingInvoiceByNumberWhenSomethingWentWrongOnServer() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.getInvoiceByNumber(invoice.getNumber())).thenThrow(new ServiceOperationException());

        String url = "/invoices/byNumber?number=" + invoice.getNumber();

        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(invoiceService).getInvoiceByNumber(invoice.getNumber());
    }

    @Test
    public void shouldAddInvoice() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.addInvoice(invoice)).thenReturn(invoice);

        String url = "/invoices/";

        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(invoice)));

        verify(invoiceService).addInvoice(invoice);
    }

    @Test
    public void shouldReturnConflictStatusDuringAddingInvoiceWhenInvoiceExistsInDatabase() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.invoiceExists(invoice.getId())).thenReturn(true);

        String url = "/invoices/";

        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());

        verify(invoiceService).invoiceExists(invoice.getId());
    }

    @Test
    public void shouldReturnBadRequestStatusDuringAddingNullAsInvoice() throws Exception {
        String url = "/invoices/";

        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(null))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnInternalServerErrorDuringAddingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.addInvoice(invoice)).thenThrow(new ServiceOperationException());

        String url = "/invoices/";

        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(invoiceService).addInvoice(invoice);
    }

    @Test
    public void shouldUpdateInvoice() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.invoiceExists(invoice.getId())).thenReturn(true);
        when(invoiceService.updateInvoice(invoice)).thenReturn(invoice);

        String url = "/invoices/" + invoice.getId() + "/";

        mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(invoice)));

        verify(invoiceService).invoiceExists(invoice.getId());
        verify(invoiceService).updateInvoice(invoice);
    }

    @Test
    public void shouldReturnBadRequestStatusDuringUpdatingNullAsInvoice() throws Exception {
        String url = "/invoices/1/";

        mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(null))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestStatusDuringUpdatingInvoiceWhenPassedIdIsDifferentThanInvoiceId() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();

        String id = invoice.getId() + "1";
        String url = "/invoices/" + id;

        mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringUpdatingInvoiceWhenInvoiceDoesNotExistInDatabase() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.invoiceExists(invoice.getId())).thenReturn(false);

        String url = "/invoices/" + invoice.getId() + "/";

        mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(invoiceService).invoiceExists(invoice.getId());
    }

    @Test
    public void shouldReturnInternalServerErrorDuringUpdatingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.invoiceExists(invoice.getId())).thenReturn(true);
        when(invoiceService.updateInvoice(invoice)).thenThrow(new ServiceOperationException());

        String url = "/invoices/" + invoice.getId() + "/";

        mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoice))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(invoiceService).invoiceExists(invoice.getId());
        verify(invoiceService).updateInvoice(invoice);
    }

    @Test
    public void shouldRemoveInvoice() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.invoiceExists(invoice.getId())).thenReturn(true);
        doNothing().when(invoiceService).deleteInvoiceById(invoice.getId());

        String url = "/invoices/" + invoice.getId() + "/";

        mockMvc.perform(delete(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(invoiceService).invoiceExists(invoice.getId());
        verify(invoiceService).deleteInvoiceById(invoice.getId());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringRemovingInvoiceWhenInvoiceDoesNotExistInDatabase() throws Exception {
        when(invoiceService.invoiceExists(1L)).thenReturn(false);

        String url = "/invoices/1/";

        mockMvc.perform(delete(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(invoiceService).invoiceExists(1L);
    }

    @Test
    public void shouldReturnInternalServerErrorDuringRemovingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.invoiceExists(invoice.getId())).thenReturn(true);
        doThrow(ServiceOperationException.class).when(invoiceService).deleteInvoiceById(invoice.getId());

        String url = "/invoices/" + invoice.getId() + "/";

        mockMvc.perform(delete(url)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(invoiceService).invoiceExists(invoice.getId());
        verify(invoiceService).deleteInvoiceById(invoice.getId());
    }
}
