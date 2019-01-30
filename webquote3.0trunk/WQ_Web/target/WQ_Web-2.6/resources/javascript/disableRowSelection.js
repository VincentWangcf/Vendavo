
		/**
		 * Primefaces 3.4 fix/override 
		 */
		PrimeFaces.widget.DataTable.prototype.bindSelectionEvents = function()
		{
			var a = this;
	        this.rowSelector = this.jqId + " tbody.ui-datatable-data > tr.ui-widget-content:not(.ui-datatable-empty-message)";
	        if (this.cfg.selectionMode != "checkbox") {
	            this.bindRowHover();
	            $(document).off("click.datatable", this.rowSelector).on("click.datatable", this.rowSelector, null, function (b) {
	                a.onRowClick(b, this)
	            })
	        } else {
	            this.bindRadioEvents()
	        } if (this.isCheckboxSelectionEnabled()) {
	            this.bindCheckboxEvents();
	            this.updateHeaderCheckbox()
	        }
	        if (this.hasBehavior("rowDblselect")) {
	            $(document).off("dblclick.datatable", this.rowSelector).on("dblclick.datatable", this.rowSelector, null, function (b) {
	                a.onRowDblclick(b, $(this))
	            })
	        }
		};

