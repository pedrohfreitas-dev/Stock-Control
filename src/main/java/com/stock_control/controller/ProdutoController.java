package com.stock_control.controller;



import com.stock_control.dto.PrecoDTO;
import com.stock_control.exception.EstoqueCheioException;
import com.stock_control.exception.ProdutoNaoEncontradoException;
import com.stock_control.model.Produto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private ArrayList<Produto> produtos = new ArrayList<>();
    private Long nextId = 1L;

    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody Produto produto)  {
        if (produtos.size() > 10) {
             throw new EstoqueCheioException("Estoque cheio");
        }

        produto.setId(nextId++);
        produtos.add(produto);
       return ResponseEntity.ok("Produto adicionado com sucesso!");
    }

    @GetMapping
    public List<Produto> getProducts() {
        return produtos;
    }

    @GetMapping("/{id}")
    public Produto getProductById(@PathVariable long id) {
        return produtos.stream().filter(produto -> produto.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ProdutoNaoEncontradoException(("Produto não encontrado")));

    }

    @GetMapping("/baixo-estoque")
    public List<Produto> getLowPrice() {
       return produtos.stream().filter(produto -> produto.getQuantidade() < 3).collect(Collectors.toList());
    }

    @GetMapping("/categoria")
    public List<Produto> getProdutosByCategory(@RequestParam String categoria)  {
        return produtos.stream().filter(produto -> produto.getCategoria()
                        .equalsIgnoreCase(categoria)).
                collect(Collectors.toList());
    }

    @PutMapping("/update-preco/{id}")
    public ResponseEntity<String> updateValueProduct(@RequestBody PrecoDTO dto, @PathVariable long id) {
        Produto updateProduct = produtos.stream().filter(produto -> produto.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));
         updateProduct.setPreco(dto.getNovoPreco());
        return ResponseEntity.ok("Preço alterado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id) {
        Produto removed = produtos.stream().filter(produto -> produto.getId() == id).findFirst()
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto com id " + id + " não encontrado"));
        produtos.remove(removed);
        return ResponseEntity.ok("Produto removido com sucesso!");
    }
}
