package pl.coderstrust.soap;

import java.util.Collection;
import java.util.Optional;
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
import pl.coderstrust.soap.bindingclasses.InvoiceExistsRequest;
import pl.coderstrust.soap.bindingclasses.InvoiceExistsResponse;
import pl.coderstrust.soap.bindingclasses.InvoiceResponse;
import pl.coderstrust.soap.bindingclasses.InvoiceSoap;
import pl.coderstrust.soap.bindingclasses.InvoicesCountRequest;
import pl.coderstrust.soap.bindingclasses.InvoicesCountResponse;
import pl.coderstrust.soap.bindingclasses.InvoicesResponse;
import pl.coderstrust.soap.bindingclasses.Status;
import pl.coderstrust.soap.bindingclasses.UpdateInvoiceRequest;

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
            Collection<Invoice> allInvoices = invoiceService.getAllInvoices();
            Collection<InvoiceSoap> allInvoicesSoap = ModelConverter.convertInvoiceCollectionToInvoicesSoap(allInvoices);
            return createSuccessResponse(allInvoicesSoap);
        } catch (Exception e) {
            return createErrorResponseForInvoicesCollection("An error occured during getting all invoices", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addInvoiceRequest")
    @ResponsePayload
    public InvoiceResponse addInvoice(@RequestPayload AddInvoiceRequest request) throws ServiceOperationException {
        try {
            InvoiceSoap invoiceSoapToSave = request.getInvoice();
            if (invoiceSoapToSave.getId() != null && invoiceService.invoiceExists(invoiceSoapToSave.getId())) {
                return createErrorResponseForSingleInvoice(String.format("Invoice with the following id: %d does not exist and cannot be updated", invoiceSoapToSave.getId()));
            }
            Invoice invoiceToSave = ModelConverter.convertInvoiceSoapToInvoice(invoiceSoapToSave);
            Invoice invoice = invoiceService.addInvoice(invoiceToSave);
            InvoiceSoap invoiceSoapToDisplay = ModelConverter.convertInvoiceToInvoiceSoap(invoice);
            log.debug(String.format("New invoice with id %d was added to database", invoiceSoapToDisplay.getId()));
            return createSuccessResponse(invoiceSoapToDisplay);
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be added to database", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceByIdRequest")
    @ResponsePayload
    public InvoiceResponse getInvoiceById(@RequestPayload GetInvoiceByIdRequest request) {
        try {
            Optional<Invoice> invoiceOptional = invoiceService.getInvoiceById(request.getId());
            if (invoiceOptional.isEmpty()) {
                return createErrorResponseForSingleInvoice(String.format("Invoice with the following id: %d does not exist and cannot be retrieved", request.getId()));
            } else {
                Invoice retrievedInvoice = invoiceOptional.get();
                return createSuccessResponse(ModelConverter.convertInvoiceToInvoiceSoap(retrievedInvoice));
            }
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be retrieved from database",e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateInvoiceRequest")
    @ResponsePayload
    public InvoiceResponse updateInvoice(@RequestPayload UpdateInvoiceRequest request) {
        try {
            InvoiceSoap invoiceSoapToUpdate = request.getInvoice();
            if (!invoiceService.invoiceExists(invoiceSoapToUpdate.getId())) {
                return createErrorResponseForSingleInvoice(String.format("Invoice with the following id: %d does not exist and cannot be updated", invoiceSoapToUpdate.getId()));
            }
            Invoice invoiceToUpdate = ModelConverter.convertInvoiceSoapToInvoice(invoiceSoapToUpdate);
            Invoice invoiceToDisplay = invoiceService.updateInvoice(invoiceToUpdate);
            log.debug(String.format("Invoice with the following id: %d was successfully updated", invoiceToDisplay.getId()));
            return createSuccessResponse(ModelConverter.convertInvoiceToInvoiceSoap(invoiceToDisplay));
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be updated ", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteInvoiceByIdRequest")
    @ResponsePayload
    public InvoiceResponse deleteInvoiceById(@RequestPayload DeleteInvoiceByIdRequest request) {
        try {
            Optional<Invoice> invoiceOptional = invoiceService.getInvoiceById(request.getId());
            if (invoiceOptional.isEmpty()) {
                return createErrorResponseForSingleInvoice(String.format("Invoice with the following id: %d does not exist and cannot be deleted", request.getId()));
            } else {
                invoiceService.deleteInvoiceById(request.getId());
                log.debug(String.format("Invoice with the following id: %d was successfully deleted from database", request.getId()));
                return createSuccessResponse((InvoiceSoap) null);
            }
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be deleted: ", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAllInvoicesRequest")
    @ResponsePayload
    public InvoicesResponse deleteAllInvoices(@RequestPayload DeleteAllInvoicesRequest request) {
        try {
            Collection<Invoice> allInvoices = invoiceService.getAllInvoices();
            Collection<InvoiceSoap> allInvoicesSoap = ModelConverter.convertInvoiceCollectionToInvoicesSoap(allInvoices);
            invoiceService.deleteAllInvoices();
            log.debug("All invoices were successfully deleted from database");
            return createSuccessResponse((Collection<InvoiceSoap>) null);
        } catch (Exception e) {
            return createErrorResponseForInvoicesCollection("Invoices could not be deleted", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceByNumberRequest")
    @ResponsePayload
    public InvoiceResponse getInvoiceByNumber(@RequestPayload GetInvoiceByNumberRequest request) {
        try {
            Optional<Invoice> invoiceOptional = invoiceService.getInvoiceByNumber(request.getNumber());
            if (invoiceOptional.isEmpty()) {
                return createErrorResponseForSingleInvoice(String.format("Invoice with the following number: %s does not exist", request.getNumber()));
            } else {
                return createSuccessResponse(ModelConverter.convertInvoiceToInvoiceSoap(invoiceOptional.get()));
            }
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be retrieved", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "invoiceExistsRequest")
    @ResponsePayload
    public InvoiceExistsResponse invoiceExists(@RequestPayload InvoiceExistsRequest request) {
        InvoiceExistsResponse response = new InvoiceExistsResponse();
        try {
            log.debug("Checking if invoice exists");
            response.setExists(invoiceService.invoiceExists(request.getId()));
            response.setMessage("");
            response.setStatus(Status.SUCCESS);
            return response;
        } catch (Exception e) {
            response.setStatus(Status.FAILED);
            response.setMessage("Invoice does not exist");
            return response;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "invoicesCountRequest")
    @ResponsePayload
    public InvoicesCountResponse invoicesCount(@RequestPayload InvoicesCountRequest request) {
        InvoicesCountResponse response = new InvoicesCountResponse();
        try {
            log.debug("Retrieving number of invoices stored in database");
            response.setCount(invoiceService.invoicesCount());
            response.setMessage("");
            response.setStatus(Status.SUCCESS);
            return response;
        } catch (Exception e) {
            log.error("Could not retrieve number of  invoices", e);
            response.setStatus(Status.FAILED);
            response.setMessage("Could not retrieve number of invoices");
            return response;
        }
    }

    private InvoiceResponse createSuccessResponse(InvoiceSoap invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setInvoice(invoice);
        response.setMessage("");
        response.setStatus(Status.SUCCESS);
        return response;
    }

    private InvoiceResponse createErrorResponseForSingleInvoice(String message) {
        log.error(message);
        InvoiceResponse response = new InvoiceResponse();
        response.setMessage(message);
        response.setStatus(Status.FAILED);
        return response;
    }

    private InvoiceResponse createErrorResponseForSingleInvoice(String message, Exception e) {
        log.error(message, e);
        InvoiceResponse response = new InvoiceResponse();
        response.setMessage(message);
        response.setStatus(Status.FAILED);
        return response;
    }

    private InvoicesResponse createSuccessResponse(Collection<InvoiceSoap> invoices) {
        InvoicesResponse response = new InvoicesResponse();
        if (invoices != null) {
            response.getInvoices().addAll(invoices);
        }
        response.setMessage("");
        response.setStatus(Status.SUCCESS);
        return response;
    }

    private InvoicesResponse createErrorResponseForInvoicesCollection(String message, Exception e) {
        log.error(message, e);
        InvoicesResponse response = new InvoicesResponse();
        response.setMessage(message);
        response.setStatus(Status.FAILED);
        return response;
    }
}
