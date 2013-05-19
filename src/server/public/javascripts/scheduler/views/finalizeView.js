Entwine.scheduler.views.EventsFinalizeView = Backbone.View.extend({
  events: {
    "click #finalizeButton": "finalize"
  },

  calendarObject: null,

  initialize: function () {
    var _this = this;

    this.calendarObject = $("#calendarContainer");


    
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
