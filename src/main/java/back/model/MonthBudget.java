package back.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name="month_budget")
@Getter
@Setter
public class MonthBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="month_budget_id")
    private Long monthBudgetId;

    @Column(name="first_of_month", nullable = false)
    private LocalDate firstOfMonth;

    @Column(name = "total_income", nullable = false)
    private double totalIncome;

    @Column(name = "remaining_balance", nullable = false)
    private double remainingBalance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "monthBudget", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PlannedBudget> plannedBudgets;

}
