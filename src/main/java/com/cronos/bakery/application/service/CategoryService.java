package com.cronos.bakery.application.service;

import com.cronos.bakery.application.dto.request.CreateCategoryRequest;
import com.cronos.bakery.application.dto.response.CategoryResponse;
import com.cronos.bakery.domain.entity.core.Category;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.persistence.CategoryRepository;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new category
     */
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (categoryRepository.existsByNameAndUser(request.getName(), user)) {
            throw new RuntimeException("Category already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(user)
                .isSystemDefault(false)
                .build();

        category = categoryRepository.save(category);

        log.info("Category created: {} by user: {}", category.getName(), username);

        return mapToResponse(category);
    }

    /**
     * Gets all categories available for a user (system + user's own)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#username")
    public List<CategoryResponse> getUserCategories(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return categoryRepository.findByUserOrIsSystemDefaultTrue(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets system categories
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'system'")
    public List<CategoryResponse> getSystemCategories() {
        return categoryRepository.findSystemCategories().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isSystemDefault(category.getIsSystemDefault())
                .build();
    }
}
