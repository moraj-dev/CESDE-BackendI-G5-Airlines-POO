package com.airlinespoo.customers.model;

import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.model.Identifiable;
import com.airlinespoo.core.util.Validator;

public class Customer implements Identifiable<String> {

    private static final int MAX_ID_LENGTH = 20;
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_TELEPHONE_LENGTH = 15;
    private static final int MAX_EMAIL_LENGTH = 80;
    private static final int MAX_ADDRESS_LENGTH = 40;
    private static final String TELEPHONE_PATTERN = "\\+?[0-9]+"; //el + al inicio es opcional (+57) solo se permite digitos
    private static final String EMAIL_PATTERN = "[^@\\s]+@[^@\\s]+\\.[^@\\s]+";
    private static final String TELEPHONE_FIELD = "teléfono del cliente";
    private static final String EMAIL_FIELD = "correo del cliente";

    private final String id;
    private String firstName;
    private String lastName;
    private DocumentType documentType;
    private String telephoneNumber;
    private String email;
    private Country country;
    private String address;

    //Creacion del cliente con datos OBLIGATORIOS, la dirección y el correo se asignan luego con SETTERS
    public Customer(String id,
                    String firstName,
                    String lastName,
                    DocumentType documentType,
                    String telephoneNumber,
                    Country country) {
        this.id = Validator.requireText(id, "Número de documento del cliente", MAX_ID_LENGTH);
        setFirstName(firstName);
        setLastName(lastName);
        setDocumentType(documentType);
        setTelephoneNumber(telephoneNumber);
        setCountry(country);
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getFirstName() {
        return firstName;
    }

    //Validación del nombre para el maximo de caracteres y no puede ser null
    public void setFirstName(String firstName) {
        this.firstName = Validator.requireText(firstName, "Nombre del cliente", MAX_NAME_LENGTH);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = Validator.requireText(lastName, "Apellido del cliente", MAX_NAME_LENGTH);
    }

    //RETURN Nombres y apellidos separados por un espacio
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    //No permite null en el documenttype
    public void setDocumentType(DocumentType documentType) {
        this.documentType = Validator.requireNotNull(documentType, "Tipo de documento del cliente");
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = validateTelephone(telephoneNumber);
    }

    //RETURN el correo o null si no tiene
    public String getEmail() {
        return email;
    }

    //Verificar si tiene correo, si cumple con el formato y los caracteres
    public void setEmail(String email) {
        this.email = validateEmail(email);
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = Validator.requireNotNull(country, "País del cliente");
    }

    //RETURN la direccion o null si no hay
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = validateAddress(address);
    }

    /*Cambiar los tres datos del cliente a la vez (Telefono, correo y direccion), primero los valida y luego los asigna,
    si uno es invalido, no se modifican y el cliente queda como estaba */

    public void updateContact(String telephoneNumber, String email, String address) {
        String newTelephone = validateTelephone(telephoneNumber);
        String newEmail = validateEmail(email);
        String newAddress = validateAddress(address);
        this.telephoneNumber = newTelephone;
        this.email = newEmail;
        this.address = newAddress;
    }

    //Validaciones
    private static String validateTelephone(String telephoneNumber) {
        String text = Validator.requireText(telephoneNumber, TELEPHONE_FIELD, MAX_TELEPHONE_LENGTH);
        if (!text.matches(TELEPHONE_PATTERN)) { //Si el numero no cumple con el formato ejecuta IF
            //text.matches compara el texto ingresado con la expresion
            throw new ValidationException("El campo ´" + TELEPHONE_FIELD + " no es valido, aolo se admite dígitos " +
                "con la opción del + al inicio");
        }
        return text;
    }

    private static String validateEmail(String email) {
        String text = optionalText(email, EMAIL_FIELD, MAX_EMAIL_LENGTH);
        if (text != null && !text.matches(EMAIL_PATTERN)) {
            throw new ValidationException("El campo ´" + EMAIL_FIELD + "´no tiene un fomato válido");
        }
        return text;
    }

    private static String validateAddress(String address) {
        return optionalText(address, "la dirección del cliente", MAX_ADDRESS_LENGTH);
    }

    //VALIDACION: si esta vacio se vuelve null y si hay texto se valida la longitud
    private static String optionalText(String value, String field, int maxLength) {
        String text = Validator.normalize(value);
        if (text == null || text.isEmpty()) {
            return null;
        }
        return Validator.requireMaxLength(text, maxLength, field);
    }

    //Dos clientes son el mismo si tiene el mimo documento
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Customer)) {
            return false;
        }
        Customer that = (Customer) other;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    //Muestra los datos, excepto dirección, correo y telefono
    @Override
    public String toString() {
        return "Customer{id=` " + id + " `, fullName=` " + getFullName() + "´, documentType=´" + documentType.getId() +
            "`,country=`" + country.getName() + "`} ";
    }
}



