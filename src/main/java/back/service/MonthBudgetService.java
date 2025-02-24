//package back.service;
//
//import back.controller.dto.MonthBudgetDTO;
//import back.controller.dto.PlannedBudgetDTO;
//import back.model.CashflowRecord;
//import back.model.MonthBudget;
//import back.model.PlannedBudget;
//import back.model.User;
//import back.repository.CashflowRecordRepository;
//import back.repository.CategoryRepository;
//import back.repository.MonthBudgetRepository;
//import back.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class MonthBudgetService {
//    private final MonthBudgetRepository monthBudgetRepository;
//    private final UserRepository userRepository;
//    private final CategoryRepository categoryRepository;
//    private final CashflowRecordRepository cashflowRecordRepository;
//
//    public MonthBudgetDTO getBudgetByDate(Long userId, LocalDate date) {
//        User user = userRepository.findById(Math.toIntExact(userId))
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        MonthBudget monthBudget = monthBudgetRepository.findByUserAndDate(user, date)
//                .orElseThrow(() -> new IllegalArgumentException("No budget found for the given date"));
//
//        return mapToDTO(monthBudget);
//    }
//
//    public MonthBudgetDTO createMonthBudget(MonthBudgetDTO dto, Long userId) {
//        System.out.println("[DEBUG] Wywołano createMonthBudget dla miesiąca: " + dto.getFirstOfMonth());
//        User user = userRepository.findById(Math.toIntExact(userId))
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        MonthBudget monthBudget = new MonthBudget();
//        monthBudget.setUser(user);
//        monthBudget.setFirstOfMonth(dto.getFirstOfMonth());
//
//        // Obliczenie sumy przychodów dla tego miesiąca
//        double totalIncome = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
//                        userId,
//                        false, // recordType dla przychodów
//                        dto.getFirstOfMonth().getMonthValue(),
//                        dto.getFirstOfMonth().getYear()
//                ).stream()
//                .mapToDouble(CashflowRecord::getAmount)
//                .sum();
//
//        monthBudget.setTotalIncome(totalIncome);
//
//        // Pozostały budżet = dochody - wszystkie wydatki
//        double spentAmountTotal = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
//                        userId,
//                        true, // recordType dla wydatków
//                        dto.getFirstOfMonth().getMonthValue(),
//                        dto.getFirstOfMonth().getYear()
//                ).stream()
//                .mapToDouble(CashflowRecord::getAmount)
//                .sum();
//
//        monthBudget.setRemainingBalance(totalIncome - spentAmountTotal);
//
//        List<PlannedBudget> plannedBudgets = dto.getPlannedBudgets().stream().map(pbDto -> {
//            PlannedBudget plannedBudget = new PlannedBudget();
//            plannedBudget.setMonthBudget(monthBudget);
//            plannedBudget.setCategory(categoryRepository.findById(pbDto.getCategoryId())
//                    .orElseThrow(() -> new IllegalArgumentException("Category not found")));
//            plannedBudget.setPlannedAmount(pbDto.getPlannedAmount());
//
//            // Obliczenie sumy wydatków (spentAmount) dla kategorii
//            double spentAmount = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndCategoryAndMonthAndYear(
//                            userId,
//                            true, // recordType dla wydatków
//                            pbDto.getCategoryId(),
//                            dto.getFirstOfMonth().getMonthValue(),
//                            dto.getFirstOfMonth().getYear()
//                    ).stream()
//                    .mapToDouble(CashflowRecord::getAmount)
//                    .sum();
//
//            plannedBudget.setSpentAmount(spentAmount);
//            return plannedBudget;
//        }).collect(Collectors.toList());
//
//        monthBudget.setPlannedBudgets(plannedBudgets);
//
//        // Sprawdzenie, czy istnieje budżet na poprzedni miesiąc
//        LocalDate previousMonthDate = dto.getFirstOfMonth().minusMonths(1).withDayOfMonth(1);
//        System.out.println("[DEBUG] Poprzedni miesiac: " + previousMonthDate);
//
//        MonthBudget previousMonthBudget = monthBudgetRepository.findByUserIdAndMonth(
//                userId,
//                previousMonthDate.getMonthValue(),
//                previousMonthDate.getYear()
//        ).orElse(null);
//
//        System.out.println("[DEBUG] Poprzedni budget: " + previousMonthBudget);
//
//
//        if (previousMonthBudget != null) {
//            // Dodanie remainingBalance z poprzedniego miesiąca do totalIncome
//            double previousRemainingBalance = previousMonthBudget.getRemainingBalance();
//            monthBudget.setTotalIncome(monthBudget.getTotalIncome() + previousRemainingBalance);
//
//            // DEBUG: Sprawdzenie czy dodaje się poprawnie
//            System.out.println("[DEBUG] Dodano remainingBalance z poprzedniego miesiąca: " + previousRemainingBalance);
//            System.out.println("[DEBUG] Total Income po dodaniu: " + monthBudget.getTotalIncome());
//        }
//
//        MonthBudget savedBudget = monthBudgetRepository.save(monthBudget);
//        return mapToDTO(savedBudget);
//    }
//
//    @Transactional
//    public MonthBudgetDTO editBudgetByDate(Long userId, LocalDate date, MonthBudgetDTO dto) {
//        User user = userRepository.findById(Math.toIntExact(userId))
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Sprawdzenie lub utworzenie MonthBudget
//        MonthBudget monthBudget = monthBudgetRepository.findByUserAndDate(user, date)
//                .orElseGet(() -> {
//                    MonthBudget newMonthBudget = new MonthBudget();
//                    newMonthBudget.setUser(user);
//                    newMonthBudget.setFirstOfMonth(date.withDayOfMonth(1));
//                    newMonthBudget.setTotalIncome(0.0);
//                    newMonthBudget.setRemainingBalance(0.0);
//                    newMonthBudget.setPlannedBudgets(new ArrayList<>()); // Inicjalizacja pustą listą
//                    return monthBudgetRepository.save(newMonthBudget);
//                });
//
//        // Przeliczenie totalIncome na podstawie rekordów typu 0
//        double totalIncome = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
//                        userId,
//                        false, // recordType dla przychodów
//                        date.getMonthValue(),
//                        date.getYear()
//                ).stream()
//                .mapToDouble(CashflowRecord::getAmount)
//                .sum();
//
//        // Dodanie remainingBalance z poprzedniego miesiąca
//        LocalDate previousMonthDate = date.minusMonths(1).withDayOfMonth(1);
//        MonthBudget previousMonthBudget = monthBudgetRepository.findByUserIdAndMonth(
//                userId,
//                previousMonthDate.getMonthValue(),
//                previousMonthDate.getYear()
//        ).orElse(null);
//
//        if (previousMonthBudget != null) {
//            double previousRemainingBalance = previousMonthBudget.getRemainingBalance();
//            totalIncome += previousRemainingBalance;
//            System.out.println("[DEBUG] Dodano remainingBalance z poprzedniego miesiąca: " + previousRemainingBalance);
//        } else {
//            System.out.println("[DEBUG] Brak poprzedniego MonthBudget. Nie dodano remainingBalance.");
//        }
//
//        monthBudget.setTotalIncome(totalIncome);
//
//        // Przeliczenie remainingBalance na podstawie dochodów i wydatków
//        double spentAmountTotal = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
//                        userId,
//                        true, // recordType dla wydatków
//                        date.getMonthValue(),
//                        date.getYear()
//                ).stream()
//                .mapToDouble(CashflowRecord::getAmount)
//                .sum();
//
//        monthBudget.setRemainingBalance(totalIncome - spentAmountTotal);
//
//        // Sprawdzenie null i aktualizacja lub dodanie planów budżetowych dla kategorii
//        List<PlannedBudget> updatedPlannedBudgets = Optional.ofNullable(dto.getPlannedBudgets())
//                .orElse(Collections.emptyList()) // Jeśli null, ustaw pustą listę
//                .stream()
//                .map(pbDto -> {
//                    // Użycie Optional dla znalezienia istniejącego PlannedBudget
//                    PlannedBudget plannedBudget = monthBudget.getPlannedBudgets() != null
//                            ? monthBudget.getPlannedBudgets().stream()
//                            .filter(pb -> Optional.ofNullable(pbDto.getPlannedBudgetId())
//                                    .map(id -> id.equals(pb.getPlannedBudgetId()))
//                                    .orElse(false))
//                            .findFirst()
//                            .orElse(new PlannedBudget())
//                            : new PlannedBudget();
//
//                    plannedBudget.setMonthBudget(monthBudget);
//
//                    // Sprawdzenie null dla CategoryId
//                    Long categoryId = Optional.ofNullable(pbDto.getCategoryId())
//                            .orElseThrow(() -> new IllegalArgumentException("Category ID cannot be null"));
//
//                    plannedBudget.setCategory(categoryRepository.findById(categoryId)
//                            .orElseThrow(() -> new IllegalArgumentException("Category not found")));
//
//                    // Sprawdzenie null dla PlannedAmount
//                    Double plannedAmount = Optional.ofNullable(pbDto.getPlannedAmount())
//                            .orElse(0.0);
//                    plannedBudget.setPlannedAmount(plannedAmount);
//
//                    // Przeliczenie wydatków (spentAmount) dla danej kategorii
//                    double spentAmount = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndCategoryAndMonthAndYear(
//                                    userId,
//                                    true, // recordType dla wydatków
//                                    categoryId,
//                                    date.getMonthValue(),
//                                    date.getYear()
//                            ).stream()
//                            .mapToDouble(CashflowRecord::getAmount)
//                            .sum();
//
//                    plannedBudget.setSpentAmount(spentAmount);
//                    return plannedBudget;
//                }).collect(Collectors.toList());
//
//
//        // Ustawienie zaktualizowanej listy
//        monthBudget.getPlannedBudgets().clear();
//        monthBudget.getPlannedBudgets().addAll(updatedPlannedBudgets);
//
//        // Zapisanie zmian
//        MonthBudget savedBudget = monthBudgetRepository.save(monthBudget);
//        return mapToDTO(savedBudget);
//    }
//
//    public MonthBudgetDTO mapToDTO(MonthBudget monthBudget) {
//        MonthBudgetDTO dto = new MonthBudgetDTO();
//        dto.setMonthBudgetId(monthBudget.getMonthBudgetId());
//        dto.setFirstOfMonth(monthBudget.getFirstOfMonth());
//        dto.setTotalIncome(monthBudget.getTotalIncome());
//        dto.setRemainingBalance(monthBudget.getRemainingBalance());
//        dto.setPlannedBudgets(monthBudget.getPlannedBudgets().stream().map(pb -> {
//            PlannedBudgetDTO pbDto = new PlannedBudgetDTO();
//            pbDto.setPlannedBudgetId(pb.getPlannedBudgetId());
//            pbDto.setCategoryId(pb.getCategory().getCategoryId());
//            pbDto.setPlannedAmount(pb.getPlannedAmount());
//            pbDto.setSpentAmount(pb.getSpentAmount());
//            return pbDto;
//        }).collect(Collectors.toList()));
//        return dto;
//    }
//
//
//    @Transactional
//    public void updateNextMonthTotalIncome(Long userId, LocalDate monthStartDate, double delta) {
//        // Pobranie budżetu następnego miesiąca
//        LocalDate nextMonthStart = monthStartDate.plusMonths(1).withDayOfMonth(1);
//        MonthBudget nextMonthBudget = monthBudgetRepository.findByUserIdAndMonth(
//                userId,
//                nextMonthStart.getMonthValue(),
//                nextMonthStart.getYear()
//        ).orElse(null);
//
//        // Jeśli nie istnieje budżet na następny miesiąc, kończymy rekurencję
//        if (nextMonthBudget == null) {
//            return;
//        }
//
//// Dodanie delta tylko raz w n+1
//        nextMonthBudget.setTotalIncome(nextMonthBudget.getTotalIncome() + delta);
//        nextMonthBudget.setRemainingBalance(nextMonthBudget.getRemainingBalance() + delta);
//        monthBudgetRepository.save(nextMonthBudget);
//
//
//        // Rekurencyjne wywołanie dla kolejnego miesiąca
//        updateNextMonthTotalIncome(userId, nextMonthStart, delta);
//    }
//}


package back.service;

import back.controller.dto.MonthBudgetDTO;
import back.controller.dto.PlannedBudgetDTO;
import back.model.CashflowRecord;
import back.model.MonthBudget;
import back.model.PlannedBudget;
import back.model.User;
import back.repository.CashflowRecordRepository;
import back.repository.CategoryRepository;
import back.repository.MonthBudgetRepository;
import back.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthBudgetService {
    private final MonthBudgetRepository monthBudgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CashflowRecordRepository cashflowRecordRepository;
    private final PlannedBudgetService plannedBudgetService;

    public MonthBudgetDTO getBudgetByDate(Long userId, LocalDate date) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MonthBudget monthBudget = monthBudgetRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("No budget found for the given date"));

        return mapToDTO(monthBudget);
    }

//    public MonthBudgetDTO createMonthBudget(MonthBudgetDTO dto, Long userId) {
//        System.out.println("[DEBUG] Wywołano createMonthBudget dla miesiąca: " + dto.getFirstOfMonth());
//        User user = userRepository.findById(Math.toIntExact(userId))
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        MonthBudget monthBudget = new MonthBudget();
//        monthBudget.setUser(user);
//        monthBudget.setFirstOfMonth(dto.getFirstOfMonth());
//
//        // Obliczenie sumy przychodów dla tego miesiąca
//        double totalIncome = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
//                        userId,
//                        false, // recordType dla przychodów
//                        dto.getFirstOfMonth().getMonthValue(),
//                        dto.getFirstOfMonth().getYear()
//                ).stream()
//                .mapToDouble(CashflowRecord::getAmount)
//                .sum();
//
//        monthBudget.setTotalIncome(totalIncome);
//
//        // Pozostały budżet = dochody - wszystkie wydatki
//        double spentAmountTotal = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
//                        userId,
//                        true, // recordType dla wydatków
//                        dto.getFirstOfMonth().getMonthValue(),
//                        dto.getFirstOfMonth().getYear()
//                ).stream()
//                .mapToDouble(CashflowRecord::getAmount)
//                .sum();
//
//        monthBudget.setRemainingBalance(totalIncome - spentAmountTotal);
//
//        List<PlannedBudget> plannedBudgets = dto.getPlannedBudgets().stream().map(pbDto -> {
//            PlannedBudget plannedBudget = new PlannedBudget();
//            plannedBudget.setMonthBudget(monthBudget);
//            plannedBudget.setCategory(categoryRepository.findById(pbDto.getCategoryId())
//                    .orElseThrow(() -> new IllegalArgumentException("Category not found")));
//            plannedBudget.setPlannedAmount(pbDto.getPlannedAmount());
//
//            // Obliczenie sumy wydatków (spentAmount) dla kategorii
//            double spentAmount = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndCategoryAndMonthAndYear(
//                            userId,
//                            true, // recordType dla wydatków
//                            pbDto.getCategoryId(),
//                            dto.getFirstOfMonth().getMonthValue(),
//                            dto.getFirstOfMonth().getYear()
//                    ).stream()
//                    .mapToDouble(CashflowRecord::getAmount)
//                    .sum();
//
//            plannedBudget.setSpentAmount(spentAmount);
//            return plannedBudget;
//        }).collect(Collectors.toList());
//
//        monthBudget.setPlannedBudgets(plannedBudgets);
//
//        // Sprawdzenie, czy istnieje budżet na poprzedni miesiąc
//        LocalDate previousMonthDate = dto.getFirstOfMonth().minusMonths(1).withDayOfMonth(1);
//        System.out.println("[DEBUG] Poprzedni miesiac: " + previousMonthDate);
//
//        MonthBudget previousMonthBudget = monthBudgetRepository.findByUserIdAndMonth(
//                userId,
//                previousMonthDate.getMonthValue(),
//                previousMonthDate.getYear()
//        ).orElse(null);
//
//        System.out.println("[DEBUG] Poprzedni budget: " + previousMonthBudget);
//
//
//        if (previousMonthBudget != null) {
//            // Dodanie remainingBalance z poprzedniego miesiąca do totalIncome
//            double previousRemainingBalance = previousMonthBudget.getRemainingBalance();
//            monthBudget.setTotalIncome(monthBudget.getTotalIncome() + previousRemainingBalance);
//
//            // DEBUG: Sprawdzenie czy dodaje się poprawnie
//            System.out.println("[DEBUG] Dodano remainingBalance z poprzedniego miesiąca: " + previousRemainingBalance);
//            System.out.println("[DEBUG] Total Income po dodaniu: " + monthBudget.getTotalIncome());
//        }
//
//        MonthBudget savedBudget = monthBudgetRepository.save(monthBudget);
//        return mapToDTO(savedBudget);
//    }

    @Transactional
    public MonthBudgetDTO editBudgetByDate(Long userId, LocalDate date, MonthBudgetDTO dto) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Sprawdzenie lub utworzenie MonthBudget
        MonthBudget monthBudget = monthBudgetRepository.findByUserAndDate(user, date)
                .orElseGet(() -> {
                    MonthBudget newMonthBudget = new MonthBudget();
                    newMonthBudget.setUser(user);
                    newMonthBudget.setFirstOfMonth(date.withDayOfMonth(1));
                    newMonthBudget.setTotalIncome(0.0);
                    newMonthBudget.setRemainingBalance(0.0);
                    newMonthBudget.setPlannedBudgets(new ArrayList<>()); // Inicjalizacja pustą listą
                    return monthBudgetRepository.save(newMonthBudget);
                });

        // Przeliczenie totalIncome na podstawie rekordów typu 0
        double totalIncome = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndMonthAndYear(
                        userId,
                        false, // recordType dla przychodów
                        date.getMonthValue(),
                        date.getYear()
                ).stream()
                .mapToDouble(CashflowRecord::getAmount)
                .sum();

        // Dodanie remainingBalance z poprzedniego miesiąca
        LocalDate previousMonthDate = date.minusMonths(1).withDayOfMonth(1);
        MonthBudget previousMonthBudget = monthBudgetRepository.findByUserIdAndMonth(
                userId,
                previousMonthDate.getMonthValue(),
                previousMonthDate.getYear()
        ).orElse(null);

        if (previousMonthBudget != null) {
            double previousRemainingBalance = previousMonthBudget.getRemainingBalance();
            totalIncome += previousRemainingBalance;
            System.out.println("[DEBUG] Dodano remainingBalance z poprzedniego miesiąca: " + previousRemainingBalance);
        } else {
            System.out.println("[DEBUG] Brak poprzedniego MonthBudget. Nie dodano remainingBalance.");
        }

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

        // Sprawdzenie null i aktualizacja lub dodanie planów budżetowych dla kategorii
        List<PlannedBudget> updatedPlannedBudgets = Optional.ofNullable(dto.getPlannedBudgets())
                .orElse(Collections.emptyList()) // Jeśli null, ustaw pustą listę
                .stream()
                .map(pbDto -> {
                    // Użycie Optional dla znalezienia istniejącego PlannedBudget
                    PlannedBudget plannedBudget = monthBudget.getPlannedBudgets() != null
                            ? monthBudget.getPlannedBudgets().stream()
                            .filter(pb -> Optional.ofNullable(pbDto.getPlannedBudgetId())
                                    .map(id -> id.equals(pb.getPlannedBudgetId()))
                                    .orElse(false))
                            .findFirst()
                            .orElse(new PlannedBudget())
                            : new PlannedBudget();

                    plannedBudget.setMonthBudget(monthBudget);

                    // Sprawdzenie null dla CategoryId
                    Long categoryId = Optional.ofNullable(pbDto.getCategoryId())
                            .orElseThrow(() -> new IllegalArgumentException("Category ID cannot be null"));

                    plannedBudget.setCategory(categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new IllegalArgumentException("Category not found")));

                    // Sprawdzenie null dla PlannedAmount
                    Double plannedAmount = Optional.ofNullable(pbDto.getPlannedAmount())
                            .orElse(0.0);
                    plannedBudget.setPlannedAmount(plannedAmount);

                    // Przeliczenie wydatków (spentAmount) dla danej kategorii
                    double spentAmount = cashflowRecordRepository.findAllByUserIdAndRecordTypeAndCategoryAndMonthAndYear(
                                    userId,
                                    true, // recordType dla wydatków
                                    categoryId,
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

    public MonthBudgetDTO getBudgetByDate(Integer userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MonthBudget monthBudget = monthBudgetRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("No budget found for the given date"));

        return mapToDTO(monthBudget);
    }

    @Transactional
    public void updateBudgetBalance(MonthBudget monthBudget, double delta) {
        if (monthBudget == null) {
            throw new IllegalStateException("MonthBudget cannot be null when updating balance.");
        }

        monthBudget.setTotalIncome(monthBudget.getTotalIncome() + delta);
        monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() + delta);

        monthBudgetRepository.save(monthBudget);

        updateNextMonthTotalIncome(monthBudget.getUser().getUserId(), monthBudget.getFirstOfMonth(), delta);
    }

    @Transactional
    public void updateNextMonthTotalIncome(Long userId, LocalDate monthStartDate, double delta) {
        LocalDate nextMonthStart = monthStartDate.plusMonths(1).withDayOfMonth(1);
        MonthBudget nextMonthBudget = monthBudgetRepository.findByUserIdAndMonth(
                userId,
                nextMonthStart.getMonthValue(),
                nextMonthStart.getYear()
        ).orElse(null);

        // Jeśli nie istnieje budżet na następny miesiąc, kończymy rekurencję
        if (nextMonthBudget == null) {
            return;
        }

        // Dodanie delta do TotalIncome i RemainingBalance w n+1
        nextMonthBudget.setTotalIncome(nextMonthBudget.getTotalIncome() + delta);
        nextMonthBudget.setRemainingBalance(nextMonthBudget.getRemainingBalance() + delta);
        monthBudgetRepository.save(nextMonthBudget);

        // DEBUG: Informacje o aktualizacji
        System.out.println("[DEBUG] Zaktualizowano totalIncome w miesiącu n+1 o delta: " + delta);

        // Rekurencyjne wywołanie dla kolejnego miesiąca
        updateNextMonthTotalIncome(userId, nextMonthStart, delta);
    }


    public MonthBudgetDTO mapToDTO(MonthBudget monthBudget) {
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