package back.service;

import back.model.Category;
import back.model.MonthBudget;
import back.model.PlannedBudget;
import back.repository.PlannedBudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlannedBudgetService {

    private final PlannedBudgetRepository plannedBudgetRepository;

    /**
     * Dodaje nowy PlannedBudget lub aktualizuje istniejący.
     * Jeśli budżet dla danej kategorii już istnieje, aktualizuje SpentAmount.
     * Jeśli nie istnieje, tworzy nowy wpis w bazie danych.
     */
    public PlannedBudget addOrUpdatePlannedBudget(MonthBudget monthBudget, Category category, double amount) {
        PlannedBudget plannedBudget = plannedBudgetRepository
                .findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
                        monthBudget.getMonthBudgetId(),
                        category.getCategoryId()
                )
                .orElseGet(() -> {
                    PlannedBudget newPlannedBudget = new PlannedBudget();
                    newPlannedBudget.setMonthBudget(monthBudget);
                    newPlannedBudget.setCategory(category);
                    newPlannedBudget.setPlannedAmount(0.0);
                    newPlannedBudget.setSpentAmount(0.0);
                    return plannedBudgetRepository.save(newPlannedBudget);
                });

        plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() + amount);
        return plannedBudgetRepository.save(plannedBudget);
    }

    //Aktualizuje istniejący PlannedBudget o podaną różnicę w wydatkach.
    public PlannedBudget updateSpentAmount(PlannedBudget plannedBudget, double difference) {
        plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() + difference);
        return plannedBudgetRepository.save(plannedBudget);
    }
}
