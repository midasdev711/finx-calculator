package com.finx.persistence;

import com.finx.domain.products.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Override
    Set<Product> findAll();

    Optional<Product> findByName(String name);
}
