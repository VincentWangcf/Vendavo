function handleFiltered(xhr, status, args){

	if(args){
	
	    document.getElementById("form_pendinglist:qcCount").innerHTML = 'QC : ' + args.qcCount; 
	    document.getElementById("form_pendinglist:itCount").innerHTML = 'IT : ' + args.itCount; 
	    document.getElementById("form_pendinglist:ritCount").innerHTML = 'RIT : ' + args.ritCount; 
	    document.getElementById("form_pendinglist:sqCount").innerHTML = 'SQ : ' + args.sqCount;
	    
	    var mfrCode_size = args.mfrCode_size;
		var team_size = args.team_size;
		var soldTo_size = args.soldTo_size;
		
		var i=0;

		if(mfrCode_size > 0){
			var mfrCodes = document.getElementById("form_pendinglist:datatable_pendinglist:p_mfr:filter");
			var selectedMfr = '';
			for(i = 0; i < mfrCodes.options.length; i++){
				if(mfrCodes.options[i].selected == true){
					selectedMfr = mfrCodes.options[i].value;
				}
			}			
			mfrCodes.options.length = 0;
			mfrCodes.options[0] = new Option('All', '');    	
			for(i = 0; i < mfrCode_size; i++){
				var mfrCode_v = eval('args.mfrCode'+i);
				mfrCode_v = unescape(mfrCode_v);
				mfrCodes.options[i+1] = new Option(mfrCode_v, mfrCode_v);
				if(mfrCodes.options[i+1].value == selectedMfr)
					mfrCodes.options[i+1].selected = true;
			}			
		}

		if(team_size > 0){
			var teams = document.getElementById("form_pendinglist:datatable_pendinglist:p_team:filter");
			var selectedTeam = '';
			for(i = 0; i < teams.options.length; i++){
				if(teams.options[i].selected == true){
					selectedTeam = teams.options[i].value;
				}
			}			
			teams.options.length = 0;
			teams.options[0] = new Option('All', '');    	
			for(i = 0; i < team_size; i++){
				var team_v = eval('args.team'+i);
				team_v = unescape(team_v);
				teams.options[i+1] = new Option(team_v, team_v);
				if(teams.options[i+1].value == selectedTeam)
					teams.options[i+1].selected = true;
			}
		}
		
		if(soldTo_size > 0){
			var soldTos = document.getElementById("form_pendinglist:datatable_pendinglist:p_soldTo:filter");
			var selectedSoldTo = '';
			for(i = 0; i < soldTos.options.length; i++){
				if(soldTos.options[i].selected == true){
					selectedSoldTo = soldTos.options[i].value;
				}
			}						
			soldTos.options.length = 0;
			soldTos.options[0] = new Option('All', '');    	
			for(i = 0; i < soldTo_size; i++){
				var soldTo_v = eval('args.soldTo'+i);
				soldTo_v = unescape(soldTo_v);			
				soldTos.options[i+1] = new Option(soldTo_v, soldTo_v); 
				if(soldTos.options[i+1].value == selectedSoldTo)
					soldTos.options[i+1].selected = true;
			}
		}
		
	}
} 

var previousQuoteItemId = 0;

function checkUpdateDetailPanel(editmode, changed, currentNumber, rowInd){
	if(currentNumber != previousQuoteItemId){
		datatable_pendinglist_var.unselectAllRows();
		datatable_pendinglist_var.selectRow(rowInd);
		previousQuoteItemId = currentNumber;
	}	
}

function validDateFormat(inp){
    try{
        var D, d=inp.value.split(/\D+/);
        d[0]*=1;
        d[1]-=1;
        d[2]*=1;
        
        D=new Date(d[0],d[1],d[2]);
        
        if(D.getFullYear()== d[0] && D.getMonth()== d[1] && D.getDate()== d[2]){
        	return true;
        } else {
        	return false;
        }
    }
    catch(er){
        return false;
    }
}

function updateQuotedMargin(rowIndex){

	var quotedPrice = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':quotedPrice_in';
    var margin = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':quotedMargin_';
    var cost = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':cost_in';
    var v_quotedPrice = document.getElementById(quotedPrice);
    var v_cost = document.getElementById(cost);
    if(v_quotedPrice.value != ''){
	    document.getElementById(margin+"out").innerHTML  = Math.round((v_quotedPrice.value - v_cost.value)*100*100/v_quotedPrice.value)/100;
	    document.getElementById(margin+"in").value  = Math.round((v_quotedPrice.value - v_cost.value)*100*100/v_quotedPrice.value)/100;
    } else {
	    document.getElementById(margin+"out").innerHTML  = '';
	    document.getElementById(margin+"in").value  = '';    	
    }

	updateDecimal(rowIndex, 'form_pendinglist', 'quotedMargin', 'datatable_pendinglist');
}

function updateTargetMargin(rowIndex){
	
    var targetResale = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':targetResale';
    var margin = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':targetMargin';
    var cost = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':cost_in';
    var v_targetResale = document.getElementById(targetResale);
    var v_cost = document.getElementById(cost);
    if(v_targetResale.innerHTML != ''){
	    document.getElementById(margin).innerHTML  = Math.round((v_targetResale.innerHTML - v_cost.value)*100*100/v_targetResale.innerHTML)/100;
    } else {
	    document.getElementById(margin).innerHTML  = '';    	
    }
}

function updateQuotedPrice(rowIndex){
	updateDecimal(rowIndex, 'form_pendinglist', 'quotedPrice', 'datatable_pendinglist');
	
    var cost = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':cost_';

	var quotedPrice = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':quotedPrice_';
    var quotedMargin = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':quotedMargin_';
    var v_quotedPrice = document.getElementById(quotedPrice+"in");
    var v_cost = document.getElementById(cost+"out");
    if(v_quotedPrice.value != '' && v_cost.innerHTML != ''){
	    document.getElementById(quotedMargin+"out").innerHTML  = Math.round((v_quotedPrice.value - v_cost.innerHTML)*100*100/v_quotedPrice.value)/100;
	    document.getElementById(quotedMargin+"in").value  = Math.round((v_quotedPrice.value - v_cost.innerHTML)*100*100/v_quotedPrice.value)/100;
    } else {
	    document.getElementById(quotedMargin+"out").innerHTML  = '';
	    document.getElementById(quotedMargin+"in").value  = '';    	
    }	

}

function updateTargetAndQuoteMargin(rowIndex){
	updateQuotedMargin(rowIndex);
	updateTargetMargin(rowIndex);	
}

function updateResaleIndicator(rowIndex){
	var formId = 'form_pendinglist';
	var datatableId = 'datatable_pendinglist';
	var fieldId = 'resalesIndicator';
    var valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var valueOut = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_out';
    var v_valueIn = document.getElementById(valueIn);
    var v_valueOut = document.getElementById(valueOut);	

    while(!v_valueIn.value.match(/^[0-9]{4}|[0-9]{2}[A][A]$/) && v_valueIn.value.length > 0){
    	v_valueIn.value = '';
    	v_valueOut.innerHTML = '';  	
    }
}

function updateCancellationWindow(rowIndex){
	updateInteger(rowIndex, 'form_pendinglist', 'cancellationWindow', 'datatable_pendinglist');
}

function updateReschedulingWindow(rowIndex){
	updateInteger(rowIndex, 'form_pendinglist', 'reschedulingWindow', 'datatable_pendinglist');	
}

function updateVendorQuoteQty(rowIndex){
	updateInteger(rowIndex, 'form_pendinglist', 'vendorQuoteQty', 'datatable_pendinglist');		
}

function updateShipmentValidity(rowIndex){
	var formId = 'form_pendinglist';
	var datatableId = 'datatable_pendinglist';
	var fieldId = 'shipmentValidity';
    var valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var valueInn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_out';	
    var v_valueIn = document.getElementById(valueIn);
    var v_valueOut = document.getElementById(valueOut);
	
	if(!validDateFormat(v_valueIn.value)){
		v_valueIn.value = '';
	}
}

function updatePriceValidity(rowIndex){
	var formId = 'form_pendinglist';
	var datatableId = 'datatable_pendinglist';
	var fieldId = 'validity';
    var valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var valueOut = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_out';	
    var v_valueIn = document.getElementById(valueIn);
    var v_valueOut = document.getElementById(valueOut);
    
    if(v_valueIn.value.length > 0){
	    if(v_valueIn.value.match(/^[0-9]\d{0,9}$/)){
	    } else if(v_valueIn.value.match(/^[0-3][0-9][\/][0-1][0-9][\/][0-9]{4}$/)){
	    } else {
	    	alert('Invalid Input : ' + v_valueIn.value);
	    	v_valueIn.value = '';
	    	//v_valueOut.innerHTML = '';
	    }
	}
}

function updateMov(rowIndex){
	updateInteger(rowIndex, 'form_pendinglist', 'mov', 'datatable_pendinglist');	
}

function updateMoq(rowIndex){
	updateInteger(rowIndex, 'form_pendinglist', 'moq', 'datatable_pendinglist');		
}

function updateMpq(rowIndex){
	updateInteger(rowIndex, 'form_pendinglist', 'mpq', 'datatable_pendinglist');
}

function updateLeadTime(rowIndex){
	
}

function updateQuotedQty(rowIndex){
	var formId = 'form_pendinglist';
	var datatableId = 'datatable_pendinglist';
	var fieldId = 'qtyIndicator';
    var valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var v_valueIn = document.getElementById(valueIn);
     
    if(v_valueIn.value != 'Exact'){
    	alert("Please check Qty Indicator.\n\rQuoted Qty can be edited ONLY WHEN Qty Indicator is Exact");
    } else {
    	updateInteger(rowIndex, formId, 'quotedQty', datatableId);			
    }
}

function updateCost(rowIndex){
	updateDecimal(rowIndex, 'form_pendinglist', 'cost', 'datatable_pendinglist');

    var cost = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':cost_';
    var v_cost = document.getElementById(cost+"in");

	var quotedPrice = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':quotedPrice_';
    var quotedMargin = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':quotedMargin_';
    var v_quotedPrice = document.getElementById(quotedPrice+"out");
    if(v_quotedPrice.innerHTML != '' && v_cost.value != ''){
	    document.getElementById(quotedMargin+"out").innerHTML  = Math.round((v_quotedPrice.innerHTML - v_cost.value)*100*100/v_quotedPrice.innerHTML)/100;
	    document.getElementById(quotedMargin+"in").value  = Math.round((v_quotedPrice.innerHTML - v_cost.value)*100*100/v_quotedPrice.innerHTML)/100;
    } else {
	    document.getElementById(quotedMargin+"out").innerHTML  = '';
	    document.getElementById(quotedMargin+"in").value  = '';    	
    }	
    
    var targetResale = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':targetResale';
    var targetMargin = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':targetMargin';
    var v_targetResale = document.getElementById(targetResale);
    var v_margin = document.getElementById(targetMargin);
    if(v_targetResale.innerHTML != '' && v_cost.value != ''){
    	v_margin.innerHTML  = Math.round((v_targetResale.innerHTML - v_cost.value)*100*100/v_targetResale.innerHTML)/100;
    } else {
    	v_margin.innerHTML  = '';  
    }
}

function updateQuotedMargin(rowIndex){

	updateDecimal(rowIndex, 'form_pendinglist', 'quotedMargin', 'datatable_pendinglist');	
	
    var quotedPrice = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':quotedPrice_';
    var quotedMargin = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':quotedMargin_';
    var cost = 'form_pendinglist:datatable_pendinglist:' + rowIndex + ':cost_';
    var v_quotedMargin = document.getElementById(quotedMargin+'in');
    var v_cost = document.getElementById(cost+'out');
    if(v_quotedMargin.value != '' && v_cost.innerHTML != ''){
	    document.getElementById(quotedPrice+"out").innerHTML = Math.round((v_cost.innerHTML*(1+(v_quotedMargin.value/100)))*1000)/1000;
	    document.getElementById(quotedPrice+"in").value = Math.round((v_cost.innerHTML*(1+(v_quotedMargin.value/100)))*1000)/1000;
    } else {
	    document.getElementById(quotedPrice+"out").innerHTML = '';
	    document.getElementById(quotedPrice+"in").value = '';    	
    }
}

function checkQtyIndicator(rowIndex){
	var formId = 'form_pendinglist';
	var datatableId = 'datatable_pendinglist';
	var fieldId = 'qtyIndicator';
    var valueIn = formId+':'+datatableId+':' + rowIndex + ':'+fieldId+'_in';
    var v_valueIn = document.getElementById(valueIn);    
    if(v_valueIn.value != 'Exact'){
    	alert("Please check Qty Indicator.\n\rQuoted Qty can be edited ONLY WHEN Qty Indicator is Exact");
        valueIn = formId+':'+datatableId+':' + rowIndex + ':'+'quotedQty'+'_in';
        v_valueIn = document.getElementById(valueIn);
        v_valueIn.blur();
    	
    }
}

function checkQtyIndicator(){
	/*
	var formId = 'panelgrid_rfqdetailreference';
	var fieldId = 'wpInput_qtyIndicator';	
    var valueIn = formId+':'+fieldId;
    var v_valueIn = document.getElementById(valueIn);    
    if(v_valueIn.value != 'Exact'){
    	alert("Please check Qty Indicator.\n\rQuoted Qty can be edited ONLY WHEN Qty Indicator is Exact");
        valueIn = formId+':wpInput_quotedQty';
        v_valueIn = document.getElementById(valueIn);
        v_valueIn.blur();
    	
    }
    */
}
