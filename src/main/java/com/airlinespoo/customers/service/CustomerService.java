package com.airlinespoo.customers.service;

import com.airlinespoo.core.exception.BusinessRuleException;
import com.airlinespoo.core.exception.DuplicateException;
import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.core.repository.Repository;
import com.airlinespoo.core.util.Validator;
import com.airlinespoo.customers.model.Customer;
import com.airlinespoo.customers.model.Country;
import com.airlinespoo.customers.model.DocumentType;
import com.airlinespoo.customers.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

/**
 * El servicio coordina: pide datos a los respositorios y comprueba las reglas de los objetos y lanza excepciones
 * Se reciben los datos del repositorio por medio del constructor
 */
public class CustomerService {

    private final Repository<Country, Integer> countryRepository;
    private final Repository<DocumentType, String> documentTypeRepository;
    private final CustomerRepository customerRepository;

    /**
     * Creación del servicio con los repositorios que se van a usar
     */
    public CustomerService(Repository<Country, Integer> countryRepository,
                           Repository<DocumentType, String> documentTypeRepository,
                           CustomerRepository customerRepository) {
        this.countryRepository = countryRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.customerRepository = customerRepository;
    }

    //-----------------------------------------------------------//--------------------------------------------------//

    /**
     * Registra un país (Create)
     */
    public Country createCountry(int id, String name) {
        return countryRepository.create(new Country(id, name));
    }

    /**
     * Consulta un país por su id (Read)
     */
    public Country getCountry(int id) {
        Optional<Country> country = countryRepository.findById(id);
        if (country.isEmpty()) {
            throw new NotFoundException("país", id); // Si no existe
        }
        return country.get();
    }

    /**
     * Lista los países ordenados por nombre
     */
    public List<Country> listCountries() {
        List<Country> countries = countryRepository.findAll();
        countries.sort(new CountryNameComparator());
        return countries;
    }

    /**
     * Cambiar nombre de un país (Update)
     */
    public Country updateCountry(int id, String name) {
        Country country = getCountry(id);
        country.setName(name);
        return countryRepository.update(country);
    }

    /**
     * Eliminar un país (Delete) y solo si ningun cliente pertenece a el
     */
    public void deleteCountry(int id) {
        Country country = getCountry(id);
        if (customerRepository.existsByCountry(country)) {
            throw new BusinessRuleException("No se puede eliminar el país '" + country.getName()
                + "' debido a que ya tiene clientes");
        }
        countryRepository.deleteById(id);
    }

    //--------------------------------------------------------//------------------------------------------------------//

    /**
     * Registrar un tipo de documento (Insert)
     */
    public DocumentType createDocumentType(String code, String description) {
        return documentTypeRepository.create(new DocumentType(code, description));
    }

    /**
     * Consulta un tipo de documento por code (Select) y se normaliza así cc encuentra a CC
     */
    public DocumentType getDocumentType(String code) {
        String normalized = DocumentType.normalizeCode(code);
        Optional<DocumentType> documentType = documentTypeRepository.findById(normalized);
        if (documentType.isEmpty()) {
            throw new NotFoundException("tipo de documento", normalized);
        }
        return documentType.get();
    }

    /**
     * Lista los tipos de documento en orden de registro
     */
    public List<DocumentType> listDocumentTypes() {
        return documentTypeRepository.findAll();
    }

    /**
     * Cambia la descripcion de un tipo de documento (UPDATE)
     */
    public DocumentType updateDocumentType(String code, String description) {
        DocumentType documentType = getDocumentType(code);
        documentType.setDescription(description);
        return documentTypeRepository.update(documentType);
    }

    /**
     * Elimina un tipo de documento (Delete) solo si ningun cliente lo usa
     */
    public void deleteDocumentType(String code) {
        DocumentType documentType = getDocumentType(code);
        if (customerRepository.existsByDocumentType(documentType)) {
            throw new BusinessRuleException("No se puede eliminar el tipo de documento '"
                + documentType.getId() + "' porque hay clientes que lo usan");
        }
        documentTypeRepository.deleteById(documentType.getId());
    }

    //----------------------------------------------//---------------------------------------------------------------//
    /**
     * Registrar un cliente, donde el DocumentType y Country ya deben existir.
     * El documento y el correo no pueden repetirse
     */
    public Customer createCustomer(String id, String firstName, String lastName, String documentTypeCode,
                                   String telephoneNumber, String email, int countryId, String address) {
        DocumentType documentType = getDocumentType(documentTypeCode);
        Country country = getCountry(countryId);
        Customer customer = new Customer(id, firstName, lastName, documentType, telephoneNumber, country);
        customer.setAddress(address);
        customer.setEmail(email);
        if (customerRepository.existsById(customer.getId())) {
            throw new DuplicateException("Cliente", customer.getId());
        }
        requireUniqueEmail(customer.getEmail(), customer.getId());
        return customerRepository.create(customer);
    }

    /**
     * Consulta un cliente por documento
     */
    public Customer getCustomer(String id) {
        Optional<Customer> customer = customerRepository.findById(Validator.normalize(id));
        if (customer.isEmpty()) {
            throw new NotFoundException("Cliente", id);
        }
        return customer.get();
    }

    /**
     * Lista a los clientes ordenados por apellido y luego por nombre
     */
    public List<Customer> listCustomers() {
        return sortByName(customerRepository.findAll());
    }

    /**
     * Actualiza los datos de contacto del cliente como e telefono, la direccion y el correo
     */
    public Customer updateCustomer(String id, String telephoneNumber, String email, String address) {
        Customer customer = getCustomer(id);
        requireUniqueEmail(email, customer.getId());
        customer.updateContact(telephoneNumber, email, address);
        return customerRepository.update(customer);
    }

    /**
     * Elimina un cliente (por ahora siempre se permite)
     */
    public void deleteCustomer(String id) {
        Customer customer = getCustomer(id);
        customerRepository.deleteById(customer.getId());
    }

    /**
     * Busca un cliente por correo
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Busca clientes por parte del apellido, sin importar si es mayuscula o minuscula
     */
    public List<Customer> searchCustomersByLastName(String text) {
        String wanted = Validator.requireText(text, "apellido a buscar");
        return sortByName(customerRepository.findByLastNameContaining(wanted));
    }

    /**
     * Lista a los clientes de un país
     */
    public List<Customer> listCustomersByCountry(int countryId) {
        Country country = getCountry(countryId);
        return sortByName(customerRepository.findByCountry(country));
    }

    /**
     * Lista a los clientes con un tipo de documento
     */
    public List<Customer> listCustomersByDocumentType(String documentTypeCode) {
        DocumentType documentType = getDocumentType(documentTypeCode);
        return sortByName(customerRepository.findByDocumentType(documentType));
    }

    /**
     * Cuenta a los clientes registrados (Select Count)
     */
    public long countCustomers() {
        return customerRepository.count();
    }

    /**
     * El correo no puede repetirse o ser de otro cliente
     */
    private void requireUniqueEmail(String email, String ownerId) {
        Optional<Customer> other = customerRepository.findByEmail(Validator.normalize(email));
        if (other.isPresent() && !other.get().getId().equals(ownerId)) {
            throw new DuplicateException("Ya existe un cliente con el correo '" + other.get().getEmail() + "'");
        }
    }

    private static List<Customer> sortByName(List<Customer> customers) {
        customers.sort(new CustomerNameComparator());
        return customers;
    }
}
