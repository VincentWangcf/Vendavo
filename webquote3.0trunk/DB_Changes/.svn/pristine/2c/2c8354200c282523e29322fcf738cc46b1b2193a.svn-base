---update configure tables
update APP_PROPERTY t set T.PROP_VALUE='/app/eap70/webquote_jp/attachments' where T.PROP_KEY ='ATTACHMENT_ROOT_PATH';
update APP_PROPERTY t set T.PROP_VALUE='/app/eap70/webquote_jp/tempdir/' where T.PROP_KEY ='TMP_DIRECTORY';

update system_code_maintenance t set T.VALUE ='/app/eap70/webquote/b2bfiles/inbound/' where T.CATEGORY ='STM_RECEIVE_PATH';
update system_code_maintenance t set T.VALUE ='/app/eap70/webquote/b2bfiles/outbound/' where T.CATEGORY ='STM_SEND_PATH';
update system_code_maintenance t set T.VALUE ='/app/eap70/webquote/b2bfiles/backup/' where T.CATEGORY ='STM_FILE_BACKUP';
update system_code_maintenance t set T.VALUE ='/app/eap70/webquote/b2bfiles/exception/' where T.CATEGORY ='STM_EXCEPTION_PATH';
update system_code_maintenance t set T.VALUE ='/app/eap70/webquote/sapfiles/tempfile/' where T.CATEGORY ='PRICER_UPLOAD_OFFLINE';

update Schedule_Config t set t.value='/app/eap70/webquote_jp/sapfiles/' where T.type='COMMON' and T.key='FILE_PATH';
update Schedule_Config t set t.value='/app/eap70/webquote_jp/sapfiles/backup/' where T.type='COMMON' and T.key='BACKUP_PATH';
