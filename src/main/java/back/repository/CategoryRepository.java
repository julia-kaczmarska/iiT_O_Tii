package back.repository;

import back.controller.dto.CategoryDTO;
import back.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // GET categories
    @Query("SELECT new back.controller.dto.CategoryDTO(c.categoryId, c.title, c.color) " +
            "FROM Category c " +
            "WHERE c.user.userId = :userId")
    List<CategoryDTO> findByUserId(@Param("userId") Long userId);


    @Query("SELECT new back.controller.dto.CategoryDTO(c.categoryId) " +
            "FROM Category c " +
            "WHERE c.user.userId = :userId")
    Optional<Category> getThisUsersCat(@Param("userId") Long userId); //ADD cashflowRecord

    // PUT category
    // DELETE category
    // PUT cashflow record
    @Query("SELECT c FROM Category c " +
            "WHERE c.categoryId = :categoryId AND c.user.userId = :userId")
    Optional<Category> findByCategoryIdAndUserId(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

}
