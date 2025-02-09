package back.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlannedBudgetDTO {
    @NotNull(message = "plannedBudgetId cannot be null")
    private Long plannedBudgetId;
    @NotNull(message = "categoryId cannot be null")
    private Long categoryId;
    @NotNull(message = "plannedAmount cannot be null")
    private double plannedAmount;
    @NotNull(message = "spentAmount cannot be null")
    private double spentAmount;

    public PlannedBudgetDTO(Long plannedBudgetId, Long categoryId, double plannedAmount, double spentAmount) {
        this.plannedBudgetId = plannedBudgetId;
        this.categoryId = categoryId;
        this.plannedAmount = plannedAmount;
        this.spentAmount = spentAmount;
    }

    public PlannedBudgetDTO() {

    }
}
