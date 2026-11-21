package com.airlinespoo.customers.model;


import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.model.Identifiable;
import com.airlinespoo.core.util.Validator;

public class DocumentType implements Identifiable<String> {

    private static final int CODE_LENGTH = 2;
    private static final int MAX_DESCRIPTION_LENGTH = 20;
    private static final String CODE_FIELD = "código del tipo de documento";
    //CODE_FIELD es la variable donde guardamos el texto que nos ayuda a identificar que campo esta causando el error,
    //en este caso el error estaría en el código del documenttype

    private final String id; //private no lo deja modificar desde otras clases y el final indica que es para asignarse
    //UNA sola vez
    private String description;

    //Constructor para recibir los datos, validar que esten correctos y guardar la información
    public DocumentType(
        String id,
        String description) {
        //Creamos la variable Code, y llamamos a un metodo que recibe el id y lo normaliza para escribir el codigo
        //en mayuscula (cc -> CC)
        String code = normalizeCode(id);
        if (code.length() != CODE_LENGTH) {
            throw new ValidationException("El campo " + CODE_FIELD + " debe tener " + CODE_LENGTH + " caracteres");
        }
        this.id = code; //Guarda el valor que recibimos
        setDescription(description);
    }

    //Metodo que utilizara el constructor y el servicio para normalizar el id del tipo de documento, poner mayus y quitar espacio
    public static String normalizeCode(String code) {
        return Validator.requireText(code, CODE_FIELD).replace(" ","").toUpperCase();
    }
    @Override
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription (String description){
        this.description = Validator.requireText(description, "En la descripción del Document Type",
            MAX_DESCRIPTION_LENGTH);
    }

    @Override
    public boolean equals (Object other){
        if (this == other) {
            return true;
        }
        if (!(other instanceof DocumentType)) {
            return false;
        }
        DocumentType that = (DocumentType) other;
        return id.equals(that.id);
    }

    @Override
    public int hashCode () {
        return id.hashCode();
    }

    @Override
    public String toString () {
        return "DocumentType {id =´ " + id + "´ , description= " + description + "´}";
    }
}

