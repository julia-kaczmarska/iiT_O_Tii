package back.service;

import back.controller.dto.MonthBudgetDTO;
import back.controller.dto.PlannedBudgetDTO;
import back.model.MonthBudget;
import back.model.PlannedBudget;
import back.model.User;
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
        monthBudget.setTotalIncome(dto.getTotalIncome());
        monthBudget.setRemainingBalance(dto.getRemainingBalance());

        List<PlannedBudget> plannedBudgets = dto.getPlannedBudgets().stream().map(pbDto -> {
            PlannedBudget plannedBudget = new PlannedBudget();
            plannedBudget.setMonthBudget(monthBudget);
            plannedBudget.setCategory(categoryRepository.findById(pbDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found")));
            plannedBudget.setPlannedAmount(pbDto.getPlannedAmount());
            plannedBudget.setSpentAmount(pbDto.getSpentAmount());
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

        // Aktualizacja pól budżetu miesięcznego
        monthBudget.setTotalIncome(dto.getTotalIncome());
        monthBudget.setRemainingBalance(dto.getRemainingBalance());

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
            plannedBudget.setSpentAmount(pbDto.getSpentAmount());
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
