package com.airlinespoo.customers.repository;

import com.airlinespoo.core.repository.InMemoryRepository;
import com.airlinespoo.customers.model.Customer;
import com.airlinespoo.customers.model.Country;
import com.airlinespoo.customers.model.DocumentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de clientes en la memoria que hereda del Kit y el mapa encuentra un cliente sin reccorrer todos.
 * Solo busca y se reponde
 */
public class InMemoryCustomerRepository extends InMemoryRepository<Customer, String> implements CustomerRepository {

    @Override
    protected String entityName() {
        return "Cliente";
    }

    /**
     * Busca un cliente por correo, sin importar mayúsculas o minúsculas
     */
    @Override
    public Optional<Customer> findByEmail(String email){
        for (Customer customer : values()) {
            if (customer.getEmail() != null && customer.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    /**
     * Busca los clientes que tienen ese texto en el apellido
     */
    @Override
    public List<Customer> findByLastNameContaining(String text){
        String wanted = text.toLowerCase();
        List<Customer> found = new ArrayList<>();
        for (Customer customer : values()) {
            if (customer.getLastName().toLowerCase().contains(wanted)) {
                found.add(customer);
            }
        }
        return found;
    }

    /**
     * Lista los clientes de un país
     */
    @Override
    public List<Customer> findByCountry(Country country){
        List<Customer> found = new ArrayList<>();
        for (Customer customer : values()) {
            if (customer.getCountry().equals(country)) {
                found.add(customer);
            }
        }
        return found;
    }

    /**
     * Lista los clientes con un tipo de documento
     */
    @Override
    public List<Customer> findByDocumentType(DocumentType documentType) {
        List<Customer> found = new ArrayList<>();
        for (Customer customer : values()) {
            if (customer.getDocumentType().equals(documentType)) {
                found.add(customer);
            }
        }
        return found;
    }

    /**
     * Dice si algún cliente es de ese país
     */
    @Override
    public boolean existsByCountry(Country country){
        return !findByCountry(country).isEmpty();
    }

    /**
     * Dice si algún cliente usa ese tipo de documento
     */
    @Override
    public boolean existsByDocumentType(DocumentType documentType){
        return !findByDocumentType(documentType).isEmpty();
    }
}
