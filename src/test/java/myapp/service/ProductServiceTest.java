package myapp.service;

import myapp.domain.Product;
import myapp.domain.enumeration.ProductStatus;
import myapp.repository.ProductRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService; // Injects the mock into the service

    // Helper method to create a sample product with flexible parameters
    public static Product createProductSample(Long id, String title, String keywords, String description, int rating, int quantityInStock, String dimensions, BigDecimal price) {
        Product product = new Product()
            .id(id)
            .title(title)
            .keywords(keywords)
            .description(description)
            .rating(rating)
            .quantityInStock(quantityInStock)
            .dimensions(dimensions)
            .price(price);

        product.setDateAdded(Instant.now());
        product.setDateModified(Instant.now());

        return product;
    }

    @Test
    public void testTitleEquivalencePartition() {
        Product validTitleMin = createProductSample(1L, "Van", null, null, 0, 0, null, BigDecimal.ZERO);
        validTitleMin.setStatus(ProductStatus.IN_STOCK);

        Product validTitleMax = createProductSample(2L, "Loja de personagens de filmes, desenhos, séries, anime, jogos, gibis e mais formas de entretenimento", null, null, 0, 0, null, BigDecimal.ZERO);
        validTitleMax.setStatus(ProductStatus.IN_STOCK);

        when(productRepository.save(validTitleMin)).thenReturn(validTitleMin);
        when(productRepository.save(validTitleMax)).thenReturn(validTitleMax);

        assertNotNull(productService.save(validTitleMin)); // Título válido (min)
        assertNotNull(productService.save(validTitleMax)); // Título válido (max)

        Product invalidTitleTooShort = createProductSample(3L, "Al", null, null, 0, 0, null, BigDecimal.ZERO);
        invalidTitleTooShort.setStatus(ProductStatus.IN_STOCK);

        Product invalidTitleTooLong = createProductSample(4L, "Loja de personagens de filmes, desenhos, séries, anime, jogos, gibis e mais formas de entretenimento!", null, null, 0, 0, null, BigDecimal.ZERO);
        invalidTitleTooLong.setStatus(ProductStatus.IN_STOCK);

        assertThrows(InvalidTitleException.class, () -> productService.save(invalidTitleTooShort));
        assertThrows(InvalidTitleException.class, () -> productService.save(invalidTitleTooLong));
    }

    @Test
    public void testKeywordsEquivalencePartition() {
        // Palavras-chave com 200 caracteres (válido)
        Product validKeywords = createProductSample(1L, "Van", "Produto extremamente interessante, prático, bonito e que vai te fazer feliz. Com ele você pode presentear pessoas queridas e mostrar o verdadeiro significado da amizade. Esse produto tem boa qualidade", null, 0, 0, null, BigDecimal.ZERO);
        validKeywords.setStatus(ProductStatus.IN_STOCK);

        when(productRepository.save(validKeywords)).thenReturn(validKeywords);
        assertNotNull(productService.save(validKeywords)); // Palavras-chave válidas

        // Palavras-chave com mais de 200 caracteres (inválido)
        Product invalidKeywordsTooLong = createProductSample(2L, "Van", "Produto extremamente interessante, prático, bonito e que vai te fazer feliz. Com ele você pode presentear pessoas queridas e mostrar o verdadeiro significado da amizade. Esse produto tem uma boa qualidade!", null, 0, 0, null, BigDecimal.ZERO);
        invalidKeywordsTooLong.setStatus(ProductStatus.IN_STOCK);

        assertThrows(InvalidKeywordsException.class, () -> productService.save(invalidKeywordsTooLong)); // Palavras-chave muito longas
    }


    @Test
    public void testDescriptionEquivalencePartition() {
        // Descrição com 10 caracteres (válido)
        Product validDescription = createProductSample(1L, "Van", null, "Mangá fofo", 0, 0, null, BigDecimal.ZERO);
        validDescription.setStatus(ProductStatus.IN_STOCK);

        when(productRepository.save(validDescription)).thenReturn(validDescription);
        assertNotNull(productService.save(validDescription)); // Descrição válida

        // Descrição com menos de 10 caracteres (inválido)
        Product invalidDescriptionTooShort = createProductSample(2L, "Van", null, "Mangá", 0, 0, null, BigDecimal.ZERO);
        invalidDescriptionTooShort.setStatus(ProductStatus.IN_STOCK);

        assertThrows(InvalidDescriptionException.class, () -> productService.save(invalidDescriptionTooShort)); // Descrição muito curta
    }

    @Test
    public void testRatingEquivalencePartition() {
        Product validRatingMin = createProductSample(1L, "Van", null, null, 0, 0, null, BigDecimal.ZERO);
        validRatingMin.setStatus(ProductStatus.IN_STOCK);

        Product validRatingMax = createProductSample(2L, "Van", null, null, 5, 0, null, BigDecimal.ZERO);
        validRatingMax.setStatus(ProductStatus.IN_STOCK);

        when(productRepository.save(validRatingMin)).thenReturn(validRatingMin);
        when(productRepository.save(validRatingMax)).thenReturn(validRatingMax);

        assertNotNull(productService.save(validRatingMin)); // Avaliação válida (min)
        assertNotNull(productService.save(validRatingMax)); // Avaliação válida (max)

        Product invalidRatingNegative = createProductSample(3L, "Van", null, null, -1, 0, null, BigDecimal.ZERO);
        invalidRatingNegative.setStatus(ProductStatus.IN_STOCK);

        Product invalidRatingTooHigh = createProductSample(4L, "Van", null, null, 6, 0, null, BigDecimal.ZERO);
        invalidRatingTooHigh.setStatus(ProductStatus.IN_STOCK);

        assertThrows(InvalidRatingException.class, () -> productService.save(invalidRatingNegative)); // Avaliação negativa
        assertThrows(InvalidRatingException.class, () -> productService.save(invalidRatingTooHigh));  // Avaliação maior que 5
    }

    @Test
    public void testPriceEquivalencePartition() {
        // Preço 0 (válido)
        Product validPriceMin = createProductSample(1L, "Van", null, null, 0, 0, null, BigDecimal.ZERO);
        validPriceMin.setStatus(ProductStatus.IN_STOCK);

        when(productRepository.save(validPriceMin)).thenReturn(validPriceMin);
        assertNotNull(productService.save(validPriceMin)); // Preço válido

        // Preço negativo (inválido)
        Product invalidPriceNegative = createProductSample(2L, "Van", null, null, 0, 0, null, BigDecimal.valueOf(-1));
        invalidPriceNegative.setStatus(ProductStatus.DISCONTINUED);

        assertThrows(InvalidPriceException.class, () -> productService.save(invalidPriceNegative)); // Preço negativo

    }


    @Test
    public void testStockQuantityEquivalencePartition() {
        // Quantidade 0 (válido)
        Product validStockMin = createProductSample(1L, "Van", null, null, 0, 0, null, BigDecimal.ZERO);
        validStockMin.setStatus(ProductStatus.OUT_OF_STOCK);

        when(productRepository.save(validStockMin)).thenReturn(validStockMin);
        assertNotNull(productService.save(validStockMin)); // Quantidade válida

        // Quantidade negativa (inválido)
        Product invalidStockNegative = createProductSample(2L, "Van", null, null, 0, -1, null, BigDecimal.ZERO);
        invalidStockNegative.setStatus(ProductStatus.DISCONTINUED);

        assertThrows(InvalidStockQuantityException.class, () -> productService.save(invalidStockNegative)); // Quantidade negativa
    }

    @Test
    public void testStatusEquivalencePartition() {
        // Status "IN_STOCK" (válido)
        Product validStatusAvailable = createProductSample(1L, "Van", null, null, 0, 0, null, BigDecimal.ZERO);
        validStatusAvailable.setStatus(ProductStatus.IN_STOCK);

        when(productRepository.save(validStatusAvailable)).thenReturn(validStatusAvailable);
        assertNotNull(productService.save(validStatusAvailable)); // Status válido

        // Status inválido
        Product invalidStatus = createProductSample(2L, "Van", null, null, 0, 0, null, BigDecimal.ZERO);
        invalidStatus.setStatus(null);

        assertThrows(InvalidStatusException.class, () -> productService.save(invalidStatus));
    }

    @Test
    public void testDimensionsEquivalencePartition() {
        // Dimensões com exatamente 50 caracteres (válido)
        Product validDimensions = createProductSample(1L, "Van", null, null, 0, 0, "12345678901234567890123456789012345678901234567890", BigDecimal.ZERO);
        validDimensions.setStatus(ProductStatus.OUT_OF_STOCK);

        when(productRepository.save(validDimensions)).thenReturn(validDimensions);
        assertNotNull(productService.save(validDimensions)); // Dimensões válidas

        // Dimensões com mais de 50 caracteres (inválido)
        Product invalidDimensionsTooLong = createProductSample(2L, "Van", null, null, 0, 0, "123456789012345678901234567890123456789012345678901", BigDecimal.ZERO); // 51 caracteres
        invalidDimensionsTooLong.setStatus(ProductStatus.OUT_OF_STOCK);

        assertThrows(InvalidDimensionsException.class, () -> productService.save(invalidDimensionsTooLong)); // Dimensões muito longas
    }

}
