function updateDecimal2(rowIndex, fieldId, datatableId){
    var valueIn = 'form_submit:'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var valueOut = 'form_submit:'+datatableId+':' + rowIndex + ':'+fieldId+'_out';   
    var v_valueIn = document.getElementById(valueIn);
    var v_valueOut = document.getElementById(valueOut);
    while(!v_valueIn.value.match(/^\s*-?[0-9]\d{0,9}(\.\d{0,5})?\s*$/) && v_valueIn.value.length > 0){    	
    	v_valueIn.value = v_valueIn.value.substr(0, v_valueIn.value.length-1);
    	v_valueOut.value = v_valueIn.value;
    }
}

function updateInteger2(rowIndex, fieldId, datatableId){
    var valueIn = 'form_submit:'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var valueOut = 'form_submit:'+datatableId+':' + rowIndex + ':'+fieldId+'_out';   
    var v_valueIn = document.getElementById(valueIn);
    var v_valueOut = document.getElementById(valueOut);
    while(!v_valueIn.value.match(/^[0-9]\d{0,9}$/) && v_valueIn.value.length > 0){    	   	
    	v_valueIn.value = v_valueIn.value.substr(0, v_valueIn.value.length-1);
    	v_valueOut.value = v_valueIn.value;
    }
}

function updateTargetResale(rowIndex){
    updateDecimal(rowIndex, 'basicDetails_input_targetResale', 'datatable_basicDetails_rfqSubmission');
}


function updateProgRequiredQty(rowIndex){
	updateInteger2(rowIndex, 'requiredQtyProg', 'submissionTable');
}
function updateProgTargetResale(rowIndex){
	updateDecimal2(rowIndex, 'targetResaleProg', 'submissionTable');
}

