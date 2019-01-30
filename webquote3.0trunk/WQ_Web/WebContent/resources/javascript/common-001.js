function start() { 
	PF('statusDialog').show();   
}   
 
function stop() {   
	PF('statusDialog').hide();   
}

function start2() { 
	PF('statusDialog2').show();   
}   
 
function stop2() {   
	PF('statusDialog2').hide();   
}
function stopWithError2() {
	alert("The error is found in the ajax");
	PF('statusDialog2').hide();   
}
function stopWithError() {
	alert("The error is found in the ajax");
	PF('statusDialog').hide();   
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