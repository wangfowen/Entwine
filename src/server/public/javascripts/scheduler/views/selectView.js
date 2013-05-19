Entwine.scheduler.views.EventsSelectView = Backbone.View.extend({
  events: {
    "click #submitButton": "plan"
  },

  calendarObject: null,
  infoIcon: null,
  infoDialog: null,

  initialize: function (aOpts) {
    var self = this;
    this.eventId = aOpts["eventId"];
    this.userId = aOpts["userId"];
    this.participationId = aOpts["participationId"];

    this.infoIcon = $("#infoIcon");
    this.infoDialog = $("#infoDialog").css("opacity", "0");

    this.infoIcon.on("mouseenter", function () {
      self.infoDialog.fadeTo(150, 1);
    });

    this.infoIcon.on("mouseleave", function () {
      self.infoDialog.fadeTo(150, 0);
    });

    this.eventModel = new (Entwine.scheduler.models.Event.extend({
      "url": Entwine.scheduler.models.Event.prototype.url + "/" + this.eventId
    }));
    this.timeblockModel = new (Entwine.scheduler.collections.Timeblocks.extend({
      "url": Entwine.scheduler.collections.Timeblocks.prototype.url + "/" + this.participationId
    }));

    this.calendarObject = new Entwine.scheduler.views.widgets.Calendar({
      "el": $("#calendarContainer"),
      "model": this.timeblockModel
    });

    $('.info-pane').css("position", "absolute");

    this.eventModel.fetch();
    this.timeblockModel.fetch();
  },

  plan: function (aEvent) {
    aEvent.preventDefault();
    this.calendarObject.save(function () {
      alertify.success("You have successfully selected your time.");
      window.href = "/scheduler/dashboard";
    }, function () {
      alertify.error("Oops, there appears to be a server error.");
    }, {
      "eventId": this.eventId,
      "userId": this.userId
    });
  }
});
