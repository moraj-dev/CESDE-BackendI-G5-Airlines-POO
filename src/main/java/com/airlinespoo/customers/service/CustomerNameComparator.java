package com.airlinespoo.customers.service;

import com.airlinespoo.customers.model.Customer;
import java.util.Comparator;

//Ordena clientes por apellido y si son iguales, por el nombre, sin importar si es mayuscula o minuscula

public class CustomerNameComparator implements  Comparator<Customer> {

    @Override
    public int compare(Customer a, Customer b) {
        int byLastName = a.getLastName().compareToIgnoreCase(b.getLastName());
        if (byLastName != 0) {
           return byLastName;
        }
        return a.getFirstName().compareToIgnoreCase(b.getFirstName());
    }
}
