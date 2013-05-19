Entwine.scheduler.models.Event = Backbone.Model.extend({
  "defaults": {
    "event": {
      "eventId": undefined,
      "name": undefined,
      "description": undefined,
      "location": undefined,
      "ownerId": undefined,
      "createdDate": undefined
    },
    "participants": Entwine.scheduler.models.Participants
  },
  
  "url": "api/event",
  "idAttribute": "eventId",
  
  "parse": function(aResponse, aOptions) {
    var ret = aResponse;
    ret["participants"] = new Entwine.scheduler.models.Participants(aResponse["participants"], { "parse": true });
    return ret;
  }
});

Entwine.scheduler.collections.Events = Backbone.Collection.extend({
  "model": Entwine.scheduler.models.Event,
  "url": "/api/events"
});
