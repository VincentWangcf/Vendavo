
INSERT INTO APP_FUNCTION (ID,NAME) VALUES((SELECT MAX(ID) + 1 FROM APP_FUNCTION), 'Dispatcher Control');
 
 
INSERT INTO APP_FUNCTION_ROLE_MAPPING (FUNCTION_ID,ROLE_ID) VALUES((SELECT ID FROM APP_FUNCTION WHERE NAME='Dispatcher Control'),
  (SELECT ID FROM ROLE WHERE NAME='ROLE_SYS_ADMIN'));
  
insert into app_resource values ('SCREEN', ( select max(id)+1 from app_resource), '/DispacherAdmin/DispatcherControl.jsf', 
(SELECT ID FROM APP_FUNCTION WHERE NAME='Maintain Admin Profile'), 1, 1);

insert into app_label(id, locale_id, message_code, message) values ((select max(id) + 1 from app_label), 'en', 'wq.label.dispatcherCotrl', 'Dispatcher Control');

COMMIT;