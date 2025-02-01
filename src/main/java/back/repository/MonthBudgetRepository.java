package back.repository;

import back.model.MonthBudget;
import back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MonthBudgetRepository extends JpaRepository<MonthBudget, Long> {
    @Query("SELECT mb FROM MonthBudget mb WHERE mb.user = :user AND mb.firstOfMonth = :date")
    Optional<MonthBudget> findByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
}


