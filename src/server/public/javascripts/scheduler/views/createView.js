Entwine.scheduler.views.CreateEventsView = Backbone.View.extend({
  inviteeField: null,
  cutoffDatePicker: null,
  submitButton: null,
  mainForm: null,
  input: null,

  events: {
    "click #submitButton": "submit"
  },

  createInvitee: function(email) {
    var _this = this,
        invitee = "<p class='invitee'>" +
            "<span class='email'>" + email + "</span>" +
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
          //strip out the space / comma
          var email = _this.input.val();
          _this.createInvitee(email.substring(0, email.length -1));
        }

        _this.input.val('');
      }
    });

    _this.input.keydown(function(e) {
      if (e.keyCode === 8 && _this.input.val() === "") {
        $('.invitee').last().remove();
      }

      if (e.keyCode === 9 && _this.input.val().length > 0) {
        _this.createInvitee(_this.input.val());
        _this.input.val('');
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
      errorClass: "text-error",
      errorPlacement: function(_error, _element){
        _error.insertBefore(_element);
      }
    });

    return this;
  },

  submit: function(e) {
    e.preventDefault();
    e.stopPropagation();

    var name = $('#eventName').val(),
        loc = $('#location').val(),
        description = $('#description').val(),
        cutoff = Utility.dateStrToUTC($('#cutoff').val()),
        invitees = _.map($('.invitee'), function(invitee) {
          var role = $(invitee).find('.important input').prop('checked') ? 1 : 0;
          return { email: $(invitee).children('.email').html(),
                   role: role };
        });

    var data = {
          name: name,
          description: description,
          location: loc,
          participants: invitees
      };

    $.ajax({
      type: "POST",
      url: '/api/event',
      data: JSON.stringify(data),
      success: function(data) {
          console.log(data);
      },
      dataType: "json",
      contentType: "application/json"
    });
  }
});
