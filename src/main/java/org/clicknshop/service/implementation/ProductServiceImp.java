package org.clicknshop.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clicknshop.dto.request.ProductRequestDto;
import org.clicknshop.dto.response.ProductResponseDto;
import org.clicknshop.exception.DuplicateResourceException;
import org.clicknshop.exception.ResourceNotFoundException;
import org.clicknshop.mapper.ProductMapper;
import org.clicknshop.model.entity.Product;
import org.clicknshop.repository.ProductRepository;
import org.clicknshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto){
        log.info("Création d'un nouveau produit : {}",productRequestDto.getName());

        if(productRepository.existsByName(productRequestDto.getName())){
            throw new DuplicateResourceException("Un produit deja existe avec ce nom");
        }

        Product product = productMapper.toEntity(productRequestDto);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponseDto(savedProduct);

    }

    @Override
    public Page<ProductResponseDto> getAllProducts(Pageable pageable){
        log.info("Récupération de tous les produits");
        Page<Product> productsPage = productRepository.findAll(pageable);
        return productsPage.map(productMapper::toResponseDto);
    }

    @Override
    public ProductResponseDto getProductById(Long id ){
        Product product = productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Produit introuvable avec l'id " + id));

        return productMapper.toResponseDto(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id){
        log.info("Suppression du produit avec l'ID: {}", id);

        Product product = productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Produit introuvable avec l'id " + id)) ;

        productRepository.delete(product);
    }

    @Override
    public boolean existsById(Long id){
        return productRepository.existsById(id);
    }

    @Override
    public List<ProductResponseDto> searchByName(String name){
        List<Product> products = productRepository.searchByName(name);
        return productMapper.toResponseDtoList(products);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id,ProductRequestDto productRequestDto){
        log.info("Mise à jour du produit avec l'ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'ID: " + id));

        productMapper.updateEntityFromDto(productRequestDto ,product);

        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponseDto(updatedProduct);
    }




}
