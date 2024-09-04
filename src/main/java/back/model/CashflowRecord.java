package back.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cashflow_record")
@Getter
@Setter
public class CashflowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cashflow_record_id")
    private Long cashflowRecordId;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "record_type", nullable = false)
    private boolean recordType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "cashflowRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sharing> sharings = new ArrayList<>();
}

