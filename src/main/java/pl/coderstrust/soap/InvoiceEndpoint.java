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
        InvoicesResponse response = new InvoicesResponse();
        try {
            Collection<Invoice> allInvoices = invoiceService.getAllInvoices();
            Collection<InvoiceSoap> allInvoicesSoap = ModelConverter.convertInvoiceCollectionToInvoicesSoap(allInvoices);
            response.getInvoices().addAll(allInvoicesSoap);
            return createSuccessResponse(response);
        } catch (Exception e) {
            return createErrorResponse(response, "Invoices could not be retrieved: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addInvoiceRequest")
    @ResponsePayload
    public InvoiceResponse addInvoice(@RequestPayload AddInvoiceRequest request) throws ServiceOperationException {
        InvoiceResponse response = new InvoiceResponse();
        try {
            InvoiceSoap invoiceSoapToSave = request.getInvoice();
            Invoice invoiceToSave = ModelConverter.convertInvoiceSoapToInvoice(invoiceSoapToSave);
            Invoice invoice = invoiceService.addInvoice(invoiceToSave);
            InvoiceSoap invoiceSoapToDisplay = ModelConverter.convertInvoiceToInvoiceSoap(invoice);
            response.setInvoice(invoiceSoapToDisplay);
            return createSuccessResponse(response);
        } catch (Exception e) {
            return createErrorResponse(response, "Invoice could not be added to database: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceByIdRequest")
    @ResponsePayload
    public InvoiceResponse getInvoiceById(@RequestPayload GetInvoiceByIdRequest request) {
        InvoiceResponse response = new InvoiceResponse();
        try {
            Optional<Invoice> invoiceOptional = invoiceService.getInvoiceById(request.getId());
            if (invoiceOptional.isEmpty()) {
                throw new ServiceOperationException("Invoice cannot be null");
            } else {
                Invoice retrievedInvoice = invoiceOptional.get();
                response.setInvoice(ModelConverter.convertInvoiceToInvoiceSoap(retrievedInvoice));
                return createSuccessResponse(response);
            }
        } catch (Exception e) {
            return createErrorResponse(response, "Invoice could not be retrieved: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateInvoiceRequest")
    @ResponsePayload
    public InvoiceResponse updateInvoice(@RequestPayload UpdateInvoiceRequest request) {
        InvoiceResponse response = new InvoiceResponse();
        try {
            InvoiceSoap invoiceSoapToUpdate = request.getInvoice();
            Invoice invoiceToUpdate = ModelConverter.convertInvoiceSoapToInvoice(invoiceSoapToUpdate);
            Invoice invoiceToDisplay = invoiceService.updateInvoice(invoiceToUpdate);
            response.setInvoice(ModelConverter.convertInvoiceToInvoiceSoap(invoiceToDisplay));
            return createSuccessResponse(response);
        } catch (Exception e) {
            return createErrorResponse(response, "Invoice could not be updated: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteInvoiceByIdRequest")
    @ResponsePayload
    public InvoiceResponse deleteInvoiceById(@RequestPayload DeleteInvoiceByIdRequest request) {
        InvoiceResponse response = new InvoiceResponse();
        try {
            Optional<Invoice> invoiceOptional = invoiceService.getInvoiceById(request.getId());
            if (invoiceOptional.isEmpty()) {
                throw new ServiceOperationException("Invoice cannot be null");
            } else {
                response.setInvoice(ModelConverter.convertInvoiceToInvoiceSoap(invoiceOptional.get()));
                invoiceService.deleteInvoiceById(request.getId());
                return createSuccessResponse(response);
            }
        } catch (Exception e) {
            return createErrorResponse(response, "Invoice could not be deleted: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAllInvoicesRequest")
    @ResponsePayload
    public InvoicesResponse deleteAllInvoices(@RequestPayload DeleteAllInvoicesRequest request) {
        InvoicesResponse response = new InvoicesResponse();
        try {
            Collection<Invoice> allInvoices = invoiceService.getAllInvoices();
            Collection<InvoiceSoap> allInvoicesSoap = ModelConverter.convertInvoiceCollectionToInvoicesSoap(allInvoices);
            response.getInvoices().addAll(allInvoicesSoap);
            invoiceService.deleteAllInvoices();
            return createSuccessResponse(response);
        } catch (Exception e) {
            return createErrorResponse(response, "Invoices could not be deleted: " + e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceByNumberRequest")
    @ResponsePayload
    public InvoiceResponse getInvoiceByNumber(@RequestPayload GetInvoiceByNumberRequest request) {
        InvoiceResponse response = new InvoiceResponse();
        try {
            Optional<Invoice> invoiceOptional = invoiceService.getInvoiceByNumber(request.getNumber());
            if (invoiceOptional.isEmpty()) {
                throw new ServiceOperationException("Invoice cannot be null");
            } else {
                response.setInvoice(ModelConverter.convertInvoiceToInvoiceSoap(invoiceOptional.get()));
                return createSuccessResponse(response);
            }
        } catch (Exception e) {
            return createErrorResponse(response, "Invoice could not be retrieved");
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

    private InvoicesResponse createErrorResponse(InvoicesResponse response, String message) {
        response.setMessage(message);
        response.setStatus(Status.FAILED);
        return response;
    }

    private InvoiceResponse createErrorResponse(InvoiceResponse response, String message) {
        response.setMessage(message);
        response.setStatus(Status.FAILED);
        return response;
    }

    private InvoicesResponse createSuccessResponse(InvoicesResponse response) {
        response.setStatus(Status.SUCCESS);
        response.setMessage("");
        return response;
    }

    private InvoiceResponse createSuccessResponse(InvoiceResponse response) {
        response.setStatus(Status.SUCCESS);
        response.setMessage("");
        return response;
    }
}
