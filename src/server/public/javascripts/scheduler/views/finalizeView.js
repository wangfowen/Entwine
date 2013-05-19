Entwine.scheduler.views.EventsFinalizeView = Backbone.View.extend({
  events: {
    "click #finalizeButton": "finalize"
  },

  calendarObject: null,

  initialize: function () {
    var _this = this;

    this.calendarObject = $("#calendarContainer");

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
      eventClick: function (aEvent) {
        if (aEvent.layer)
          return true;

        if (_this.model.removeTimeBlock(aEvent))
          _this.calendarObject.fullCalendar("refetchEvents");

        return false;
      },
      select: function (start, end) {
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


    this.model.fetch({
      success: function (_model, _response) {
        _this.calendarObject.fullCalendar("refetchEvents");
      }
    });
  },

  finalize: function (aEvent) {
    aEvent.preventDefault();
    var _this = this;
    this.model.save(this.model.toJSON(), {
      success: function () {
        _this.calendarObject.fullCalendar("refetchEvents");
        alert("You have successfully selected your time.");
      },
      error: function () {
        alert("Oops, there appears to be a server error.");
      }
    });
  }
});
