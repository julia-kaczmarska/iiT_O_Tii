package back.controller.dto;

import back.model.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    @NotNull(message = "CategoryID cannot be null")
    private Long categoryId;

    @NotNull(message = "Title cannot be null")
    private String title;


    public CategoryDTO(Category category) {
//        this.categoryId = category.getCategoryId();
        this.title = category.getTitle();
    }

    public CategoryDTO(Long categoryId, String title) {
        this.categoryId = categoryId;
        this.title = title;
    }

    public CategoryDTO(String title) {
        this.title = title;
    }
}