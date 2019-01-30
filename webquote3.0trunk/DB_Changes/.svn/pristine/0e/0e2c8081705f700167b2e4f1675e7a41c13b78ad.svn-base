delete from APP_MESSAGES where MESSAGE_CODE in ('wq.error.progClsingDate',
'wq.error.20030',
'wq.message.uplodMsg',
'wq.expiryDateQuoteeffectiveDate');

commit;

delete from APP_LABEL where MESSAGE_CODE in ('wq.button.smSubmit',
'wq.label.ConAllNode',
'wq.label.drExpDate',
'wq.label.drExpiryDate',
'wq.label.pendAt',
'wq.label.saleQty',
'wq.label.shipToCode',
'wq.label.soldToCode',
'wq.label.tcspace',
'wq.label.restirctedCustomerMappingUpload',
'wq.label.restrcitedCustControl');
commit;


set define off;
Insert into APP_LABEL (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('en','RestrictedCustomerMapping Upload','wq.label.restrictedCustomerMappingUpload');
Insert into APP_LABEL (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('en','Restricted Customer Control','wq.label.restrictedCustControl');
Insert into APP_MESSAGES (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('en','Field Type Error - Program Closing Date must be Date(dd/MM/yyyy)','wq.error.progClosingDate');
Insert into APP_MESSAGES (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('en','Quote Expiry date {0} entered cannot be earlier than Quotation Effective Date {1} for RFQ: {2}','wq.expiryDateQuoteEffectiveDate');
Insert into APP_MESSAGES (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('en','is uploaded.','wq.message.uploadMsg');
commit;
Insert into APP_LABEL (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('ja','特定の顧客マッピングのアップロード','wq.label.restrictedCustomerMappingUpload');
Insert into APP_LABEL (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('ja','制限された顧客制御','wq.label.restrictedCustControl');
INSERT into APP_LABEL (LOCALE_ID, MESSAGE_CODE, MESSAGE) values ('en', 'wq.label.language', 'Language');
INSERT into APP_LABEL (LOCALE_ID, MESSAGE_CODE, MESSAGE) values ('ja', 'wq.label.language', '言語');
Insert into APP_MESSAGES (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('ja','フィールド種別エラー-プログラム終了日は日付（dd/MM/yyyy）でなければなりません。','wq.error.progClosingDate');
Insert into APP_MESSAGES (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('ja','入力した失効日{0}はRFQの見積有効日{1}より前にすることはできません：{2}','wq.expiryDateQuoteEffectiveDate');
Insert into APP_MESSAGES (LOCALE_ID,MESSAGE,MESSAGE_CODE) values ('ja','がアップロードされます。','wq.message.uploadMsg');
commit;

set define off;
update APP_MESSAGES set MESSAGE = 'backup file exception :' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.error.90000';
update APP_MESSAGES set MESSAGE = 'Transfer exception :' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.error.90031';
update APP_MESSAGES set MESSAGE = 'Get the STM config info failed' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.message.getSTMConfigFailed';
update APP_MESSAGES set MESSAGE = 'Save draft RFQ succussfully.' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.message.savedDraftSuccess';
update APP_MESSAGES set MESSAGE = 'Save selected RFQ succussfully.' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.message.savedSuccess';

update APP_MESSAGES set MESSAGE = 'backup file exception :' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.error.90000';
update APP_MESSAGES set MESSAGE = 'Transfer exception :' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.error.90031';
update APP_MESSAGES set MESSAGE = 'STMの取得に失敗しました。' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.message.getSTMConfigFailed';
update APP_MESSAGES set MESSAGE = 'ドラフトRFQを成功裏に保存しました。' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.message.savedDraftSuccess';
update APP_MESSAGES set MESSAGE = '選択したRFQを成功裏に保存しました。s' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.message.savedSuccess';
commit;

set define off;
update APP_LABEL set MESSAGE = 'New Parts Created' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.header.recordsPartCreated';
update APP_LABEL set MESSAGE = 'Parts Removed' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.header.recordsPartRemoved';
update APP_LABEL set MESSAGE = 'Parts Updated' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.header.recordsPartUpdated';
update APP_LABEL set MESSAGE = 'Target Resale' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.header.trgtResale';
update APP_LABEL set MESSAGE = 'Last BMT In Date' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastBmtInDate';
update APP_LABEL set MESSAGE = 'Last BMT Out Date' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastBmtOutDate';
update APP_LABEL set MESSAGE = 'Last IT Update Time' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastItUpdateTime';
update APP_LABEL set MESSAGE = 'Last PM Updated On' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastPmUpdatedOn';
update APP_LABEL set MESSAGE = 'Last QC In Date' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastQcInDate';
update APP_LABEL set MESSAGE = 'Last QC Out Date' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastQcOutDate';
update APP_LABEL set MESSAGE = 'Last QC Updated On' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastQcUpdateOn';
update APP_LABEL set MESSAGE = 'Last RIT Update Time' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastRitUpdateTime';
update APP_LABEL set MESSAGE = 'Last SQ In Date' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastSqInDate';
update APP_LABEL set MESSAGE = 'Last SQ Out Date' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastSqOutDate';
update APP_LABEL set MESSAGE = 'Last SQ Update Time' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastSqUpdateTime';
update APP_LABEL set MESSAGE = 'Last Updated PM' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastUpdatedPm';
update APP_LABEL set MESSAGE = 'Last Updated QC' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastUpdatedQc';
update APP_LABEL set MESSAGE = 'Last Updated QC Name' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.lastUpdatedQcName';
update APP_LABEL set MESSAGE = 'LOA Flag' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.loaFlag';
update APP_LABEL set MESSAGE = 'Sales (For CS)' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.salesforcs';
update APP_LABEL set MESSAGE = 'WebQuote V3.0' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.title.webQuoteVersion';
update APP_LABEL set MESSAGE = 'CS' where LOCALE_ID = 'en' and MESSAGE_CODE = 'wq.label.cs';

update APP_LABEL set MESSAGE = '作成された新しいパーツ' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.header.recordsPartCreated';
update APP_LABEL set MESSAGE = '部品が削除されました' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.header.recordsPartRemoved';
update APP_LABEL set MESSAGE = '部品が更新されました' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.header.recordsPartUpdated';
update APP_LABEL set MESSAGE = '目標再販' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.header.trgtResale';
update APP_LABEL set MESSAGE = 'BMTへの最終引き渡し日' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastBmtInDate';
update APP_LABEL set MESSAGE = 'BMTからの最終回答日' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastBmtOutDate';
update APP_LABEL set MESSAGE = '前回のITアップデート時間' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastItUpdateTime';
update APP_LABEL set MESSAGE = '最終PM更新日' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastPmUpdatedOn';
update APP_LABEL set MESSAGE = '最終QC入り日' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastQcInDate';
update APP_LABEL set MESSAGE = '最終QC出し日' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastQcOutDate';
update APP_LABEL set MESSAGE = '最後に更新されたQC' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastQcUpdateOn';
update APP_LABEL set MESSAGE = '最終RIT(対応済み内部転送)更新時間' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastRitUpdateTime';
update APP_LABEL set MESSAGE = '最終SQ入り日' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastSqInDate';
update APP_LABEL set MESSAGE = '最終SQ出し日' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastSqOutDate';
update APP_LABEL set MESSAGE = '最終SQ更新時間' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastSqUpdateTime';
update APP_LABEL set MESSAGE = '最終更新したPM' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastUpdatedPm';
update APP_LABEL set MESSAGE = '最終更新したQC' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastUpdatedQc';
update APP_LABEL set MESSAGE = '最終更新QC名' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.lastUpdatedQcName';
update APP_LABEL set MESSAGE = 'LOAフラグ' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.loaFlag';
update APP_LABEL set MESSAGE = '販売（CS用）' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.salesforcs';
update APP_LABEL set MESSAGE = 'WebQuote V3.0' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.title.webQuoteVersion';
update APP_LABEL set MESSAGE = 'CS' where LOCALE_ID = 'ja' and MESSAGE_CODE = 'wq.label.cs';

commit;
