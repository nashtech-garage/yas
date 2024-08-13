package com.yas.inventory.controller;

import com.yas.inventory.constants.ApiConstant;
import com.yas.inventory.constants.PageableConstant;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.service.WarehouseService;
import com.yas.inventory.viewmodel.error.ErrorVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseDetailVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseListGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(ApiConstant.WAREHOUSE_URL)
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping("/{warehouseId}/products")
    public ResponseEntity<List<ProductInfoVm>> getProductByWarehouse(
        @PathVariable Long warehouseId, @RequestParam String productName,
        @RequestParam String productSku, @RequestParam FilterExistInWhSelection existStatus) {
        return ResponseEntity.ok(
            warehouseService.getProductWarehouse(warehouseId, productName, productSku, existStatus));
    }

    @GetMapping("/paging")
    public ResponseEntity<WarehouseListGetVm> getPageableWarehouses(
        @RequestParam(value = "pageNo", defaultValue = PageableConstant.DEFAULT_PAGE_NUMBER, required = false)
        final int pageNo,
        @RequestParam(value = "pageSize", defaultValue = PageableConstant.DEFAULT_PAGE_SIZE, required = false)
        final int pageSize) {
        return ResponseEntity.ok(warehouseService.getPageableWarehouses(pageNo, pageSize));
    }

    @GetMapping
    public ResponseEntity<List<WarehouseGetVm>> listWarehouses() {
        return ResponseEntity.ok(warehouseService.findAllWarehouses());
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_200, description = ApiConstant.OK,
            content = @Content(schema = @Schema(implementation = WarehouseGetVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_404, description = ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<WarehouseDetailVm> getWarehouse(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(warehouseService.findById(id));
    }

    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_201, description = ApiConstant.CREATED,
            content = @Content(schema = @Schema(implementation = WarehouseGetVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<WarehouseGetVm> createWarehouse(
        @Valid @RequestBody final WarehousePostVm warehousePostVm,
        final UriComponentsBuilder uriComponentsBuilder) {
        final Warehouse warehouse = warehouseService.create(warehousePostVm);
        return ResponseEntity.created(
                uriComponentsBuilder
                    .replacePath("/warehouses/{id}")
                    .buildAndExpand(warehouse.getId())
                    .toUri())
            .body(WarehouseGetVm.fromModel(warehouse));
    }

    @PutMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_204, description = ApiConstant.NO_CONTENT, content = @Content()),
        @ApiResponse(responseCode = ApiConstant.CODE_404, description = ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateWarehouse(@PathVariable final Long id,
                                                @Valid @RequestBody final WarehousePostVm warehousePostVm) {
        warehouseService.update(warehousePostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_204, description = ApiConstant.NO_CONTENT, content = @Content()),
        @ApiResponse(responseCode = ApiConstant.CODE_404, description = ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteWarehouse(@PathVariable(name = "id") final Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

