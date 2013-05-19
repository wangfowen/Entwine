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
    this.infoDialog = $("#infoDialog");

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
    this.eventModel.fetch();
    this.timeblockModel.fetch();
  },

  plan: function (aEvent) {
    aEvent.preventDefault();
    this.calendarObject.save(function () {
      alert("You have successfully selected your time.");
      window.href = "/scheduler/dashboard";
    }, function () {
      alert("Oops, there appears to be a server error.");
    }, {
      "eventId": this.eventId,
      "userId": this.userId
    });
  }
});
