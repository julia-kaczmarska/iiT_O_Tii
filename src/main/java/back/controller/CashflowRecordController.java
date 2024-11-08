package back.controller;

import back.controller.dto.CashflowRecordDTO;
import back.service.CashflowRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CashflowRecordController {

    private final CashflowRecordService cashflowRecordService;

    @GetMapping("/user/{userId}/records")
    public ResponseEntity<Object> getRecordsByUserId(@PathVariable Long userId) {
        try {
            List<CashflowRecordDTO> cashflowRecords = cashflowRecordService.getCashflowRecordsByUserId(userId);
            return new ResponseEntity<>(cashflowRecords, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/user/{userId}/addrecords")
    public ResponseEntity<Object> addCashflowRecord(@Valid @RequestBody CashflowRecordDTO cashflowRecordDTO, @PathVariable Long userId) {
        System.out.println(userId);
        try{
            CashflowRecordDTO savedCashflowRecord = cashflowRecordService.addCashflowRecord(cashflowRecordDTO, userId);


            return new ResponseEntity<>(savedCashflowRecord, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
//    @PutMapping("/user/{userId}/records/{recordId}")
//    public ResponseEntity<CashflowRecordDTO> updateCashflowRecord(
//            @PathVariable Long recordId,
//            @RequestBody CashflowRecordDTO cashflowRecordDTO) {
//        CashflowRecordDTO updatedRecord = cashflowRecordService.updateCashflowRecord(recordId, cashflowRecordDTO);
//        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
//    }
//
////    @DeleteMapping("/user/{userId}/records/{recordId}")
////    public ResponseEntity<Void> deleteCashflowRecord(
////            @PathVariable Long userId,
////            @PathVariable Long recordId) {
////        cashflowRecordService.deleteCashflowRecord(userId, recordId);
////        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
////    }
}
