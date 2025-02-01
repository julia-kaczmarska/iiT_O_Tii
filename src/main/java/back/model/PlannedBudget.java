package back.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="planned_budget")
@Getter
@Setter
public class PlannedBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plannedBudgetId;

    @Column(name = "planned_amount", nullable = false)
    private double plannedAmount;

    @Column(name = "spent_amount", nullable = false)
    private double spentAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "month_budget_id", nullable = false)
    private MonthBudget monthBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
