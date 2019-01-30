function updateTargetResale(rowIndex){
    updateDecimal(rowIndex, 'form_rfqSubmission', 'basicDetails_input_targetResale', 'datatable_basicDetails_rfqSubmission');
}

function validateTargetResale(rowIndex){
    validateDecimal(rowIndex, 'form_rfqSubmission', 'basicDetails_input_targetResale', 'datatable_basicDetails_rfqSubmission');
}

function updateEau(rowIndex){
	updateInteger(rowIndex, 'form_rfqSubmission', 'basicDetails_input_eau', 'datatable_basicDetails_rfqSubmission');
}

function updateRequiredQty(rowIndex){
	updateInteger(rowIndex, 'form_rfqSubmission', 'basicDetails_input_requiredQty', 'datatable_basicDetails_rfqSubmission');
}

function updateDrmsTargetResale(rowIndex){
    updateDecimal(rowIndex, 'form_rfqSubmission', 'drmsChecking_input_targetResale', 'datatable_drmsChecking_rfqSubmission');
}

function updateDrmsEau(rowIndex){
	updateInteger(rowIndex, 'form_rfqSubmission', 'drmsChecking_input_eau', 'datatable_drmsChecking_rfqSubmission');
}

function updateDrmsRequiredQty(rowIndex){
    updateInteger(rowIndex, 'form_rfqSubmission', 'drmsChecking_input_requiredQty', 'datatable_drmsChecking_rfqSubmission');
}

function updateConfirmationTargetResale(rowIndex){
    updateDecimal(rowIndex, 'form_rfqSubmission', 'confirmation_input_targetResale', 'datatable_confirmation_rfqSubmission');
}

function updateConfirmationEau(rowIndex){
	updateInteger(rowIndex, 'form_rfqSubmission', 'confirmation_input_eau', 'datatable_confirmation_rfqSubmission');
}

function checkId(obj){
	alert(obj.id);
}

function validateMfr(rowIndex){
	var formId = 'form_rfqSubmission';
	var datatableId = 'datatable_basicDetails_rfqSubmission';
	var fieldId = 'mfr';
    var mfr_valueOut = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_out';
    var v_mfr_valueOut = document.getElementById(mfr_valueOut);
    var mfr_valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var v_mfr_valueIn = document.getElementById(mfr_valueIn);
    
    var part_valueIn = formId+':'+datatableId+':' + rowIndex + ':'+'requiredPartNumber'+'_in_input'; 
    var v_part_valueIn = document.getElementById(part_valueIn);
    var part_valueOut = formId+':'+datatableId+':' + rowIndex + ':'+'requiredPartNumber'+'_out';    	
    var v_part_valueOut = document.getElementById(part_valueOut);
    if(v_mfr_valueOut != null){
        if(v_mfr_valueOut.innerHTML == ' ' || v_mfr_valueOut.innerHTML == ''){
        	if(v_part_valueOut != null) v_part_valueOut.innerHTML = '';
        	if(v_part_valueIn != null) v_part_valueIn.value = '';
            alert('Please select MFR Code before input Requested P/N');
        }
    } else if(v_mfr_valueIn != null){
        if(v_mfr_valueIn.value == ' ' || v_mfr_valueIn.value == ''){
        	if(v_part_valueOut != null) v_part_valueOut.innerHTML = '';
        	if(v_part_valueIn != null) v_part_valueIn.value = '';
            alert('Please select MFR Code before input Requested P/N');
        }
    }    
}