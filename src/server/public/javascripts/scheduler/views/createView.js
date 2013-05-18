Entwine.scheduler.views.CreateEventsView = Backbone.View.extend({
	inviteeField: null,
	cutoffDatePicker: null,
	submitButton: null,
	mainForm: null,

	initialize: function(){
		var _this = this;

		this.inviteeField = $("#invitees");
		this.cutoffDatePicker = $("#cutoff");
		this.submitButton = $("#submitButton");
		this.mainForm = $("#mainForm");

		this.render();
	},

	render: function(){
		this.inviteeField.tagsInput({
			width: "",
			minInputWidth: "100%",
			placeholderColor: "",
			delimiter: ", ",
			defaultText: "Add emails, separated by commas",
			containerClass: "form-input field-textarea"
		});
		
		this.cutoffDatePicker.datepicker({
			minDate: new Date(),
			dateFormat: "dd/mm/yy"
		});
		
		this.cutoffDatePicker.datepicker("setDate", new Date());

		var _this = this;
		this.mainForm.validate({
			rules: {
				name: "required",
				cutoff: "required"
			},
			messages: {
				name: "this field is required",
				cutoff: "this field is required"
			},
			submitHandler: function(_form){
				_form["cutoff"].value = Utility.dateStrToUTC(_form["cutoff"].value);
				_form.submit();
			},
			errorClass: "text-error",
			errorPlacement: function(_error, _element){
				_error.insertBefore(_element);
			}
		});

		this.submitButton.bind("click", function(_event){
			_event.preventDefault();
			_this.mainForm.submit();
		});
		
		return this;
	}
});
