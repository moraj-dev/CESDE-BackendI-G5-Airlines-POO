package com.airlinespoo.customers.service;

import com.airlinespoo.customers.model.Country;
import java.util.Comparator;

//Ordenar paises por nombre sin importar mayusculas o minusculas

public class CountryNameComparator implements Comparator<Country>{

    @Override
    public int compare(Country a, Country b) {
        return a.getName().compareToIgnoreCase(b.getName());
    }
}
