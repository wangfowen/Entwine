Entwine.widgets.views.Calendar = Backbone.View.extend({
  "defaults": {
    "model": new Entwine.scheduler.collections.Participations()
  },
  
  "initialize": function (aOpts) {
    var opts = _.defaults(aOpts, this.defaults);
    var self = this;
    
    this.model = opts["model"];
    
    this.$el.fullCalendar({
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
      events: getTimeBlocks: function (_start, _end, _callback) {
        var a = this.get("localTimeBlocks");
        var b = this.get("foreignTimeBlocks");
        _callback(a.concat.apply(a, b));
      },
      firstDay: 0,
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
      success: function (aModel, aResponse) {
        self.$el.fullCalendar("refetchEvents");
      }
    });
  }
});