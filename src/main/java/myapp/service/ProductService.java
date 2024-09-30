package myapp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import myapp.domain.Product;
import myapp.domain.enumeration.ProductStatus;
import myapp.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link myapp.domain.Product}.
 */
@Service
@Transactional
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Save a product.
     *
     * @param product the entity to save.
     * @return the persisted entity.
     */
    public Product save(Product product) {
        LOG.debug("Request to save Product : {}", product);

        // Validação do Título: 3 <= t <= 100 caracteres
        if (product.getTitle() == null || product.getTitle().length() < 3 || product.getTitle().length() > 100) {
            throw new InvalidTitleException("Título inválido: deve ter entre 3 e 100 caracteres.");
        }

        // Validação de Palavras-chave: máximo de 200 caracteres
        if (product.getKeywords() != null && product.getKeywords().length() > 200) {
            throw new InvalidKeywordsException("Palavras-chave inválidas: máximo de 200 caracteres.");
        }

        // Validação de Descrição: deve ter pelo menos 10 caracteres
        if (product.getDescription() != null && product.getDescription().length() < 10) {
            throw new InvalidDescriptionException("Descrição inválida: deve ter pelo menos 10 caracteres.");
        }

        // Validação de Avaliação: 0 <= rating <= 5
        if (product.getRating() != null && (product.getRating() < 0 || product.getRating() > 5)) {
            throw new InvalidRatingException("Avaliação inválida: deve estar entre 0 e 5.");
        }

        // Validação de Preço: deve ser >= 0
        if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPriceException("Preço inválido: deve ser maior ou igual a 0.");
        }

        // Validação de Quantidade em estoque: deve ser >= 0
        if (product.getQuantityInStock() != null && product.getQuantityInStock() < 0) {
            throw new InvalidStockQuantityException("Quantidade em estoque inválida: deve ser maior ou igual a 0.");
        }

        // Validação de Status: deve ser "IN_STOCK", "OUT_OF_STOCK" ou "DISCONTINUED"
        if (product.getStatus() == null || !isValidStatus(product.getStatus())) {
            throw new InvalidStatusException("Status inválido: deve ser 'IN_STOCK', 'OUT_OF_STOCK' ou 'DISCONTINUED'.");
        }


        // Validação de Peso: deve ser >= 0
        if (product.getWeight() != null && product.getWeight().compareTo(0.0) < 0) {
            throw new InvalidWeightException("Peso inválido: deve ser maior ou igual a 0.");
        }

        // Validação de Dimensões: máximo de 50 caracteres
        if (product.getDimensions() != null && product.getDimensions().length() > 50) {
            throw new InvalidDimensionsException("Dimensões inválidas: máximo de 50 caracteres.");
        }

        // Validação de Data de Adição: não pode ser nula e deve ser igual à data atual
        if (product.getDateAdded() == null || !isDateToday(product.getDateAdded())) {
            throw new InvalidDateAddedException("Data de adição inválida: deve ser a data atual.");
        }

        return productRepository.save(product);
    }

    private boolean isDateToday(Instant dateAdded) {
        // Converte o Instant para LocalDate (ignorando o horário)
        LocalDate addedDate = dateAdded.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        // Verifica se a data de adição é igual à data atual (ignorando o horário)
        return addedDate.equals(today);
    }
    /**
     * Verifica se o status do produto é válido.
     *
     * @param status o status a ser verificado.
     * @return true se for válido, caso contrário false.
     */
    private boolean isValidStatus(ProductStatus status) {
        return status == ProductStatus.IN_STOCK ||
            status == ProductStatus.OUT_OF_STOCK ||
            status == ProductStatus.DISCONTINUED;
    }

    /**
     * Update an existing product.
     *
     * @param product the entity to update (with validation).
     * @return the updated product.
     */
    public @Valid Product update(@Valid Product product) {
        // Validação manual extra (caso necessário)
        validateProduct(product);

        // Buscar o produto existente
        Optional<Product> existingProductOpt = productRepository.findById(product.getId());

        if (existingProductOpt.isEmpty()) {
            throw new ProductNotFoundException("Product not found with ID: " + product.getId());
        }

        Product existingProduct = existingProductOpt.get();

        // Atualiza os campos do produto existente com os novos valores
        existingProduct.setTitle(product.getTitle());
        existingProduct.setKeywords(product.getKeywords());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setRating(product.getRating());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantityInStock(product.getQuantityInStock());
        existingProduct.setStatus(product.getStatus());
        existingProduct.setWeight(product.getWeight());
        existingProduct.setDimensions(product.getDimensions());
        existingProduct.setDateModified(product.getDateModified());

        // Salva o produto atualizado
        return productRepository.save(existingProduct);
    }

    // Validações manuais (caso você precise de validações adicionais além do @Valid)
    private void validateProduct(Product product) {
        if (product.getTitle() == null || product.getTitle().length() < 3 || product.getTitle().length() > 100) {
            throw new InvalidTitleException("Título inválido: deve ter entre 3 e 100 caracteres.");
        }
        // Outras validações podem ser feitas aqui, como você já tem no método save
    }

    /**
     * Partial update of a product.
     *
     * @param product the entity to update partially.
     * @return the updated entity.
     */
    public Optional<Product> partialUpdate(Product product) {
        // Buscando o produto existente pelo ID
        return productRepository
            .findById(product.getId())
            .map(existingProduct -> {
                // Atualiza apenas os campos que foram fornecidos (não-nulos)
                if (product.getTitle() != null) {
                    existingProduct.setTitle(product.getTitle());
                }
                if (product.getKeywords() != null) {
                    existingProduct.setKeywords(product.getKeywords());
                }
                if (product.getDescription() != null) {
                    existingProduct.setDescription(product.getDescription());
                }
                if (product.getRating() != null) {
                    existingProduct.setRating(product.getRating());
                }
                if (product.getPrice() != null) {
                    existingProduct.setPrice(product.getPrice());
                }
                if (product.getQuantityInStock() != null) {
                    existingProduct.setQuantityInStock(product.getQuantityInStock());
                }
                if (product.getStatus() != null) {
                    existingProduct.setStatus(product.getStatus());
                }
                if (product.getWeight() != null) {
                    existingProduct.setWeight(product.getWeight());
                }
                if (product.getDimensions() != null) {
                    existingProduct.setDimensions(product.getDimensions());
                }
                if (product.getDateAdded() != null) {
                    existingProduct.setDateAdded(product.getDateAdded());
                }
                if (product.getDateModified() != null) {
                    existingProduct.setDateModified(product.getDateModified());
                }

                // Salva o produto com as mudanças parciais
                return productRepository.save(existingProduct);
            });
    }

    /**
     * Get all products with pagination support.
     *
     * @param pageable the pagination information.
     * @return a page of products.
     */
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the product.
     * @return an Optional containing the product if found, or empty otherwise.
     */
    @Transactional(readOnly = true)
    public Optional<Product> findOne(Long id) {
        return productRepository.findById(id);
    }


    /**
     * Delete a product by id.
     *
     * @param id the id of the product to delete.
     */
    public void delete(Long id) {
        // Verifica se o produto existe
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }

        // Exclui o produto
        productRepository.deleteById(id);
    }
}
