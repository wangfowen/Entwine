Entwine.scheduler.views.EventsSelectView = Backbone.View.extend({
	events: {
		"click #submitButton": "plan"
	},
	
	calendarObject: null,
	infoIcon: null,
	infoDialog: null,

	initialize: function(){
		var _this = this;
		
		this.calendarObject = $("#calendarContainer");
		this.infoIcon = $("#infoIcon");
		this.infoDialog = $("#infoDialog");
		this.render();
		
		this.model.fetch({
			success: function(_model, _response){
				_this.calendarObject.fullCalendar("refetchEvents");
			}
		});
	},

	render: function(){
		var _this = this;

		this.infoIcon.on("mouseenter", function () {
			_this.infoDialog.fadeTo(150, 1);
		});
		
		this.infoIcon.on("mouseleave", function () {
			_this.infoDialog.fadeTo(150, 0);
		});
		
		this.calendarObject.fullCalendar({
			dayNamesShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
			defaultView: "agendaWeek",
			columnFormat: "ddd",
			timeFormat: "h:mm TT{ - h:mm TT}",
			axisFormat: "h(:mm) TT",
			titleFormat: {
				month: "MMMM yyyy",
				week: "MMM d[, yyyy]{ -[ MMM] d, yyyy}",
				day: "dddd, MMM d, yyyy"
			},
			selectable: true,
			selectHelper: true,
			height: window.innerHeight - 200,
			header: {
				left: "prev",
				center: "title",
				right: "next"
			},
			events: $.proxy(_this.model.getTimeBlocks, _this.model),
			firstDay: 1,
			eventClick: function(_event){
				if (_event.layer)
					return true;
				
				if (_this.model.removeTimeBlock(_event))
					_this.calendarObject.fullCalendar("refetchEvents");
				
				return false;
			},
			select: function(start, end){
				var allDay = (start.getTime() === end.getTime() ? true : false);
				var event = {
					title: "",
					start: start,
					end: end,
					allDay: allDay,
					layer: 0
				};
				
				_this.model.addTimeBlock(event);
				_this.calendarObject.fullCalendar("refetchEvents");
				_this.calendarObject.fullCalendar("unselect");
			}
		});
		
		return this;
	},
	
	plan: function(_event){
		_event.preventDefault();
		var _this = this;
		this.model.save(this.model.toJSON(), {
			success: function(){
				_this.calendarObject.fullCalendar("refetchEvents");
				alert("You have successfully selected your time.");
			},
			error: function(){
				alert("Oops, there appears to be a server error.");
			}
		});
	}
});