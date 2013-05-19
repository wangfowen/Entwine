Entwine.scheduler.models.CommonTimeblock = Backbone.Model.extend({
  "defaults": {
    "startTime": undefined,
    "endTime": undefined,
    "attendees": Entwine.scheduler.models.Participants
  },
  
  "idAttribute": "participationId",
  
  "parse": function(aResponse, aOptions) {
    var ret = aResponse;
    ret["attendees"] = new Entwine.scheduler.models.Participants(aResponse["attendees"], { "parse": true });
    return ret;
  }
});

Entwine.scheduler.collections.CommonTimeblocks = Backbone.Collection.extend({
  "model": Entwine.scheduler.models.CommonTimeblock,
  "url": "/api/commonTimeblocks"
});
