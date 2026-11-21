package com.airlinespoo.customers.repository;

import com.airlinespoo.core.repository.InMemoryRepository;
import com.airlinespoo.customers.model.Country;

/**
 * Repositorio en memoria de Country, este hereda del kit base inmemory
 */

public class CountryRepository extends InMemoryRepository<Country, Integer>{

    @Override
    protected String entityName() {
        return "Country";
    }
}
