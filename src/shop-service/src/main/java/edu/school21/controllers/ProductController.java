package edu.school21.controllers;

import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.ProductDto;
import edu.school21.entity.Product;
import edu.school21.services.ProductService;
import edu.school21.utils.MapperUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/products")
public class ProductController {

    private final MapperUtil mapperUtil;
    private final ProductService productService;

    @GeneralApiResponses(summary = "Get product by id")
    @GetMapping("/{id}")
    public ProductDto findById(@PathVariable("id") UUID id) {
        Product product = productService.findById(id);
        return mapperUtil.mapToProductDto(product);
    }

    @GeneralApiResponses(summary = "Get all products with pagination")
    @GetMapping
    public List<ProductDto> findAll(
            @RequestParam(required = false)
            @Min(value = 1, message = "must be greater than 0")
            @Max(value = 25, message = "must be less than 26")
            Integer limit,
            @RequestParam(required = false)
            @Min(value = 0, message = "must be positive")
            Integer offset) {
        if (limit == null || offset == null) {
            return productService.findAll().stream()
                    .map(mapperUtil::mapToProductDto)
                    .toList();
        }
        return productService.findAll(PageRequest.of(offset, limit)).stream()
                .map(mapperUtil::mapToProductDto)
                .toList();
    }

    @GeneralApiResponses(summary = "Get product image by product id")
    @GetMapping(value = "/image/{id}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getImage(@PathVariable("id") UUID id) {
        return productService.getImageByProductId(id);
    }

    @GeneralApiResponses(summary = "Create product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto) {
        Product product = mapperUtil.mapToProduct(productDto);
        product = productService.saveProduct(product);
        return mapperUtil.mapToProductDto(product);
    }

    @GeneralApiResponses(summary = "Create or update product by id")
    @PutMapping("/{id}")
    public ProductDto putUpdateProduct(@PathVariable("id") UUID id,
                                       @Valid @RequestBody ProductDto productDto) {
        Product product = mapperUtil.mapToProduct(productDto);
        product = productService.putUpdateProduct(id, product);
        return mapperUtil.mapToProductDto(product);

    }

    @GeneralApiResponses(summary = "Update product by id")
    @PatchMapping("/{id}")
    public ProductDto patchUpdateProduct(@PathVariable("id") UUID id,
                                         @RequestBody ProductDto productDto) {
        Product product = mapperUtil.mapToProduct(productDto);
        product = productService.patchUpdateProduct(id, product);
        return mapperUtil.mapToProductDto(product);
    }

    @GeneralApiResponses(summary = "Reducing the amount of products in stock by id product")
    @PatchMapping("/amount/{id}")
    public ProductDto patchReductionAvailableProduct(
            @PathVariable("id") UUID id,
            @Min(value = 0, message = "amount: must be greater than 0")
            @RequestParam("amount") Integer amount) {
        Product product = productService.patchReductionAvailableProduct(id, amount);
        return mapperUtil.mapToProductDto(product);
    }

    @GeneralApiResponses(summary = "Delete product by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") UUID id) {
        productService.deleteById(id);
    }
}
