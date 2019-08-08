package pl.coderstrust.controller;

import java.net.URI;
import java.util.Optional;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.coderstrust.controller.exceptions.InvoiceIdNotFoundException;
import pl.coderstrust.controller.exceptions.InvoiceNumberNotFoundException;
import pl.coderstrust.controller.exceptions.NullInvoiceNumberException;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoiceEmailService;
import pl.coderstrust.service.InvoicePdfService;
import pl.coderstrust.service.InvoiceService;
import pl.coderstrust.service.ServiceOperationException;

@RestController
@RequestMapping("/invoices")
@Api(value = "/invoices")
public class InvoiceController {

    private Logger log = LoggerFactory.getLogger(InvoiceController.class);

    private final InvoiceService invoiceService;
    private final InvoiceEmailService invoiceEmailService;
    private final InvoicePdfService invoicePdfService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, InvoiceEmailService invoiceEmailService, InvoicePdfService invoicePdfService) {
        this.invoiceService = invoiceService;
        this.invoiceEmailService = invoiceEmailService;
        this.invoicePdfService = invoicePdfService;
    }

    @GetMapping(produces = "application/json")
    @ApiOperation(value = "Get all invoices", notes = "Retrieving the collection of all invoices in database", response = Invoice[].class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = Invoice[].class),
        @ApiResponse(code = 406, message = "Not acceptable format"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<?> getAll() {
        try {
            return new ResponseEntity<>(invoiceService.getAllInvoices(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred during getting all invoices.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during getting all invoices.");
        }
    }

    @GetMapping(value = "/{id}", produces = {"application/json", "application/pdf"})
    @ApiOperation(value = "Get invoice by id", notes = "Retrieving the invoice by provided id in json or pdf format", produces = "application/json, application/pdf", response = Invoice.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = Invoice.class),
        @ApiResponse(code = 404, message = "Invoice not found"),
        @ApiResponse(code = 406, message = "Not acceptable format"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "id", value = "Id of the invoice to get", dataType = "Long")
    public ResponseEntity<?> getById(@PathVariable("id") Long id, @RequestHeader HttpHeaders httpHeaders) throws ServiceOperationException, InvoiceIdNotFoundException {
        Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
        if (invoice.isEmpty()) {
            log.error("Attempt to get invoice by id that does not exist in database.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt to get invoice by id that does not exist in database.");
        }
        if (isPdfResponse(httpHeaders)) {
            byte[] invoiceAsPdf = invoicePdfService.createPdf(invoice.get());
            return createPdfResponse(invoiceAsPdf);
        }
        return new ResponseEntity<>(invoice.get(), HttpStatus.OK);
    }

    @GetMapping(value = "/byNumber", produces = {"application/json", "application/pdf"})
    @ApiOperation(value = "Get invoice by number", notes = "Retrieving the invoice by provided number in json or pdf format", produces = "application/json, application/pdf", response = Invoice.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = Invoice.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 404, message = "Invoice not found"),
        @ApiResponse(code = 406, message = "Not acceptable format"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "number", value = "Number of the invoice to get", dataType = "String")
    public ResponseEntity<?> getByNumber(@RequestParam String number, @RequestHeader HttpHeaders httpHeaders) throws ServiceOperationException, InvoiceNumberNotFoundException, NullInvoiceNumberException {
        if (number == null) {
            log.error("Attempt to get invoice providing null number.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to get invoice providing null number.");
        }
        Optional<Invoice> invoice = invoiceService.getInvoiceByNumber(number);
        if (invoice.isEmpty()) {
            log.debug("Attempt to get invoice by number that does not exist in database.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt to get invoice by number that does not exist in database.");
        }
        if (isPdfResponse(httpHeaders)) {
            byte[] invoiceAsPdf = invoicePdfService.createPdf(invoice.get());
            return createPdfResponse(invoiceAsPdf);
        }
        return new ResponseEntity<>(invoice.get(), HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ApiOperation(value = "Add new invoice", notes = "Add new invoice to database", response = Invoice.class)
    @ApiResponses({
        @ApiResponse(code = 201, message = "Created", response = Invoice.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 406, message = "Not acceptable format"),
        @ApiResponse(code = 409, message = "Invoice exists"),
        @ApiResponse(code = 415, message = "Wrong invoice format"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "invoice", value = "New invoice data", dataType = "Invoice")
    public ResponseEntity<?> add(@RequestBody Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            log.error("Attempt to add null invoice.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to add null invoice.");
        }

        if (invoice.getId() != null && invoiceService.invoiceExists(invoice.getId())) {
            log.error("Attempt to add invoice already existing in database.");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Attempt to add invoice already existing in database.");
        }
        Invoice addedInvoice = invoiceService.addInvoice(invoice);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(URI.create(String.format("/invoices/%d", addedInvoice.getId())));
        invoiceEmailService.sendMailWithInvoice(addedInvoice);
        log.debug(String.format("New invoice added with id %d.", addedInvoice.getId()));
        return new ResponseEntity<>(addedInvoice, responseHeaders, HttpStatus.CREATED);

        //log.error("An error occurred during adding invoice.", e);
        //throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during adding invoice.");

    }

    /*@GetMapping("/test")
    public void test(){
        throw new RuntimeException("test exception");
    }
*/
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ApiOperation(value = "Update invoice", notes = "Update invoice with provided id", response = Invoice.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = Invoice.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 404, message = "Invoice not found"),
        @ApiResponse(code = 406, message = "Not acceptable format"),
        @ApiResponse(code = 415, message = "Wrong invoice format"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParams({
        @ApiImplicitParam(required = true, name = "id", value = "Id of invoice to update", dataType = "Long"),
        @ApiImplicitParam(required = true, name = "invoice", value = "Invoice with updated data", dataType = "Invoice")
    })
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            log.error("Attempt to update invoice providing null invoice.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to update invoice providing null invoice.");
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!id.equals(invoice.getId())) {
            log.error("Attempt to update invoice providing different invoice id.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to update invoice providing different invoice id.");
        }
        if (!invoiceService.invoiceExists(id)) {
            log.error("Attempt to update not existing invoice.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt to update not existing invoice.");
        }
        log.debug(String.format("Updated invoice with id %d.", invoice.getId()));
        return new ResponseEntity<>(invoiceService.updateInvoice(invoice), HttpStatus.OK);

        //log.error("An error occurred during updating invoice.");
        //throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during updating invoice.");
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete invoice", notes = "Delete invoice with provided id")
    @ApiResponses({
        @ApiResponse(code = 204, message = "No content"),
        @ApiResponse(code = 404, message = "Invoice not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "id", value = "Id of invoice to delete", dataType = "Long")
    public ResponseEntity<?> remove(@PathVariable("id") Long id) {
        try {
            if (!invoiceService.invoiceExists(id)) {
                log.error("Attempt to delete not existing invoice.");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt to delete not existing invoice.");
                //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            invoiceService.deleteInvoiceById(id);
            log.debug(String.format("Deleted invoice with id %d.", id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("An error occurred during deleting invoice.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during deleting invoice.");
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<?> createPdfResponse(byte[] array) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(array, responseHeaders, HttpStatus.OK);
    }

    private boolean isPdfResponse(HttpHeaders httpHeaders) {
        Optional<MediaType> type = httpHeaders.getAccept().stream()
            .filter(x -> x.isCompatibleWith(MediaType.APPLICATION_PDF) || x.isCompatibleWith(MediaType.APPLICATION_JSON))
            .findFirst();
        if (type.isEmpty()) {
            return false;
        }
        return !type.get().isWildcardType() && type.get().isCompatibleWith(MediaType.APPLICATION_PDF);
    }
}
