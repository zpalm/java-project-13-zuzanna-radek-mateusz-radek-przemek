package pl.coderstrust.soap.mappers;

import pl.coderstrust.model.Vat;
import pl.coderstrust.soap.bindingclasses.VatSoap;

public class VatMapper {

    public static Vat mapVat(VatSoap vatSoap) {
        switch (vatSoap) {
            case VAT_0:
                return Vat.VAT_0;
            case VAT_5:
                return Vat.VAT_5;
            case VAT_8:
                return Vat.VAT_8;
            default:
                return Vat.VAT_23;
        }
    }

    public static VatSoap mapVat(Vat vat) {
        switch (vat) {
            case VAT_0:
                return VatSoap.VAT_0;
            case VAT_5:
                return VatSoap.VAT_5;
            case VAT_8:
                return VatSoap.VAT_8;
            default:
                return VatSoap.VAT_23;
        }
    }
}
