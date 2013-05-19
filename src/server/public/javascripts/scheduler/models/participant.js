Entwine.scheduler.models.Participant = Backbone.Model.extend({
  "defaults": {
    "participationId": undefined,
    "role": undefined,
    "email": undefined,
    "firstName": undefined,
    "lastName": undefined
  },
  
  "idAttribute": "participationId"
});

Entwine.scheduler.collections.Participants = Backbone.Collection.extend({
  "model": Entwine.scheduler.models.Participant
});
