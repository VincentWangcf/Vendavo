ALTER TABLE audit_quote_item DROP CONSTRAINT AUDIT_QUOTE_ITEM_PK;
ALTER TABLE audit_quote_item ADD CONSTRAINT AUDIT_QUOTE_ITEM_PK PRIMARY KEY(update_date,quote_id,quote_item_id);