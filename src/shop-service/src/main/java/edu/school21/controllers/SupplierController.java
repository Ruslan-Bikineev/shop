package edu.school21.controllers;

import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.SupplierDto;
import edu.school21.entity.Supplier;
import edu.school21.services.SupplierService;
import edu.school21.utils.MapperUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final MapperUtil mapperUtil;
    private final SupplierService supplierService;

    @GeneralApiResponses(summary = "Get supplier by id")
    @GetMapping("/{id}")
    public SupplierDto getSupplier(@PathVariable("id") UUID id) {
        Supplier supplier = supplierService.findById(id);
        return mapperUtil.mapToSupplierDto(supplier);
    }

    @GeneralApiResponses(summary = "Get all suppliers with pagination")
    @GetMapping
    public List<SupplierDto> getSuppliers(
            @RequestParam(required = false)
            @Min(value = 1, message = "must be greater than 0")
            @Max(value = 25, message = "must be less than 26")
            Integer limit,
            @RequestParam(required = false)
            @Min(value = 0, message = "must be positive")
            Integer offset) {
        if (limit == null || offset == null) {
            return supplierService.findAll().stream()
                    .map(mapperUtil::mapToSupplierDto)
                    .toList();
        }
        return supplierService.findAll(PageRequest.of(offset, limit)).stream()
                .map(mapperUtil::mapToSupplierDto)
                .toList();
    }

    @GeneralApiResponses(summary = "Create supplier")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierDto createSupplier(@Valid @RequestBody SupplierDto supplierDto) {
        Supplier supplier = mapperUtil.mapToSupplier(supplierDto);
        supplier = supplierService.saveSupplier(supplier);
        return mapperUtil.mapToSupplierDto(supplier);
    }

    @GeneralApiResponses(summary = "Update supplier by id")
    @PatchMapping("/{id}")
    public SupplierDto patchUpdateSupplier(
            @PathVariable("id") UUID id,
            @RequestBody SupplierDto supplierDto) {
        Supplier supplier = mapperUtil.mapToSupplier(supplierDto);
        supplier = supplierService.patchUpdateSupplier(id, supplier);
        return mapperUtil.mapToSupplierDto(supplier);
    }

    @GeneralApiResponses(summary = "Create or update supplier by id")
    @PutMapping("/{id}")
    public SupplierDto putUpdateSupplier(
            @PathVariable("id") UUID id,
            @Valid @RequestBody SupplierDto supplierDto) {
        Supplier supplier = mapperUtil.mapToSupplier(supplierDto);
        supplier = supplierService.putUpdateSupplier(id, supplier);
        return mapperUtil.mapToSupplierDto(supplier);
    }

    @GeneralApiResponses(summary = "Delete supplier by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSupplier(@PathVariable("id") UUID id) {
        supplierService.deleteSupplier(id);
    }
}
