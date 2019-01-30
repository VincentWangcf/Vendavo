ALTER TABLE audit_quote_item RENAME COLUMN update_date TO update_date_BAK;
ALTER TABLE audit_quote_item ADD update_date date;
update audit_quote_item set update_date = update_date_BAK;
update audit_quote_item set update_date = (update_date+(15/24)) where update_date < to_date('2016/07/08','yyyy/MM/dd');