function sleep(ms)
{
	var dt = new Date();
	dt.setTime(dt.getTime() + ms);
	while (new Date().getTime() < dt.getTime());
}

var wait = 0;

function start() {
	wait = 1;
	//sleep(1000);
	if(wait == 1){
		PF('statusDialog').show();
	}
}   
 
function stop() {
	if(wait == 1){
		PF('statusDialog').hide();
	} else {
		wait = 0;
	}
}

function start2() { 
	PF('statusDialog2').show();   
}   
 
function stop2() {   
	PF('statusDialog2').hide();   
}
function stopWithError2() {
	alert("An unexpected error is encountered during the Ajax operation, please try again. Please refresh page if error persists.");
	PF('statusDialog2').hide();   
}
function stopWithError() {
	alert("An unexpected error is encountered during the Ajax operation, please try again. Please refresh page if error persists.");
	PF('statusDialog').hide();   
}

function removePercentSymbol(obj){
	if(obj.value != null && obj.value != ''){
		var lastPosition = obj.value.substr(obj.value.length-1, obj.value.length);
		if(lastPosition == '%'){
			obj.value = obj.value.substr(0, obj.value.length - 1);
		}
	}
}

function appendPercentSymbol(obj){
	if(obj.value != null && obj.value != ''){
		var lastPosition = obj.value.substr(obj.value.length-1, obj.value.length);
		if(lastPosition != '%'){
			obj.value = obj.value + '%';
		}
	}
}

function convertToSupper(obj){
	if(obj.value != null && obj.value != ''){
		obj.value = obj.value.toUpperCase();
	}
}

function checkEnterKey(e, obj) {
    if (e.which == 13 || e.keyCode == 13) {
    	obj.blur();
    }
    
    return false;
}

function handleResizeDialog(dialog) {
    var el = $(dialog.jqId);
    var doc = $('body');
    var win = $(window);
    var elPos = '';
    var bodyHeight = '';
    var bodyWidth = '';
    // position:fixed is maybe cool, but it makes the dialog not scrollable on browser level, even if document is big enough
    if (el.height() > win.height()) {
        bodyHeight = el.height() + 'px';
        elPos = 'absolute';
    }   
    if (el.width() > win.width()) {
        bodyWidth = el.width() + 'px';
        elPos = 'absolute';
    }
    el.css('position', elPos);
    doc.css('width', bodyWidth);
    doc.css('height', bodyHeight);
    var pos = el.offset();
    if (pos.top + el.height() > doc.height())
        pos.top = doc.height() - el.height();
    if (pos.left + el.width() > doc.width())
        pos.left = doc.width() - el.width();
    var offsetX = 0;
    var offsetY = 0;
    if (elPos != 'absolute') {
        offsetX = $(window).scrollLeft();
        offsetY = $(window).scrollTop();
    }
    // scroll fix for position fixed
    if (pos.left < offsetX)
        pos.left = offsetX;
    if (pos.top < offsetY)
        pos.top = offsetY;
    el.offset(pos);
}

function focusOnPartSearchSubmit(){
	document.getElementById('form_rfqSubmission:validation_expansion_SearchButton').focus();
}

function checkDecimal(obj){	
    while(!obj.value.match(/^[0-9]{1,9}(\.{0,1}[0-9]{0,5})?$/) && obj.value.length > 0){
    	obj.value = obj.value.substr(0, obj.value.length-1);    	
    }
}

function checkInteger(obj){	
    while(!obj.value.match(/^[0-9]\d{0,9}$/) && obj.value.length > 0){
    	obj.value = obj.value.substr(0, obj.value.length-1);    	
    }
}

function checkDate(obj){
	if(!obj.value.match(/^[0-9]{2}[\/][0-9]{2}[\/][0-9]{4}$/) && obj.value != ''){
		obj.value = '';
		alert('The Date Format must be dd/MM/yyyy.');
	}
}

function updateDecimal(rowIndex, formId, fieldId, datatableId){
    var valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var valueOut = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_out';   
    var v_valueIn = document.getElementById(valueIn);
    var v_valueOut = document.getElementById(valueOut);
    while(!v_valueIn.value.match(/^[0-9]{1,9}(\.{0,1}[0-9]{0,5})?$/) && v_valueIn.value.length > 0){    	
    	v_valueIn.value = v_valueIn.value.substr(0, v_valueIn.value.length-1);
    	if(v_valueOut != null)
			v_valueOut.innerHTML = v_valueIn.value;
    }
}
function updateInteger(rowIndex, formId, fieldId, datatableId){
    var valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var valueOut = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_out';   
    var v_valueIn = document.getElementById(valueIn);
    var v_valueOut = document.getElementById(valueOut);
    while(!v_valueIn.value.match(/^[0-9]\d{0,9}$/) && v_valueIn.value.length > 0){    	   	
    	v_valueIn.value = v_valueIn.value.substr(0, v_valueIn.value.length-1);
    	v_valueOut.innerHTML = v_valueIn.value;
    }
}

function updateInteger2(rowIndex, formId, fieldId, datatableId){
    var valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var valueOut = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_out';   
    var v_valueIn = document.getElementById(valueIn);
    var v_valueOut = document.getElementById(valueOut);
    while(!v_valueIn.value.match(/^[0-9]\d{0,9}$/) && v_valueIn.value.length > 0){    	   	
    	v_valueIn.value = v_valueIn.value.substr(0, v_valueIn.value.length-1);
    	if(v_valueOut != null)
    		v_valueOut.innerHTML = v_valueIn.value;
    }
}

function validateDecimal(rowIndex, formId, fieldId, datatableId){
    var valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var valueOut = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_out';   
    var v_valueIn = document.getElementById(valueIn);
    var v_valueOut = document.getElementById(valueOut);
    
    if(v_valueIn.value.match(/^[0]{0,9}(\.{0,1}[0]{0,5})?$/) && v_valueIn.value.length > 0){
    	v_valueIn.value = '';
    	v_valueOut.innerHTML = '';    	
    }
}




function isInteger(evt, allowNegative){  
    var theEvent = evt || window.event;
	var key = theEvent.keyCode || theEvent.which;
	key = String.fromCharCode(key);
	var regex;
	if (allowNegative == true){
		regex = /[0-9]|-/;	
   	}else{
   		regex = /[0-9]/;	
    }    	
	if( !regex.test(key) ) {
  		theEvent.returnValue = false;
  		if(theEvent.preventDefault) theEvent.preventDefault();
	}
}	

function isDouble(evt, allowNegative){  
    var theEvent = evt || window.event;
	var key = theEvent.keyCode || theEvent.which;
	key = String.fromCharCode(key);
	var regex;
	if (allowNegative == true){
		regex = /[0-9]|\.|-/;	
   	}else{
   		regex = /[0-9]|\./;	
    }
	
	if( !regex.test(key) ) {
  		theEvent.returnValue = false;
  		if(theEvent.preventDefault) theEvent.preventDefault();
	}
}	