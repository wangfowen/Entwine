Entwine.scheduler.views.widgets.Calendar = Backbone.View.extend({
  "defaults": {
    "model": new Entwine.scheduler.collections.Participants()
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
      "events": function (aStart, aEnd, aCallback) {
        aCallback(self.model.map(function (a) { return a; }));
      },
      "firstDay": 0,
      "eventClick": function (aEvent) {
        if (aEvent.layer)
          return true;

        if (self.model.removeTimeBlock(aEvent))
          self.calendarObject.fullCalendar("refetchEvents");

        return false;
      },
      "select": function (start, end) {
        var allDay = (start.getTime() === end.getTime() ? true : false);
        var event = {
          title: "",
          start: start,
          end: end,
          allDay: allDay,
          layer: 0
        };
        self.model.addTimeBlock(event);
        self.calendarObject.fullCalendar("refetchEvents");
        self.calendarObject.fullCalendar("unselect");
      }
    });
    
    this.model.on("reset", {
      success: function (aModel, aResponse) {
        self.$el.fullCalendar("refetchEvents");
      }
    });
  },
  
  "save": function (aOnSuccess, aOnFailure) {
    var self = this;
    
    this.model.save(this.model.toJSON(), {
      "success": function () {
        self.calendarObject.fullCalendar("refetchEvents");
        aOnSuccess();
      },
      "error": function () {
        aOnFailure();
      }
    });
  }
});
