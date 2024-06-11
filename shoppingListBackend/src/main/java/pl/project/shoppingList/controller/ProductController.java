package pl.project.shoppingList.controller;

import org.springframework.http.ResponseEntity;
import pl.project.shoppingList.model.Product;
import pl.project.shoppingList.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product product = productService.getProductById(id);

        if (product != null) {
            product.setName(productDetails.getName());
            product.setQuantity(productDetails.getQuantity());
            product.setUnit(productDetails.getUnit());
            product.setIsInteger(productDetails.getIsInteger());
            return productService.saveProduct(product);
        }

        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public void deleteAllProducts() {
        productService.deleteAllProducts();
    }
}
