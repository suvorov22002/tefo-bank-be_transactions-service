package com.tefo.bank.transactionsservice.model;

import com.tefo.library.customdata.SQLFieldValue;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Table(name = "transaction_field_value")
@Getter
@Setter
public class TransactionFieldValue implements SQLFieldValue<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String type;
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> value;
}