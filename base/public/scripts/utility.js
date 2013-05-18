var Utility = {
	dateStrToUTC: function (_value) {
		var dateArr = _value.split("/");
		return Date.UTC(dateArr[2], dateArr[1], dateArr[0]);
	}
};

$.fn.serializeJSON = function () {
	var json = {};
	
	$.map($(this).serializeArray(), function (key, value) {
		json[key["name"]] = key["value"];
	});
	
	return json;
};
