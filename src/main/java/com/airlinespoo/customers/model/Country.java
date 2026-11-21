package com.airlinespoo.customers.model;

import com.airlinespoo.core.model.Identifiable;
import com.airlinespoo.core.util.Validator;

/**
 * Clase que representa un país. Tiene un id que no cambia y un nombre
 */
public class Country implements Identifiable<Integer>{ //Se implementa la interfaz

    private final int id; //Atributos
    private String name;
    private static final int MAX_LENGTH_NAME =20;
    //Constante donde se define el maximo de letras del nombre del país
    public static final int MAX_ID = 195;
    //Constante con el total de países del mundo, es el id más grande que se permite.
    //Es pública porque el menú también la usa para pedir el id

    /**
     * Crea un país con su id y su nombre
     * @param id id del país, entre 1 y MAX_ID
     * @param name nombre, obligatorio
     */
    public Country(
        int id, //Se declara el constructor
        String name
    ) {
        Validator.requireInRange(id,1, MAX_ID, "El id del país");
        this.id = id; //Se pone los parametros del constructor
        setName(name);
    }

    /**
     * Devuelve el id del país
     */
    @Override //Se llama la interfaz implementada para mostrar id del país
    public Integer getId() {
        return id;
    }
    /**
     * Devuelve el nombre del país
     */
    public String getName() {
        return name;
    }
    /**
     * Cambia el nombre del país, valida que no esté vacío y que no pase de 20 letras
     */
    public void setName(String name) {
        this.name = Validator.requireText(name, "nombre del país", MAX_LENGTH_NAME);
        //Validacion de que el nombre cumpla con el requisito de las 20 letras
    }

    /**
     * Se verifica si dos países tienen el mismo id, serían el mismo país (equals)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        //Se verifica que el valor sea igual al parametro, como que colombia sea String
        if (!(other instanceof Country)) {
            return false;
        }
        Country country = (Country) other; //Se convierte el valor de "otro" al tipo
        return id == country.id;
    }

    /**
     * Hash nos ayuda a buscar entre todos, para equals verificar
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * Muestra el id y el nombre del país
     */
    @Override
    public String toString() {
        return "Country{id=" + id + ", name='" + name + "'}";
    }
}

