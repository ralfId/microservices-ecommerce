package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductReqDTO;
import com.ecommerce.product_service.dto.ProductResDTO;

import java.util.List;

public interface ProductService {
    ProductResDTO createProduct(ProductReqDTO productReqDTO);
    List<ProductResDTO> getAllProducts();
    ProductResDTO getProductById(String id);
    ProductResDTO updateProduct(String id, ProductReqDTO productReqDTO);
    void deleteProduct(String id);
}
