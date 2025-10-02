package io.github.tavodin.resourceserverstudy.services;

import io.github.tavodin.resourceserverstudy.dto.ProductDTO;
import io.github.tavodin.resourceserverstudy.entities.Product;
import io.github.tavodin.resourceserverstudy.exceptions.ResourceNotFoundException;
import io.github.tavodin.resourceserverstudy.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return repository.findById(id)
                .map(ProductDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("ID not found: " + id));
    }

    @Transactional(readOnly = true)
    public PagedModel<ProductDTO> findAllPaged(Pageable pageable) {
        Page<ProductDTO> page = repository.findAll(pageable)
                .map(ProductDTO::new);

        return new PagedModel<>(page);
    }

    @Transactional
    public ProductDTO save(ProductDTO productDTO) {
        Product entity = new Product();
        dtoToEntity(productDTO, entity);
        return new ProductDTO(repository.save(entity));
    }

    @Transactional
    public ProductDTO update(ProductDTO productDTO) {
        Product entity = repository.findById(productDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ID not found: " + productDTO.getId()));

        entity.setName(productDTO.getName());
        entity.setPrice(productDTO.getPrice());

        return new ProductDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Product entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID not found: " + id));

        repository.delete(entity);
    }

    private void dtoToEntity(ProductDTO dto, Product entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
    }
}
