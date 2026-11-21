package com.airlinespoo.customers.repository;

import com.airlinespoo.core.repository.InMemoryRepository;
import com.airlinespoo.customers.model.DocumentType;

/**
 * Repositorio en memoria de DocumentType, este hereda del kit base inmemory
 */
public class DocumentTypeRepository extends InMemoryRepository<DocumentType,String>{

    @Override
    protected String entityName(){
        return "Tipo de documento";
    }
}
