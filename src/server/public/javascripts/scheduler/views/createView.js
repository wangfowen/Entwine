Entwine.scheduler.views.CreateEventsView = Backbone.View.extend({
  inviteeField: null,
  cutoffDatePicker: null,
  submitButton: null,
  mainForm: null,
  input: null,

  createInvitee: function() {
    var _this = this,
        email = _this.input.val(),
        invitee = "<p class='invitee'>" +
            "<span class='email'>" + email.substring(0, email.length -1) + "</span>" +
            "<span class='hover-buttons'>" +
            "<label class='important'>" +
              "<input type='checkbox' value='1'>" +
              "<span class='star'>★</span>" +
            "</label>" +
            "<a href='#' class='close'>✖</a>" +
            "</span>" +
          "</p>";
    _this.inviteeField.append(invitee);

    var $invitee = _this.inviteeField.children().last(),
        $hoverButtons = $invitee.children('.hover-buttons'),
        $close = $hoverButtons.children('.close'),
        $important = $hoverButtons.children('.important'),
        green = "#14BC68",
        hovered = "#dedede",
        selected = "#FFC87C",
        darkGreen = "#7AA129",
        darkSelected = "#F79E60",
        darkHovered = "#ccc";

    $invitee.hover(function(e) {
      $invitee.css("background", hovered);
      $invitee.css("border-color", darkHovered);
      $hoverButtons.fadeIn("fast");
    }, function(e) {
      if (!$important.children('input').prop("checked")) {
        $invitee.css("background", green);
        $invitee.css("border-color", darkGreen);
      } else {
        $invitee.css("background", selected);
        $invitee.css("border-color", darkSelected);
      }
      $hoverButtons.hide();
    });

    $close.click(function(e) {
      e.preventDefault();
      e.stopPropagation();

      $invitee.remove();
    });
  },

  initialize: function(){
    var _this = this;

    this.inviteeField = $("#invitees");
    this.cutoffDatePicker = $("#cutoff");
    this.submitButton = $("#submitButton");
    this.mainForm = $("#mainForm");
    this.input = $('#input-invitee');

    this.render();
  },

  render: function(){
    var _this = this;

    _this.input.keyup(function(e) {
      if (e.keyCode === 188 || e.keyCode === 32) {
        if (_this.input.val().length > 1) {
          _this.createInvitee();
        }

        _this.input.val('');
      }
    });

    _this.input.keydown(function(e) {
      if (e.keyCode === 8 && _this.input.val() === "") {
        $('.invitee').last().remove();
      }
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
