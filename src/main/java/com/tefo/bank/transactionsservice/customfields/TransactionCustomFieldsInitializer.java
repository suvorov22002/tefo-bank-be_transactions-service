package com.tefo.bank.transactionsservice.customfields;

import com.tefo.bank.transactionsservice.model.TransactionFieldValue;
import com.tefo.library.commonutils.constants.RestEndpoints;
import com.tefo.library.customdata.processing.SupportedDatabaseType;
import com.tefo.library.customdata.processing.SupportedIDType;
import com.tefo.library.customdata.processing.annotation.*;

@CustomFieldConfiguration(
        databaseType = SupportedDatabaseType.POSTGRESQL,
        idType = SupportedIDType.LONG,
        entityLevelType = SupportedIDType.LONG,
        fieldEntity = @CustomFieldEntity(name = "TransactionFieldEntity", dbAlias = "transaction_custom_field"),
        templateEntity = @CustomTemplateEntity(name = "TransactionTemplateEntity", dbAlias = "transaction_entity_template", entityStructure = TransactionStructure.class),
        fieldRepository = @CustomFieldRepository(name = "TransactionFieldRepository"),
        templateRepository = @CustomTemplateRepository(name = "TransactionTemplateRepository"),
        fieldService = @CustomFieldService(name = "TransactionFieldService"),
        templateService = @CustomTemplateService(name = "TransactionTemplateService"),
        fieldController = @CustomFieldController(name = "TransactionFieldController", requestMapping = RestEndpoints.TRANSACTIONS_URL + "/transaction-fields"),
        templateController = @CustomTemplateController(name = "TransactionTemplateController", requestMapping = RestEndpoints.TRANSACTIONS_URL + "/templates"),
        fieldValueBuilder = @CustomFieldValueBuilder(name = "TransactionFieldValueBuilder", sqlFieldValueEntity = TransactionFieldValue.class),
        fieldValueValidator = @CustomFieldValueValidator(name = "TransactionFieldValueValidator"),
        fieldGroupEntity = @CustomFieldGroupEntity(name = "TransactionFieldGroupEntity", dbAlias = "transaction_custom_field_group"),
        fieldGroupRepository = @CustomFieldGroupRepository(name = "TransactionFieldGroupRepository"),
        fieldGroupService = @CustomFieldGroupService(name = "TransactionFieldGroupService"),
        fieldGroupController = @CustomFieldGroupController(name = "TransactionFieldGroupController", requestMapping = RestEndpoints.TRANSACTIONS_URL + "/transaction-groups")
)
public interface TransactionCustomFieldsInitializer {
}
