//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.08.31 at 02:06:52 PM BST
//


package pl.coderstrust.soap.bindingclasses;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 * <p>Java class for entriesList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entriesList"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="entry" type="{http://project-13-zuzanna-radek-mateusz-radek-przemek}invoiceEntrySoap" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entriesList", propOrder = {
    "entry"
})
public class EntriesList
    implements Cloneable, CopyTo, Equals, HashCode, ToString
{

    protected List<InvoiceEntrySoap> entry;

    /**
     * Gets the value of the entry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InvoiceEntrySoap }
     * 
     * 
     */
    public List<InvoiceEntrySoap> getEntry() {
        if (entry == null) {
            entry = new ArrayList<InvoiceEntrySoap>();
        }
        return this.entry;
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
            List<InvoiceEntrySoap> theEntry;
            theEntry = (((this.entry!= null)&&(!this.entry.isEmpty()))?this.getEntry():null);
            strategy.appendField(locator, this, "entry", buffer, theEntry);
        }
        return buffer;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof EntriesList)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final EntriesList that = ((EntriesList) object);
        {
            List<InvoiceEntrySoap> lhsEntry;
            lhsEntry = (((this.entry!= null)&&(!this.entry.isEmpty()))?this.getEntry():null);
            List<InvoiceEntrySoap> rhsEntry;
            rhsEntry = (((that.entry!= null)&&(!that.entry.isEmpty()))?that.getEntry():null);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "entry", lhsEntry), LocatorUtils.property(thatLocator, "entry", rhsEntry), lhsEntry, rhsEntry)) {
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
            List<InvoiceEntrySoap> theEntry;
            theEntry = (((this.entry!= null)&&(!this.entry.isEmpty()))?this.getEntry():null);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "entry", theEntry), currentHashCode, theEntry);
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
        if (draftCopy instanceof EntriesList) {
            final EntriesList copy = ((EntriesList) draftCopy);
            if ((this.entry!= null)&&(!this.entry.isEmpty())) {
                List<InvoiceEntrySoap> sourceEntry;
                sourceEntry = (((this.entry!= null)&&(!this.entry.isEmpty()))?this.getEntry():null);
                @SuppressWarnings("unchecked")
                List<InvoiceEntrySoap> copyEntry = ((List<InvoiceEntrySoap> ) strategy.copy(LocatorUtils.property(locator, "entry", sourceEntry), sourceEntry));
                copy.entry = null;
                if (copyEntry!= null) {
                    List<InvoiceEntrySoap> uniqueEntryl = copy.getEntry();
                    uniqueEntryl.addAll(copyEntry);
                }
            } else {
                copy.entry = null;
            }
        }
        return draftCopy;
    }

    public Object createNewInstance() {
        return new EntriesList();
    }

}
