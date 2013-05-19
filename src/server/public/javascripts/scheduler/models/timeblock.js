Entwine.scheduler.models.Timeblock = Backbone.Model.extend({
  "defaults": {
    "startTime": undefined,
    "endTime": undefined,
    "participationId": undefined
  },
  
  "idAttribute": "participationId"
});

Entwine.scheduler.collections.Timeblocks = Backbone.Collection.extend({
  "model": Entwine.scheduler.models.Timeblock,
  "url": "/api/timeblocks"
    // var event = _event;
// 
    // var timeCompare = function (t1, t2) {
      // return ((t1 >= t2) ? true : false);
    // };
// 
    // for (var i = 0; i < timeBlocks.length; i++) {
      // if (timeBlocks[i].allDay) {
        // if (timeBlocks[i].start.getDate() === event.start.getDate())
          // return;
      // }
      // else {
        // var start1 = timeBlocks[i].start.getTime();
        // var start2 = event.start.getTime();
        // var end1 = timeBlocks[i].end.getTime();
        // var end2 = event.end.getTime(), newStart, newEnd;
//         
        // var overlap = true;
// 
        // if (timeCompare(start2, start1) && timeCompare(end1, end2)) {
          // newStart = start1;
          // newEnd = end1;
        // }
        // else if (timeCompare(start2, start1) && timeCompare(end1, start2)) {
          // newStart = start1;
          // newEnd = end2;
        // }
        // else if (timeCompare(start1, start2) && timeCompare(end2, end1)) {
          // newStart = start2;
          // newEnd = end2;
        // }
        // else if (timeCompare(start1, start2) && timeCompare(end2, start1)) {
          // newStart = start2;
          // newEnd = end1;
        // }
        // else {
          // if (event.allDay && timeBlocks[i].start.getDate() === event.start.getDate()) {
            // newStart = start2;
            // newEnd = end2;
          // }
          // else
            // overlap = false;
        // }
//         
        // if (overlap) {
          // event = {
            // start: new Date(newStart),
            // end: new Date(newEnd),
            // title: "",
            // allDay: event.allDay,
            // layer: event.layer
          // };
          // this.removeTimeBlock(timeBlocks[i]);
          // i--;
        // }
      // }
    // };
//     
    // timeBlocks.push(event);
    // this.set("localTimeBlocks", timeBlocks);
});


// _.extend(Entwine.scheduler.collections.Timeblocks.prototype, {
//   
// });


// {
  // defaults: {
    // deletedTimeBlocks: [],
    // localTimeBlocks: [],
    // foreignTimeBlocks: [],
    // userId: -1,
    // eventId: -1
  // },
//   
  // getLocalTimeBlocks: function (_start, _end, _callback) {
    // _callback(this.get("localTimeBlocks"));
  // },
//   
  // /**
   // * Builds the url
   // */
  // url: function() {
    // var url = "/api/scheduler/participation";
    // if(this.isNew()){
      // var params = {
        // eventId: this.get("eventId"),
        // userId: this.get("userId"),
        // getOtherTimes: true
      // };
      // url += "?" + $.param(params);
    // }
//     
    // return url;
  // },
//   
  // /**
   // * Parse the response from requests
   // */
  // parse: function (_data, _xhr){
    // Backbone.Model.prototype.parse.call(this, _data);
//     
    // var cur = _data.currentPart;
    // var other = _data.otherPart;
    // var timeblocks = cur.timeBlocks;
    // var newTimeblocks = [];
//     
    // function createTB(_tb, _layer) {
      // var tb = {};
//       
      // tb.id = _tb.id;
      // tb._id = _tb.id;
      // tb.title = "";
      // tb.start = new Date(_tb.start);
      // tb.end = new Date(_tb.end);
      // tb.allDay = _tb.allDay;
      // tb.layer = _layer;
//       
      // return tb;
    // }
//     
    // for (var i = 0; i < timeblocks.length; i++)
      // newTimeblocks.push(createTB(timeblocks[i], 0));
// 
    // this.set("localTimeBlocks", newTimeblocks);
    // newTimeblocks = [];
//     
    // for (var i = 0; i < other.length; i++) {
      // var moreTimeblocks = [];
      // var timeblocks = other[i].timeBlocks;
      // for (var j = 0; j < timeblocks.length; j++)
        // moreTimeblocks.push(createTB(timeblocks[j], 1));
      // newTimeblocks.push(moreTimeblocks);
    // }
//     
    // this.set("deletedTimeBlocks", []);
    // this.set("foreignTimeBlocks", newTimeblocks);
  // },
//   
  // /**
   // * Generates post data
   // */
  // toJSON: function () {
    // var _this = this;
    // var timeblocks = this.get("localTimeBlocks");
    // var newTimeblocks = [];
//     
    // for (var i = 0; i < timeblocks.length; i++) {
      // newTimeblocks[i] = {};
      // newTimeblocks[i].id = timeblocks[i].id;
      // newTimeblocks[i].start = timeblocks[i].start.getTime();
      // if (timeblocks[i].allDay)
        // newTimeblocks[i].end = -1;
      // else
        // newTimeblocks[i].end = timeblocks[i].end.getTime();
      // newTimeblocks[i].allDay = timeblocks[i].allDay;
//       
      // if(newTimeblocks[i].id === null || newTimeblocks[i].id === undefined)
        // newTimeblocks[i].id = -1;
    // }
//     
    // return {
      // userId: this.get("userId"),
      // eventId: this.get("eventId"),
      // timeBlocks: newTimeblocks,
      // deletedTimeBlocks: this.get("deletedTimeBlocks"),
      // getOtherTimes: true
    // }
  // },
//   
  // removeTimeBlock: function (_event) {
    // var timeblocks = this.get("localTimeBlocks");
    // var found = false;
//     
    // for (var i = 0; i < timeblocks.length; i++) {
      // if (timeblocks[i]._id === _event._id) {
        // if (_event.id !== null && _event.id !== undefined)
          // this.get("deletedTimeBlocks").push(_event.id);
        // timeblocks.splice(i, 1);
        // found = true;
        // break;
      // }
    // }
//     
    // this.set("localTimeBlocks", timeblocks);
    // return found;
  // },
//   
  // addTimeBlock: function (_event) {
    // var event = _event;
    // var timeBlocks = this.get("localTimeBlocks");
// 
    // var timeCompare = function(t1, t2){
      // return ((t1 >= t2) ? true : false);
    // };
// 
    // for (var i = 0; i < timeBlocks.length; i++) {
      // if (timeBlocks[i].allDay) {
        // if (timeBlocks[i].start.getDate() === event.start.getDate())
          // return;
      // }
      // else {
        // var start1 = timeBlocks[i].start.getTime();
        // var start2 = event.start.getTime();
        // var end1 = timeBlocks[i].end.getTime();
        // var end2 = event.end.getTime(), newStart, newEnd;
//         
        // var overlap = true;
// 
        // if (timeCompare(start2, start1) && timeCompare(end1, end2)) {
          // newStart = start1;
          // newEnd = end1;
        // }
        // else if (timeCompare(start2, start1) && timeCompare(end1, start2)) {
          // newStart = start1;
          // newEnd = end2;
        // }
        // else if (timeCompare(start1, start2) && timeCompare(end2, end1)) {
          // newStart = start2;
          // newEnd = end2;
        // }
        // else if (timeCompare(start1, start2) && timeCompare(end2, start1)) {
          // newStart = start2;
          // newEnd = end1;
        // }
        // else {
          // if (event.allDay && timeBlocks[i].start.getDate() === event.start.getDate()) {
            // newStart = start2;
            // newEnd = end2;
          // }
          // else
            // overlap = false;
        // }
//         
        // if (overlap) {
          // event = {
            // start: new Date(newStart),
            // end: new Date(newEnd),
            // title: "",
            // allDay: event.allDay,
            // layer: event.layer
          // };
          // this.removeTimeBlock(timeBlocks[i]);
          // i--;
        // }
      // }
    // };
//     
    // timeBlocks.push(event);
    // this.set("localTimeBlocks", timeBlocks);
  // }
// });