Entwine.scheduler.views.EventsFinalizeView = Backbone.View.extend({
  events: {
    "click #finalizeButton": "finalize"
  },

  calendarObject: null,

  initialize: function (aOpts) {
    var self = this;
    var eventId = aOpts["eventId"];

    this.ctbModel = new Entwine.scheduler.collections.CommonTimeblocks({
      "url": Entwine.scheduler.collections.CommonTimeblocks.prototype.url + "/" + eventId
    });

    this.calendarObject = new Entwine.scheduler.views.widgets.Calendar({
      "el": $("#calendarContainer"),
      "model": this.ctbModel
    });
    this.timeblockList = new Entwine.scheduler.views.widgets.TimeslotList({
      "el": $("#timeslotListContainer"),
      "model": this.ctbModel
    });

    this.eventModel = new Entwine.scheduler.models.Event({
      "url": Entwine.scheduler.models.Event.prototype.url + "/" + eventId
    });

    //this.ctbModel.fetch();
    this.ctbModel.models[0].attributes.attendees = 2;
    this.ctbModel.models[0].attributes.startTime = 1368982667908;
    this.ctbModel.models[0].attributes.endTime = 1369022400000;
    console.log(this.ctbModel);
    this.eventModel.fetch(function (aModel) {
      self.$el.find("#eventTitle").text(aModel.get("event")["name"]);
      self.$el.find("#eventDescription").text(aModel.get("event")["description"]);
    });
  },

  finalize: function (aEvent) {
    aEvent.preventDefault();

    $.ajax({
      "url": "/api/selectTime",
      "type": "POST",
      "dataType": "application/json",
      "data": JSON.stringify({
        "startTime": 0,
        "endTime": 10
      })
    })
    var _this = this;
    this.ctbModel.save(this.model.toJSON(), {
      success: function () {
        _this.calendarObject.fullCalendar("refetchEvents");
        alertify.success("You have successfully selected your time.");
      },
      error: function () {
        alertify.error("Oops, there appears to be a server error.");
      }
    });
  }
});
