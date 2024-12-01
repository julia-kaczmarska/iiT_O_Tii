package back.service;

import back.controller.dto.CategoryDTO;
import back.model.Category;
import back.model.User;
import back.repository.CategoryRepository;
import back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    public List<CategoryDTO> getCategoriesByUserId(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return categoryRepository.findByUserId(userId);
    }

    public CategoryDTO addCategory(CategoryDTO categoryDTO, Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Category category = new Category();
        category.setCategoryId(categoryDTO.getCategoryId());
        category.setTitle(categoryDTO.getTitle());
        category.setColor(categoryDTO.getColor());
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);
        return new CategoryDTO(savedCategory.getCategoryId(), savedCategory.getTitle(), savedCategory.getColor());
    }
    public CategoryDTO updateCategoryTitle(Long userId, Long categoryId, String title) {
        Category category = categoryRepository.findByCategoryIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Category with id: "+categoryId+" not found for this user"));

        category.setTitle(title);
        Category updatedCategory = categoryRepository.save(category);
        return new CategoryDTO(updatedCategory.getCategoryId(), updatedCategory.getTitle(), updatedCategory.getColor());
    }

    public CategoryDTO updateCategoryColor(Long userId, Long categoryId, String color) {
        Category category = categoryRepository.findByCategoryIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Category with id: "+categoryId+" not found for this user"));

        category.setColor(color);
        Category updatedCategory = categoryRepository.save(category);
        return new CategoryDTO(updatedCategory.getCategoryId(), updatedCategory.getTitle(), updatedCategory.getColor());
    }

    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByCategoryIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Category with id: "+categoryId+" not found for this user"));

        if (!category.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not own this category");
        }

        categoryRepository.delete(category);
    }
}