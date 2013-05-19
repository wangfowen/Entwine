Entwine.scheduler.views.EventsSelectView = Backbone.View.extend({
  events: {
    "click #submitButton": "plan"
  },

  calendarObject: null,
  infoIcon: null,
  infoDialog: null,

  initialize: function(){
    var _this = this;

    this.infoIcon = $("#infoIcon");
    this.infoDialog = $("#infoDialog");
    this.render();

    this.infoIcon.on("mouseenter", function () {
      _this.infoDialog.fadeTo(150, 1);
    });

    this.infoIcon.on("mouseleave", function () {
      _this.infoDialog.fadeTo(150, 0);
    });
    
    this.calendarObject = new Entwine.widgets.views.Calendar({
      "el": $("#calendarContainer")
    });

    return this;
  },

  plan: function(_event){
    _event.preventDefault();
    this.calendarObject.save();
  }
});
