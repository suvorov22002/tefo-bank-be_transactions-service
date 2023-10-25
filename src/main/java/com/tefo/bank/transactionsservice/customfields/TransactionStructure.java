package com.tefo.bank.transactionsservice.customfields;

import com.tefo.bank.transactionsservice.model.TransactionEntity;
import com.tefo.library.commonutils.constants.RestEndpoints;
import com.tefo.library.commonutils.constants.ValidationMessages;
import com.tefo.library.customdata.field.FieldBuilder;
import com.tefo.library.customdata.field.instance.Field;
import com.tefo.library.customdata.field.instance.RemoteEntitiesField;
import com.tefo.library.customdata.field.instance.TextField;
import com.tefo.library.customdata.field.validation.instance.RequiredValidationRule;
import com.tefo.library.customdata.template.EntityStructure;

import java.util.Set;

public class TransactionStructure implements EntityStructure {

    private static final String NOTES_LABEL = "Notes";
    private static final String CURRENCY_LABEL = "Currency";
    private static final String CURRENCY_KEY = "name";
    private static final String CURRENCY_VALUE = "id";
    private static final RequiredValidationRule REQUIRED_VALIDATION_RULE = new RequiredValidationRule(ValidationMessages.VALUE_SHOULD_NOT_BE_NULL);

    @Override
    public Set<Field> getFields() {
        return Set.of(
                buildNotesField(),
                buildCurrencyField()
        );
    }


    private TextField buildNotesField() {
        return FieldBuilder.textField(TransactionEntity.Fields.notes, NOTES_LABEL)
                .required()
                .order(2)
                .build();
    }

    private RemoteEntitiesField buildCurrencyField() {
        return FieldBuilder.remoteEntitiesField(TransactionEntity.Fields.currencyId, CURRENCY_LABEL,
                        RestEndpoints.CURRENCY_URL + "/active/for-non-cash-operations",
                        CURRENCY_KEY, CURRENCY_VALUE)
                .order(1)
                .visible(true)
                .required()
                .addValidationRule(REQUIRED_VALIDATION_RULE)
                .build();
    }


    @Override
    public Set<Field> getEntityLevelFields() {
        return Set.of();
    }
}
