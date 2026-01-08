package com.ecommerce_backend.service;

import com.ecommerce_backend.dto.ProductRequestDTO;
import com.ecommerce_backend.dto.ProductResponseDTO;
import com.ecommerce_backend.model.Product;

import java.util.List;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO dto);

    List<ProductResponseDTO> getAllProducts();

    Product getProductById(Long id);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);
}
