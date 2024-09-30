package myapp.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import myapp.domain.Product;
import myapp.domain.enumeration.ProductStatus;
import myapp.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        // Validação de Status: deve ser "Disponível", "Esgotado" ou "Descontinuado"
        if (product.getStatus() != null && !isValidStatus(product.getStatus())) {
            throw new InvalidStatusException("Status inválido: deve ser 'Disponível', 'Esgotado' ou 'Descontinuado'.");
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
        if (product.getDateAdded() == null || !product.getDateAdded().equals(LocalDate.now())) {
            throw new InvalidDateAddedException("Data de adição inválida: deve ser a data atual.");
        }

        return productRepository.save(product);
    }

    /**
     * Verifica se o status do produto é válido.
     *
     * @param status o status a ser verificado.
     * @return true se for válido, caso contrário false.
     */
    private boolean isValidStatus(ProductStatus status) {
        return "Disponível".equals(status) || "Esgotado".equals(status) || "Descontinuado".equals(status);
    }
}
