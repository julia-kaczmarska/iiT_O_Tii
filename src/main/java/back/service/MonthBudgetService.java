package back.service;

import back.controller.dto.MonthBudgetDTO;
import back.controller.dto.PlannedBudgetDTO;
import back.model.MonthBudget;
import back.model.PlannedBudget;
import back.model.User;
import back.model.CashflowRecord;
import back.repository.CashflowRecordRepository;
import back.repository.CategoryRepository;
import back.repository.MonthBudgetRepository;
import back.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthBudgetService {
    private final MonthBudgetRepository monthBudgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CashflowRecordRepository cashflowRecordRepository;

    public MonthBudgetDTO getBudgetByDate(Long userId, LocalDate date) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MonthBudget monthBudget = monthBudgetRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("No budget found for the given date"));

        return mapToDTO(monthBudget);
    }

    public MonthBudgetDTO createMonthBudget(MonthBudgetDTO dto, Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MonthBudget monthBudget = new MonthBudget();
        monthBudget.setUser(user);
        monthBudget.setFirstOfMonth(dto.getFirstOfMonth());

        // Obliczenie sumy przychodów dla tego miesiąca
        double totalIncome = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
                        userId,
                        false, // recordType dla przychodów
                        dto.getFirstOfMonth().getMonthValue(),
                        dto.getFirstOfMonth().getYear()
                ).stream()
                .mapToDouble(CashflowRecord::getAmount)
                .sum();

        monthBudget.setTotalIncome(totalIncome);

        // Pozostały budżet = dochody - wszystkie wydatki
        double spentAmountTotal = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
                        userId,
                        true, // recordType dla wydatków
                        dto.getFirstOfMonth().getMonthValue(),
                        dto.getFirstOfMonth().getYear()
                ).stream()
                .mapToDouble(CashflowRecord::getAmount)
                .sum();

        monthBudget.setRemainingBalance(totalIncome - spentAmountTotal);

        List<PlannedBudget> plannedBudgets = dto.getPlannedBudgets().stream().map(pbDto -> {
            PlannedBudget plannedBudget = new PlannedBudget();
            plannedBudget.setMonthBudget(monthBudget);
            plannedBudget.setCategory(categoryRepository.findById(pbDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found")));
            plannedBudget.setPlannedAmount(pbDto.getPlannedAmount());

            // Obliczenie sumy wydatków (spentAmount) dla kategorii
            double spentAmount = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndCategoryAndMonthAndYear(
                            userId,
                            true, // recordType dla wydatków
                            pbDto.getCategoryId(),
                            dto.getFirstOfMonth().getMonthValue(),
                            dto.getFirstOfMonth().getYear()
                    ).stream()
                    .mapToDouble(CashflowRecord::getAmount)
                    .sum();

            plannedBudget.setSpentAmount(spentAmount);
            return plannedBudget;
        }).collect(Collectors.toList());

        monthBudget.setPlannedBudgets(plannedBudgets);

        MonthBudget savedBudget = monthBudgetRepository.save(monthBudget);
        return mapToDTO(savedBudget);
    }



    @Transactional
    public MonthBudgetDTO editBudgetByDate(Long userId, LocalDate date, MonthBudgetDTO dto) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MonthBudget monthBudget = monthBudgetRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("No budget found for the given date"));

        // Przeliczenie totalIncome na podstawie rekordów typu 0
        double totalIncome = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
                        userId,
                        false, // recordType dla przychodów
                        date.getMonthValue(),
                        date.getYear()
                ).stream()
                .mapToDouble(CashflowRecord::getAmount)
                .sum();

        monthBudget.setTotalIncome(totalIncome);

        // Przeliczenie remainingBalance na podstawie dochodów i wydatków
        double spentAmountTotal = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
                        userId,
                        true, // recordType dla wydatków
                        date.getMonthValue(),
                        date.getYear()
                ).stream()
                .mapToDouble(CashflowRecord::getAmount)
                .sum();

        monthBudget.setRemainingBalance(totalIncome - spentAmountTotal);

        // Aktualizacja lub dodanie planów budżetowych dla kategorii
        List<PlannedBudget> updatedPlannedBudgets = dto.getPlannedBudgets().stream().map(pbDto -> {
            PlannedBudget plannedBudget = monthBudget.getPlannedBudgets().stream()
                    .filter(pb -> pb.getPlannedBudgetId().equals(pbDto.getPlannedBudgetId()))
                    .findFirst()
                    .orElse(new PlannedBudget());

            plannedBudget.setMonthBudget(monthBudget);
            plannedBudget.setCategory(categoryRepository.findById(pbDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found")));
            plannedBudget.setPlannedAmount(pbDto.getPlannedAmount());

            // Przeliczenie wydatków (spentAmount) dla danej kategorii
            double spentAmount = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndCategoryAndMonthAndYear(
                            userId,
                            true, // recordType dla wydatków
                            pbDto.getCategoryId(),
                            date.getMonthValue(),
                            date.getYear()
                    ).stream()
                    .mapToDouble(CashflowRecord::getAmount)
                    .sum();

            plannedBudget.setSpentAmount(spentAmount);
            return plannedBudget;
        }).collect(Collectors.toList());

        // Ustawienie zaktualizowanej listy
        monthBudget.getPlannedBudgets().clear();
        monthBudget.getPlannedBudgets().addAll(updatedPlannedBudgets);

        // Zapisanie zmian
        MonthBudget savedBudget = monthBudgetRepository.save(monthBudget);
        return mapToDTO(savedBudget);
    }

    private MonthBudgetDTO mapToDTO(MonthBudget monthBudget) {
        MonthBudgetDTO dto = new MonthBudgetDTO();
        dto.setMonthBudgetId(monthBudget.getMonthBudgetId());
        dto.setFirstOfMonth(monthBudget.getFirstOfMonth());
        dto.setTotalIncome(monthBudget.getTotalIncome());
        dto.setRemainingBalance(monthBudget.getRemainingBalance());
        dto.setPlannedBudgets(monthBudget.getPlannedBudgets().stream().map(pb -> {
            PlannedBudgetDTO pbDto = new PlannedBudgetDTO();
            pbDto.setPlannedBudgetId(pb.getPlannedBudgetId());
            pbDto.setCategoryId(pb.getCategory().getCategoryId());
            pbDto.setPlannedAmount(pb.getPlannedAmount());
            pbDto.setSpentAmount(pb.getSpentAmount());
            return pbDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
