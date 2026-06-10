package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.ProductReqDTO;
import com.ecommerce.product_service.dto.ProductResDTO;
import com.ecommerce.product_service.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductReqDTO productReqDTO);

    ProductResDTO toProductResDTO(Product product);

    @Mapping(target = "id", ignore = true)
    void updateProduct(ProductReqDTO productReqDTO, @MappingTarget Product product);
}
