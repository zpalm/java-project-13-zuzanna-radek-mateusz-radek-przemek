package pl.coderstrust.soap;

import java.util.Collection;
import java.util.Optional;
import javax.xml.datatype.DatatypeConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoiceService;
import pl.coderstrust.service.ServiceOperationException;
import pl.coderstrust.soap.bindingclasses.AddInvoiceRequest;
import pl.coderstrust.soap.bindingclasses.DeleteAllInvoicesRequest;
import pl.coderstrust.soap.bindingclasses.DeleteInvoiceByIdRequest;
import pl.coderstrust.soap.bindingclasses.GetAllInvoicesRequest;
import pl.coderstrust.soap.bindingclasses.GetInvoiceByIdRequest;
import pl.coderstrust.soap.bindingclasses.GetInvoiceByNumberRequest;
import pl.coderstrust.soap.bindingclasses.InvoiceResponse;
import pl.coderstrust.soap.bindingclasses.InvoiceSoap;
import pl.coderstrust.soap.bindingclasses.InvoicesResponse;
import pl.coderstrust.soap.bindingclasses.Response;
import pl.coderstrust.soap.bindingclasses.ResponseStatus;
import pl.coderstrust.soap.bindingclasses.UpdateInvoiceRequest;
import pl.coderstrust.soap.mappers.InvoiceMapper;

@Endpoint
public class InvoiceEndpoint {

    private Logger log = LoggerFactory.getLogger(InvoiceEndpoint.class);

    private static final String NAMESPACE_URI = "http://project-13-zuzanna-radek-mateusz-radek-przemek";

    private InvoiceService invoiceService;

    @Autowired
    public InvoiceEndpoint(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllInvoicesRequest")
    @ResponsePayload
    public InvoicesResponse getAllInvoices(@RequestPayload GetAllInvoicesRequest request) throws ServiceOperationException {
        try {
            log.debug("Getting all invoices");
            Collection<Invoice> invoices = invoiceService.getAllInvoices();
            return createSuccessInvoicesResponse(InvoiceMapper.mapInvoices(invoices));
        } catch (Exception e) {
            String message = "An error occured during getting all invoices";
            log.error(message);
            return createErrorInvoicesResponse(message);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addInvoiceRequest")
    @ResponsePayload
    public InvoiceResponse addInvoice(@RequestPayload AddInvoiceRequest request) throws ServiceOperationException {
        try {
            log.debug("Adding invoice {} ", request.getInvoice());
            Invoice invoice = InvoiceMapper.mapInvoice(request.getInvoice());
            if (invoice.getId() != null && invoiceService.invoiceExists(invoice.getId())) {
                return createErrorInvoiceResponse("Invoice already exists");
            }
            Invoice addedInvoice = invoiceService.addInvoice(invoice);
            return createSuccessInvoiceResponse(InvoiceMapper.mapInvoice(addedInvoice));
        } catch (ServiceOperationException | DatatypeConfigurationException e) {
            String message = "An error occured during adding invoice";
            log.error(message);
            return createErrorInvoiceResponse(message);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceByIdRequest")
    @ResponsePayload
    public InvoiceResponse getInvoiceById(@RequestPayload GetInvoiceByIdRequest request) {
        try {
            log.debug("Getting invoice by id: {}", request.getId());
            Optional<Invoice> invoice = invoiceService.getInvoiceById(request.getId());
            if (invoice.isPresent()) {
                return createSuccessInvoiceResponse(InvoiceMapper.mapInvoice(invoice.get()));
            }
            String message = String.format("Invoice with the following id: %d does not exist and cannot be retrieved", request.getId());
            log.error(message);
            return createErrorInvoiceResponse(message);
        } catch (ServiceOperationException | DatatypeConfigurationException e) {
            String message = "Invoice could not be retrieved from database";
            log.error(message);
            return createErrorInvoiceResponse(message);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceByNumberRequest")
    @ResponsePayload
    public InvoiceResponse getInvoiceByNumber(@RequestPayload GetInvoiceByNumberRequest request) {
        try {
            log.debug("Getting invoice by number: {}", request.getNumber());
            Optional<Invoice> invoice = invoiceService.getInvoiceByNumber(request.getNumber());
            if (invoice.isPresent()) {
                return createSuccessInvoiceResponse(InvoiceMapper.mapInvoice(invoice.get()));
            }
            String message = String.format("Invoice with the following number: %s does not exist", request.getNumber());
            log.error(message);
            return createErrorInvoiceResponse(message);
        } catch (ServiceOperationException | DatatypeConfigurationException e) {
            String message = "Invoice could not be retrieved";
            log.error(message);
            return createErrorInvoiceResponse(message);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateInvoiceRequest")
    @ResponsePayload
    public InvoiceResponse updateInvoice(@RequestPayload UpdateInvoiceRequest request) {
        try {
            log.debug("Updating invoice {}", request.getInvoice());
            Invoice invoice = InvoiceMapper.mapInvoice(request.getInvoice());

            if (!(request.getId() == invoice.getId())) {
                String message = String.format("Invoice to update has different id than %d", request.getId());
                log.error(message);
                return createErrorInvoiceResponse(message);
            }
            if (!invoiceService.invoiceExists(invoice.getId())) {
                String message = String.format("Invoice with the following id: %d does not exist and cannot be updated", invoice.getId());
                log.error(message);
                return createErrorInvoiceResponse(message);
            }
            Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
            return createSuccessInvoiceResponse(InvoiceMapper.mapInvoice(updatedInvoice));
        } catch (ServiceOperationException | DatatypeConfigurationException e) {
            String message = "An error occured during updating invoice";
            log.error(message);
            return createErrorInvoiceResponse(message);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteInvoiceByIdRequest")
    @ResponsePayload
    public Response deleteInvoiceById(@RequestPayload DeleteInvoiceByIdRequest request) {
        try {
            log.debug("Deleting invoice by id: {}", request.getId());
            if (!invoiceService.invoiceExists(request.getId())) {
                String message = String.format("Invoice with the following id: %d does not exist and cannot be deleted", request.getId());
                log.error(message);
                return createErrorResponse(message);
            }
            invoiceService.deleteInvoiceById(request.getId());
            return createSuccessResponse();
        } catch (Exception e) {
            String message = "An error occured during deleting invoice";
            log.error(message);
            return createErrorResponse(message);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAllInvoicesRequest")
    @ResponsePayload
    public Response deleteAllInvoices(@RequestPayload DeleteAllInvoicesRequest request) {
        try {
            log.debug("Deleting all invoices from database");
            invoiceService.deleteAllInvoices();
            return createSuccessResponse();
        } catch (Exception e) {
            String message = "An error occured during deleting all invoices";
            log.error(message);
            return createErrorResponse(message);
        }
    }

    private InvoiceResponse createSuccessInvoiceResponse(InvoiceSoap invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setInvoice(invoice);
        response.setMessage("");
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    private InvoicesResponse createSuccessInvoicesResponse(Collection<InvoiceSoap> invoices) {
        InvoicesResponse response = new InvoicesResponse();
        invoices.stream().forEach(invoice -> response.getInvoices().add(invoice));
        response.setMessage("");
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    private Response createSuccessResponse() {
        Response response = new Response();
        response.setMessage("");
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    private InvoiceResponse createErrorInvoiceResponse(String message) {
        InvoiceResponse response = new InvoiceResponse();
        response.setMessage(message);
        response.setStatus(ResponseStatus.FAILURE);
        return response;
    }

    private InvoicesResponse createErrorInvoicesResponse(String message) {
        InvoicesResponse response = new InvoicesResponse();
        response.setMessage(message);
        response.setStatus(ResponseStatus.FAILURE);
        return response;
    }

    private Response createErrorResponse(String message) {
        Response response = new Response();
        response.setMessage(message);
        response.setStatus(ResponseStatus.FAILURE);
        return response;
    }
}
