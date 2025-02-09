package back.controller;

import back.controller.dto.CashflowRecordDTO;
import back.service.CashflowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("#userId == authentication.principal.userId")
@CrossOrigin(origins = "http://localhost:3000")
public class CashflowRecordController {

    private final CashflowRecordService cashflowRecordService;

    @GetMapping("/user/{userId}/expenses")
    public ResponseEntity<Object> getExpensesByMonth(
            @PathVariable Long userId,
            @RequestParam("monthStartDate") String monthStartDate) {
        try {
            LocalDate startDate = LocalDate.parse(monthStartDate);
            List<CashflowRecordDTO> expenses = cashflowRecordService.getCashflowRecordsByTypeAndMonth(userId, true, startDate);
            return new ResponseEntity<>(expenses, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/incomes")
    public ResponseEntity<Object> getIncomesByMonth(
            @PathVariable Long userId,
            @RequestParam("monthStartDate") String monthStartDate) {
        try {
            LocalDate startDate = LocalDate.parse(monthStartDate);
            List<CashflowRecordDTO> incomes = cashflowRecordService.getCashflowRecordsByTypeAndMonth(userId, false, startDate);
            return new ResponseEntity<>(incomes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/user/{userId}/addIncomes")
    public ResponseEntity<Object> addIncomes(@RequestBody List<CashflowRecordDTO> cashflowRecordDTOs, @PathVariable Long userId) {
        try {
            List<CashflowRecordDTO> savedRecords = cashflowRecordService.addIncomes(cashflowRecordDTOs, userId);
            return new ResponseEntity<>(savedRecords, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/user/{userId}/addExpense")
    public ResponseEntity<Object> addExpenses(@RequestBody List<CashflowRecordDTO> cashflowRecordDTOs, @PathVariable Long userId) {
        try {
            List<CashflowRecordDTO> savedRecords = cashflowRecordService.addExpense(cashflowRecordDTOs, userId);
            return new ResponseEntity<>(savedRecords, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/user/{userId}/editRecords")
    public ResponseEntity<List<CashflowRecordDTO>> updateMultipleCashflowRecords(
            @PathVariable Long userId,
            @RequestBody List<CashflowRecordDTO> cashflowRecordDTOs) {
        List<CashflowRecordDTO> updatedRecords = cashflowRecordService.updateMultipleCashflowRecords(userId, cashflowRecordDTOs);
        return new ResponseEntity<>(updatedRecords, HttpStatus.OK);
    }


    @DeleteMapping("/user/{userId}/records")
    public ResponseEntity<Void> deleteMultipleCashflowRecords(
            @PathVariable Long userId,
            @RequestBody List<Long> recordIds) {
        cashflowRecordService.deleteMultipleCashflowRecords(userId, recordIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
