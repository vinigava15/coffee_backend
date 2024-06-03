package br.com.senai.repositories;

import br.com.senai.models.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoffeeRepository extends JpaRepository<Coffee,Long> {
    List<Coffee> findByNameContainingIgnoreCase(String name);
}
