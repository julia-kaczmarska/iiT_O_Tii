package back.service;

import back.controller.dto.CategoryDTO;
import back.model.Category;
import back.model.User;
import back.repository.CategoryRepository;
import back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);
        return new CategoryDTO(savedCategory.getCategoryId(), savedCategory.getTitle());
//        return new CategoryDTO(savedCategory.getTitle());
    }
    public CategoryDTO updateCategory(Long userId, Long categoryId, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findByCategoryIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found for this user"));

        category.setTitle(categoryDTO.getTitle());
        Category updatedCategory = categoryRepository.save(category);
        return new CategoryDTO(updatedCategory.getCategoryId(), updatedCategory.getTitle());
    }

    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByCategoryIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found for this user"));
        categoryRepository.delete(category);
    }
}