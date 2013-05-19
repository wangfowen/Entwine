Entwine.scheduler.views.widgets.TimeslotList = Backbone.View.extend({
  "defaults": {
    "model": new Entwine.scheduler.collections.CommonTimeblocks(),
    "listitem": $.trim($("#TLITimeslot").html())
  },
  
  "initialize": function (aOpts) {
    var opts = _.defaults(aOpts, this.defaults);
    var self = this;
    
    this.model = opts["model"];
    
    var $list = self.$el.find("#timeslotList");
    var $selbox = self.$el.find("#selectionBox");
    
    $selbox.hide();
    
    this.model.on("reset", {
      success: function (aModel, aResponse) {
        aModel.forEach(function (aElem) {
          $list.append(_.template(opts.listitem, {
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