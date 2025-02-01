package back.controller;

import back.controller.dto.MonthBudgetDTO;
import back.service.MonthBudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@PreAuthorize("#userId == authentication.principal.userId")
@CrossOrigin(origins = "http://localhost:3000")
public class MonthBudgetController {
    private final MonthBudgetService monthBudgetService;

    @GetMapping("/user/{userId}/getBudget/{date}")
    public ResponseEntity<MonthBudgetDTO> getBudget(@PathVariable Long userId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MonthBudgetDTO budget = monthBudgetService.getBudgetByDate(userId, date);
        return ResponseEntity.ok(budget);
    }

    @PostMapping("/user/{userId}/createBudget")
    public ResponseEntity<MonthBudgetDTO> createMonthBudget(@PathVariable Long userId, @RequestBody MonthBudgetDTO dto) {
        MonthBudgetDTO createdBudget = monthBudgetService.createMonthBudget(dto, userId);
        return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
    }

    @PutMapping("/user/{userId}/editBudget/{date}")
    public ResponseEntity<MonthBudgetDTO> editBudget(@PathVariable Long userId,
                                                     @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                     @RequestBody MonthBudgetDTO dto) {
        MonthBudgetDTO updatedBudget = monthBudgetService.editBudgetByDate(userId, date, dto);
        return ResponseEntity.ok(updatedBudget);
    }
}
