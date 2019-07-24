package pl.coderstrust.soap;

import java.util.Collection;
import java.util.Optional;
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
            return createErrorResponseForInvoicesCollection("Invoices could not be retrieved: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addInvoiceRequest")
    @ResponsePayload
    public InvoiceResponse addInvoice(@RequestPayload AddInvoiceRequest request) throws ServiceOperationException {
        try {
            InvoiceSoap invoiceSoapToSave = request.getInvoice();
            Invoice invoiceToSave = ModelConverter.convertInvoiceSoapToInvoice(invoiceSoapToSave);
            Invoice invoice = invoiceService.addInvoice(invoiceToSave);
            InvoiceSoap invoiceSoapToDisplay = ModelConverter.convertInvoiceToInvoiceSoap(invoice);
            return createSuccessResponse(invoiceSoapToDisplay);
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be added to database: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceByIdRequest")
    @ResponsePayload
    public InvoiceResponse getInvoiceById(@RequestPayload GetInvoiceByIdRequest request) {
        try {
            Optional<Invoice> invoiceOptional = invoiceService.getInvoiceById(request.getId());
            if (invoiceOptional.isEmpty()) {
                throw new ServiceOperationException("Invoice cannot be null");
            } else {
                Invoice retrievedInvoice = invoiceOptional.get();
                return createSuccessResponse(ModelConverter.convertInvoiceToInvoiceSoap(retrievedInvoice));
            }
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be retrieved: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateInvoiceRequest")
    @ResponsePayload
    public InvoiceResponse updateInvoice(@RequestPayload UpdateInvoiceRequest request) {
        try {
            InvoiceSoap invoiceSoapToUpdate = request.getInvoice();
            Invoice invoiceToUpdate = ModelConverter.convertInvoiceSoapToInvoice(invoiceSoapToUpdate);
            Invoice invoiceToDisplay = invoiceService.updateInvoice(invoiceToUpdate);
            return createSuccessResponse(ModelConverter.convertInvoiceToInvoiceSoap(invoiceToDisplay));
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be updated: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteInvoiceByIdRequest")
    @ResponsePayload
    public InvoiceResponse deleteInvoiceById(@RequestPayload DeleteInvoiceByIdRequest request) {
        try {
            Optional<Invoice> invoiceOptional = invoiceService.getInvoiceById(request.getId());
            if (invoiceOptional.isEmpty()) {
                throw new ServiceOperationException("Invoice cannot be null");
            } else {
                invoiceService.deleteInvoiceById(request.getId());
                return createSuccessResponse(ModelConverter.convertInvoiceToInvoiceSoap(invoiceOptional.get()));
            }
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be deleted: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAllInvoicesRequest")
    @ResponsePayload
    public InvoicesResponse deleteAllInvoices(@RequestPayload DeleteAllInvoicesRequest request) {
        try {
            Collection<Invoice> allInvoices = invoiceService.getAllInvoices();
            Collection<InvoiceSoap> allInvoicesSoap = ModelConverter.convertInvoiceCollectionToInvoicesSoap(allInvoices);
            ;
            invoiceService.deleteAllInvoices();
            return createSuccessResponse(allInvoicesSoap);
        } catch (Exception e) {
            return createErrorResponseForInvoicesCollection("Invoices could not be deleted: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceByNumberRequest")
    @ResponsePayload
    public InvoiceResponse getInvoiceByNumber(@RequestPayload GetInvoiceByNumberRequest request) {
        try {
            Optional<Invoice> invoiceOptional = invoiceService.getInvoiceByNumber(request.getNumber());
            if (invoiceOptional.isEmpty()) {
                throw new ServiceOperationException("Invoice cannot be null");
            } else {
                return createSuccessResponse(ModelConverter.convertInvoiceToInvoiceSoap(invoiceOptional.get()));
            }
        } catch (Exception e) {
            return createErrorResponseForSingleInvoice("Invoice could not be retrieved");
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "invoiceExistsRequest")
    @ResponsePayload
    public InvoiceExistsResponse invoiceExists(@RequestPayload InvoiceExistsRequest request) {
        InvoiceExistsResponse response = new InvoiceExistsResponse();
        try {
            response.setExists(invoiceService.invoiceExists(request.getId()));
            response.setMessage("");
            response.setStatus(Status.SUCCESS);
            return response;
        } catch (Exception e) {
            response.setStatus(Status.FAILED);
            response.setMessage("Invoice does not exist: " + e);
            return response;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "invoicesCountRequest")
    @ResponsePayload
    public InvoicesCountResponse invoicesCount(@RequestPayload InvoicesCountRequest request) {
        InvoicesCountResponse response = new InvoicesCountResponse();
        try {
            response.setCount(invoiceService.invoicesCount());
            response.setMessage("");
            response.setStatus(Status.SUCCESS);
            return response;
        } catch (Exception e) {
            response.setStatus(Status.FAILED);
            response.setMessage("Could not retrieve number of invoices: " + e);
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
        InvoiceResponse response = new InvoiceResponse();
        response.setInvoice(null);
        response.setMessage(message);
        response.setStatus(Status.FAILED);
        return response;
    }

    private InvoicesResponse createSuccessResponse(Collection<InvoiceSoap> invoices) {
        InvoicesResponse response = new InvoicesResponse();
        response.getInvoices().addAll(invoices);
        response.setMessage("");
        response.setStatus(Status.SUCCESS);
        return response;
    }

    private InvoicesResponse createErrorResponseForInvoicesCollection(String message) {
        InvoicesResponse response = new InvoicesResponse();
        response.getInvoices().addAll(null);
        response.setMessage(message);
        response.setStatus(Status.FAILED);
        return response;
    }
}
