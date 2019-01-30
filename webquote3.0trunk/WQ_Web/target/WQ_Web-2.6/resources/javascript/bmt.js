function start() {
	PF('blockUI').show();
}

function stop() {
	PF('blockUI').hide();
}

/**
 * remark by Jason
 * overwrite primefaces3.5 BlockUI component JS part.
 * handle bugs for primefaces3.5 component BlockUI
 * if fileupload and BlockUI coexist in same page, click upload button on fileupload component no response, chome console can get error message, like 'source.name' is unkonw. 
 */
PrimeFaces.widget.BlockUI = PrimeFaces.widget.BaseWidget
		.extend({
			init : function(a) {
				this.cfg = a;
				this.id = this.cfg.id;
				this.jqId = PrimeFaces.escapeClientId(this.id);
				this.block = $(PrimeFaces.escapeClientId(this.cfg.block));
				this.content = $(this.jqId);
				this.cfg.animate = (this.cfg.animate === false) ? false : true;
				this.render();
				if (this.cfg.triggers) {
					this.bindTriggers()
				}
				if (this.cfg.blocked) {
					this.show()
				}
				$(this.jqId + "_s").remove()
			},
			refresh : function(a) {
				$(document).off(
						"ajaxSend." + this.id + " ajaxComplete." + this.id);
				this._super(a)
			},
			bindTriggers : function() {
				var a = this, b = this.cfg.triggers.split(",");
				$(document).bind("ajaxSend", function(d, f, c) {
					if (c) {
						if ($.inArray(c.source, b) != -1) {
							a.show()
						}
					}
				});
				$(document).bind("ajaxComplete", function(d, f, c) {
					if (c) {
						if ($.inArray(c.source, b) != -1) {
							a.hide()
						}
					}
				})
			},
			show : function() {
				var a = this.block.outerWidth(), b = this.block.outerHeight();
				this.blocker.width(a).height(b);
				this.content.css({
					left : (a - this.content.outerWidth()) / 2,
					top : (b - this.content.outerHeight()) / 2
				});
				if (this.cfg.animate) {
					this.blocker.fadeIn()
				} else {
					this.blocker.show()
				}
				if (this.hasContent()) {
					this.content.fadeIn()
				}
			},
			hide : function() {
				if (this.cfg.animate) {
					this.blocker.fadeOut()
				} else {
					this.blocker.hide()
				}
				if (this.hasContent()) {
					this.content.fadeOut()
				}
			},
			render : function() {
				this.blocker = $('<div id="'
						+ this.id
						+ '_blocker" class="ui-blockui ui-widget-overlay ui-helper-hidden"></div>');
				if (this.block.hasClass("ui-corner-all")) {
					this.blocker.addClass("ui-corner-all")
				}
				this.block.css("position", "relative").append(this.blocker)
						.append(this.content)
			},
			hasContent : function() {
				return this.content.contents().length > 0
			}
		});