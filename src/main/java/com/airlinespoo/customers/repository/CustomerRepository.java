package com.airlinespoo.customers.repository;

import com.airlinespoo.core.repository.Repository;
import com.airlinespoo.customers.model.Country;
import com.airlinespoo.customers.model.Customer;
import com.airlinespoo.customers.model.DocumentType;

import java.util.List;
import java.util.Optional;

/**
 * Contrato (Interfaz) del repositorio de clientes que es el kit con el CRUD y las busquedas que necesita hacer service
 */

public interface CustomerRepository extends Repository<Customer, String> {

    Optional<Customer> findByEmail(String email); //Select Where Email

    List<Customer> findByLastNameContaining(String text); //Select Where Lastname

    List<Customer> findByCountry(Country country); //Select Where Country ID

    List<Customer> findByDocumentType(DocumentType documentType);

    boolean existsByCountry(Country country); //Sirve para no eliminar países en uso

    boolean existsByDocumentType(DocumentType documentType); //Para no eliminar tipos de documento en uso
}
