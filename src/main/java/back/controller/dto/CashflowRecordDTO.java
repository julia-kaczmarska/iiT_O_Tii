package back.controller.dto;


import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Getter
@Setter
public class CashflowRecordDTO {
    @Positive(message = "Amount must be positive")
    private Float amount;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Record type cannot be null")
    private Boolean recordType;
    @NotNull(message = "Record type cannot be null")
    private String title;

    private Long categoryId;
    private Long userId;

    // Domyślny konstruktor potrzebny dla Jacksona
    public CashflowRecordDTO() {
    }

    // Konstruktor używany przy tworzeniu rekordu
    public CashflowRecordDTO(Float amount, LocalDate date, Boolean recordType, String title, Long categoryId, Long userId) {
        this.amount = amount;
        this.date = date;
        this.recordType = recordType;
        this.title = title;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    //dla zapytań
    public CashflowRecordDTO(Float amount, LocalDate date, Boolean recordType, Long categoryId, Long userId) {
        this.amount = amount;
        this.date = date;
        this.recordType = recordType;
        this.categoryId = categoryId;
        this.userId = userId;
    }
}
