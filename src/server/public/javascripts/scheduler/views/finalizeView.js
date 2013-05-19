Entwine.scheduler.views.EventsFinalizeView = Backbone.View.extend({
  events: {
    "click #finalizeButton": "finalize"
  },

  calendarObject: null,

  initialize: function (aOpts) {
    var self = this;
    var eventId = aOpts["eventId"];

    this.ctbModel = new Entwine.scheduler.models.CommonTimeblocks({
      "url": Entwine.scheduler.models.CommonTimeblocks.prototype.url + "/" + eventId
    });
    
    this.calendarObject = Entwine.widgets.views.Calendar({
      "el": $("#calendarContainer"),
      "model": this.ctbModel
    });
    
    this.eventModel = new Entwine.scheduler.models.Event({
      "url": Entwine.scheduler.models.Event.prototype.url = "/" + eventId
    });
    
    this.ctbModel.fetch();
    this.eventModel.fetch(function (aModel) {
      self.$el.find("#eventTitle").text(aModel.get("event")["name"]);
      self.$el.find("#eventDescription").text(aModel.get("event")["description"]);
    });
  },

  finalize: function (aEvent) {
    aEvent.preventDefault();
    
    $.ajax({
      "url": "/api/selectTime",
      "method": "POST",
      "dataType": "application/json",
      "data": {
        "startTime"
      }
    })
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
