package br.com.senai.controllers;

import br.com.senai.models.Coffee;
import br.com.senai.repositories.CoffeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/coffee")
public class CoffeeController {
    @Autowired
    CoffeeRepository coffeeRepository;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Coffee> getAllCoffee() {
        return coffeeRepository.findAll();
    }

    @PostMapping(value = "/createCoffee", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Coffee createCoffee(@RequestBody Coffee coffee) {
        // Cria um novo objeto coffee
        Coffee newCoffee = new Coffee();
        // Seta as propriedades do Coffee
        newCoffee.setName(coffee.getName());
        newCoffee.setDescription(coffee.getDescription());
        newCoffee.setPrice(coffee.getPrice());
        newCoffee.setRating(coffee.getRating());
        // Chama o método save para salvar o objeto no banco de dados
        coffee = coffeeRepository.save(newCoffee);
        return coffee;
    }

    @PutMapping(value = "/updateCoffee/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Coffee> updateCoffee(@PathVariable Long id, @RequestBody Coffee coffee) {
        try {
            Coffee existingCoffee = coffeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Café não encontrado"));
            existingCoffee.setName(coffee.getName());
            existingCoffee.setDescription(coffee.getDescription());
            existingCoffee.setPrice(coffee.getPrice());
            existingCoffee.setRating(coffee.getRating());
            Coffee updatedCoffee = coffeeRepository.save(existingCoffee);
            return ResponseEntity.ok(updatedCoffee);
        } catch (Exception e) {
            // Adiciona um log para ver a exceção
            e.printStackTrace();
            // Retorna um erro 500 com a mensagem da exceção
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // Método deletar coffee
    @DeleteMapping(value = "/deleteCoffee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PathVariable pega um valor passado junto a barra de endereço
    public Coffee deleteCoffee(@PathVariable Long id) {
        // Verificamos se existe o café no banco de dados procurando o id
        Coffee getCoffee = coffeeRepository.findById(id).orElseThrow();
        // Chamamos o método .delete e passamos o café a ser deletado
        coffeeRepository.delete(getCoffee);
        return getCoffee;
    }

    @GetMapping(value = "/ranked", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Coffee> getRankedCoffees() {
        List<Coffee> allCoffees = coffeeRepository.findAll();
        allCoffees.sort(Comparator.comparingInt(Coffee::getRating).reversed());
        return allCoffees;
    }

    // Método filtrar coffee de manutenção de coffee
    @GetMapping(value = "/filtro/{palavra}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Coffee> filtrarLista(@PathVariable String palavra) {
        // Procura por cafés usando a palavra fornecida
        List<Coffee> filteredCoffee = coffeeRepository.findByNameContainingIgnoreCase(palavra);

        // Verifica se algum café foi encontrado
        if (filteredCoffee.isEmpty()) {
            // Nenhum café encontrado, retorna uma lista vazia
            return Collections.emptyList();
        }

        // Cafés encontrados, retorna a lista filtrada
        return filteredCoffee;
    }
}
