package com.ecommerce.inventory_service.mapper;

import com.ecommerce.inventory_service.dto.InventoryReqDTO;
import com.ecommerce.inventory_service.dto.InventoryResDTO;
import com.ecommerce.inventory_service.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    Inventory reqDtoToInventory(InventoryReqDTO inventoryReqDTO);

    @Mapping(target = "inStock", expression = "java(inventory.getQuantity() > 0)")
    InventoryResDTO inventoryToResDto(Inventory inventory);
}
