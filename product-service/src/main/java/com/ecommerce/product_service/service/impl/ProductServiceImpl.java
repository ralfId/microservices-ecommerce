package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.ProductReqDTO;
import com.ecommerce.product_service.dto.ProductResDTO;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResDTO createProduct(ProductReqDTO productReqDTO) {
        Product product = productMapper.toProduct(productReqDTO);

        Product savedProduct = productRepository.save(product);

        log.info("Product saved successfully: {}", savedProduct.getName());

        return productMapper.toProductResDTO(savedProduct);
    }

    @Override
    public List<ProductResDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(productMapper::toProductResDTO).toList();
    }

    @Override
    public ProductResDTO getProductById(String id) {
        Product product = productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toProductResDTO(product);
    }

    @Override
    public ProductResDTO updateProduct(String id,ProductReqDTO productReqDTO) {
        Product product = productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product", "id", id));

        productMapper.updateProduct(productReqDTO,product);

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully: {}", updatedProduct.getName());

        return productMapper.toProductResDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
        log.info("Product deleted successfully");
    }
}
