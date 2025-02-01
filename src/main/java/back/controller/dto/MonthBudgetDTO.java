package back.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MonthBudgetDTO {
    @NotNull(message = "monthBudgetId cannot be null")
    private Long monthBudgetId;
    @NotNull(message = "firstOfMonth cannot be null")
    private LocalDate firstOfMonth;
    @NotNull(message = "totalIncome cannot be null")
    private double totalIncome;
    @NotNull(message = "remainingBalance cannot be null")
    private double remainingBalance;
    @NotNull(message = "plannedBudgets cannot be null")
    private List<PlannedBudgetDTO> plannedBudgets;

}
