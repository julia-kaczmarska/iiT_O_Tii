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

    @NotNull(message = "Color cannot be null")
    private String color;


    public CategoryDTO(Category category) {
//        this.categoryId = category.getCategoryId();
        this.title = category.getTitle();
    }

    public CategoryDTO(Long categoryId, String title, String color) {
        this.categoryId = categoryId;
        this.title = title;
        this.color = color;
    }

    //ADD cashflowRecord
    public CategoryDTO(Long categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryDTO(String title) {
        this.title = title;
    }
}