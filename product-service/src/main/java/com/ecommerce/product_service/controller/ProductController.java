package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.ProductReqDTO;
import com.ecommerce.product_service.dto.ProductResDTO;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResDTO addProduct(@RequestBody @Validated ProductReqDTO productReqDTO) {
        return  productService.createProduct(productReqDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResDTO getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResDTO updateProduct(@PathVariable String id, @RequestBody @Validated ProductReqDTO productReqDTO) {
        return productService.updateProduct(id, productReqDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable String id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/testfail")
    public void testfail() {
        throw new RuntimeException("testfail");
    }
}
