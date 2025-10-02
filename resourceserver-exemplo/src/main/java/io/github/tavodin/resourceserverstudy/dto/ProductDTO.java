package io.github.tavodin.resourceserverstudy.dto;

import io.github.tavodin.resourceserverstudy.entities.Product;

public class ProductDTO {

    private Long id;
    private String name;
    private Double price;

    public ProductDTO() {
    }

    public ProductDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.price = entity.getPrice();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
