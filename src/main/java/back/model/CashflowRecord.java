package back.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//    @OneToMany(mappedBy = "cashflowRecord", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Sharing> sharings = new ArrayList<>();
}

