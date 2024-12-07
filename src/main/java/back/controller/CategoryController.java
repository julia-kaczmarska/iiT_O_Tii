package back.controller;

import back.controller.dto.CategoryDTO;
import back.security.UserPrincipal;
import back.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@PreAuthorize("#userId == authentication.principal.userId")
@CrossOrigin(origins = "http://localhost:3000") // Pozwala na żądania z frontu
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/user/{userId}/categories")
    public ResponseEntity<Object> getCategoriesByUserId(@PathVariable Long userId) {
        try {
            List<CategoryDTO> categories = categoryService.getCategoriesByUserId(userId);
            return ResponseEntity.ok(categories);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/user/{userId}/addcategory")
    public ResponseEntity<Object> addCategory(@RequestBody CategoryDTO categoryDto, @PathVariable Long userId) {
        try {
            CategoryDTO savedCategory = categoryService.addCategory(categoryDto, userId);
            return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/user/{userId}/category/{categoryId}/color")
    public ResponseEntity<CategoryDTO> updateCategoryColor(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestBody Map<String, String> requestBody) {
        String color = requestBody.get("color");
        CategoryDTO updatedCategory = categoryService.updateCategoryColor(userId, categoryId, color);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @PutMapping("/user/{userId}/category/{categoryId}/title")
    public ResponseEntity<CategoryDTO> updateCategoryTitle(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestBody Map<String, String> requestBody) {
        String title = requestBody.get("title");
        CategoryDTO updatedCategory = categoryService.updateCategoryTitle(userId, categoryId, title);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/user/{userId}/category/{categoryId}")
    public String deleteCategory(@PathVariable Long userId, @PathVariable Long categoryId) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to delete this category");
        }
        categoryService.deleteCategory(userId, categoryId);
        return String.valueOf(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
