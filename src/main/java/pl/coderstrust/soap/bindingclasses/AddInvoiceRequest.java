//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.08.31 at 02:06:52 PM BST 
//


package pl.coderstrust.soap.bindingclasses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.CopyStrategy;
import org.jvnet.jaxb2_commons.lang.CopyTo;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBCopyStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="invoice" type="{http://project-13-zuzanna-radek-mateusz-radek-przemek}invoiceSoap"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "invoice"
})
@XmlRootElement(name = "addInvoiceRequest")
public class AddInvoiceRequest
    implements Cloneable, CopyTo, Equals, HashCode, ToString
{

    @XmlElement(required = true)
    protected InvoiceSoap invoice;

    /**
     * Gets the value of the invoice property.
     * 
     * @return
     *     possible object is
     *     {@link InvoiceSoap }
     *     
     */
    public InvoiceSoap getInvoice() {
        return invoice;
    }

    /**
     * Sets the value of the invoice property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvoiceSoap }
     *     
     */
    public void setInvoice(InvoiceSoap value) {
        this.invoice = value;
    }

    public String toString() {
        final ToStringStrategy strategy = JAXBToStringStrategy.INSTANCE;
        final StringBuilder buffer = new StringBuilder();
        append(null, buffer, strategy);
        return buffer.toString();
    }

    public StringBuilder append(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        strategy.appendStart(locator, this, buffer);
        appendFields(locator, buffer, strategy);
        strategy.appendEnd(locator, this, buffer);
        return buffer;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        {
            InvoiceSoap theInvoice;
            theInvoice = this.getInvoice();
            strategy.appendField(locator, this, "invoice", buffer, theInvoice);
        }
        return buffer;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof AddInvoiceRequest)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final AddInvoiceRequest that = ((AddInvoiceRequest) object);
        {
            InvoiceSoap lhsInvoice;
            lhsInvoice = this.getInvoice();
            InvoiceSoap rhsInvoice;
            rhsInvoice = that.getInvoice();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "invoice", lhsInvoice), LocatorUtils.property(thatLocator, "invoice", rhsInvoice), lhsInvoice, rhsInvoice)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = 1;
        {
            InvoiceSoap theInvoice;
            theInvoice = this.getInvoice();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "invoice", theInvoice), currentHashCode, theInvoice);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

    public Object clone() {
        return copyTo(createNewInstance());
    }

    public Object copyTo(Object target) {
        final CopyStrategy strategy = JAXBCopyStrategy.INSTANCE;
        return copyTo(null, target, strategy);
    }

    public Object copyTo(ObjectLocator locator, Object target, CopyStrategy strategy) {
        final Object draftCopy = ((target == null)?createNewInstance():target);
        if (draftCopy instanceof AddInvoiceRequest) {
            final AddInvoiceRequest copy = ((AddInvoiceRequest) draftCopy);
            if (this.invoice!= null) {
                InvoiceSoap sourceInvoice;
                sourceInvoice = this.getInvoice();
                InvoiceSoap copyInvoice = ((InvoiceSoap) strategy.copy(LocatorUtils.property(locator, "invoice", sourceInvoice), sourceInvoice));
                copy.setInvoice(copyInvoice);
            } else {
                copy.invoice = null;
            }
        }
        return draftCopy;
    }

    public Object createNewInstance() {
        return new AddInvoiceRequest();
    }

}
