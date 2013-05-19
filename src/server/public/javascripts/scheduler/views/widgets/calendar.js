Entwine.scheduler.views.widgets.Calendar = Backbone.View.extend({
  "defaults": {},

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
        var ret = _.filter(self.model.map(function (a) {
          return a.get("_metadata") || {
            "start": new Date(a.get("startTime")),
            "end": new Date(a.get("endTime")),
            "title": "",
            "allDay": false
          };
        }), function (a) {
          return a;
        });
        aCallback(ret || []);
      },
      "firstDay": 0,
      "eventClick": function (aEvent) {
        if (aEvent.layer)
          return true;


        self.model.remove(self.model.find(function (aElem) {
          return aElem.get("startTime") == aEvent["start"].getTime() &&
              aElem.get("endTime") == aEvent["end"].getTime();
        }));
        self.$el.fullCalendar("refetchEvents");

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

        self.model.add(new Entwine.scheduler.models.Timeblock({
          "startTime": event["start"].getTime(),
          "endTime": event["end"].getTime(),
          "_metadata": event
        }));

        self.$el.fullCalendar("refetchEvents");
        self.$el.fullCalendar("unselect");
      }
    });

    this.model.on("all", function (aModel, aResponse) {
        self.$el.fullCalendar("refetchEvents");
    });
  },

  "save": function (aOnSuccess, aOnFailure, aData, aUrl) {
    var self = this;

    var params = {
      "url": self.model.url,
      "type": "PUT",
      "contentType": "application/json",
      "dataType": "json",
      "data": JSON.stringify(_.extend(aData, {
        "timeBlocks": self.model.toJSON()
      }))
    };

    $.ajax(params).success(function () {
      self.$el.fullCalendar("refetchEvents");
      aOnSuccess();
    }).fail(function () {
      aOnFailure();
    });
  }
});
