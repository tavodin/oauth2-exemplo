package io.github.tavodin.resourceserverstudy.repositories;

import io.github.tavodin.resourceserverstudy.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
