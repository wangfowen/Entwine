Entwine.scheduler.views.widgets.TimeslotList = Backbone.View.extend({
  "defaults": {
    "model": new Entwine.scheduler.collections.CommonTimeslots(),
    "listitem": $.trim($("#TLITimeslot").html())
  },
  
  "initialize": function (aOpts) {
    var opts = _.defaults(aOpts, this.defaults);
    var self = this;
    
    this.model = opts["model"];
    
    this.model.on("reset", {
      success: function (aModel, aResponse) {
        aModel.forEach(function (aElem) {
          self.$el.append(_.template(opts.listitem, {
            "date": aElem.get("startTime"),
            "startTime": aElem.get("startTime"),
            "endTime": aElem.get("endTime"),
            "numNormal": aElem.get("attendees").filter(function (aM) { return aM.get("role") == 0; }).length(),
            "numImportant": aElem.get("attendees").filter(function (aM) { return aM.get("role") == 1; }).length()
          }))
        });
      }
    });
  },
  
  "getSelection": function () {
    var self = this;
  }
});