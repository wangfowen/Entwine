Entwine.account.views.SettingsView = Backbone.View.extend({
  keys: {
    changeEmail: "changeEmail",
    changePassword: "changePassword",
    connect: "connect",
    invite: "invite",
    feedback: "feedback"
  },
  links: {
    changeEmail: null,
    changePassword: null,
    connect: null,
    invite: null,
    feedback: null
  },
  // same schema as above
  dialogs: {},
  // same schema as above above
  forms: {},
  // same schema as above above above
  labels: {},
  submitButtons: {},
  validators: null,

  currentEmail: null,

  getValidators: function () {
    var _this = this;

    if (!this.validators) {
      /**
       * _params: {}
       *    key:
       *    url:
       *    form:
       *    successMsg:
       *    onSuccess:
       *    onError:
       */
      var submitHandler = function (_params) {
        var json = $(_params.form).serializeJSON();
        var label = _this.labels[_params.key];

        $.ajax({
          url: _params.url,
          type: "PUT",
          data: json,
          dataType: "json",
          success: function (_data) {
            if (_data.error) {
              label.html(_data.error);
              label.removeClass("text-success");
              label.addClass("text-error");

              if (_params.onError)
                _params.onError(_data)
            }
            else {
              label.html(_params.successMsg);
              label.removeClass("text-error");
              label.addClass("text-success");

              if (_params.onSuccess)
                _params.onSuccess(_data)
            }
            label.fadeIn();
            $.colorbox.resize();
          },
          error: function (_data) {
            console.log(_data);
          }
        });
      };

      this.validators = {
        changeEmail: {
          rules: {
            newEmail: {
              email: true,
              required: true
            },
            confirmNewEmail: {
              email: true,
              required: true,
              equalTo: "#newEmail"
            }
          },
          messages: {
            newEmail: {
              email: "please enter a valid email",
              required: "this field is required"
            },
            confirmNewEmail: {
              email: "please enter a valid email",
              required: "this field is required",
              equalTo: "your emails don't match"
            }
          },
          submitHandler: function (_form) {
            submitHandler({
              key: "changeEmail",
              url: "/api/account/email",
              successMsg: "Your email address has been changed successfully.",
              form: _form,
              onSuccess: function (_data) {
                _this.currentEmail.fadeOut(function () {
                  _this.currentEmail.html(_data.email);
                  _this.currentEmail.fadeIn();
                });
              }
            });
          },
          errorClass: "text-error",
          errorPlacement: function (_error, _element) {
            _error.insertBefore(_element);
          }
        },
        changePassword: {
          rules: {
            oldPassword: {
              required: true
            },
            newPassword: {
              required: true,
              minlength: 6
            },
            confirmNewPassword: {
              required: true,
              minlength: 6,
              equalTo: "#newPassword"
            }
          },
          messages: {
            oldPassword: {
              required: "this field is required"
            },
            newPassword: {
              required: "this field is required",
              minlength: "your password is too short"
            },
            confirmNewPassword: {
              required: "this field is required",
              minlength: "your password is too short",
              equalTo: "your passwords don't match"
            }
          },
          submitHandler: function (_form) {
            submitHandler({
              key: "changePassword",
              url: "/api/account/password",
              successMsg: "Your password has been changed successfully.",
              onSuccess: function (_data) {
                _data.email;
              }
            });
          },
          errorClass: "text-error",
          errorPlacement: function (_error, _element) {
            _error.insertBefore(_element);
          }
        },
        connect: {},
        invite: {},
        feedback: {}
      };
    }
    return this.validators;
  },

  initialize: function () {
    var _this = this;
    this.currentEmail = $("#currentEmail");

    var formHandler = function (_form) {
      _form.on({
        keypress: function (_event) {
          if (_event.keyCode == 13)
            _form.submit();
        }
      });
    };

    for (var key in this.keys) {
      if (this.keys.hasOwnProperty(key)) {
        var prop = this.keys[key];
        this.links[key] = $("#" + prop);
        this.dialogs[key] = $("#" + prop + "Content");
        this.forms[key] = $("#" + prop + "Form");
        this.submitButtons[key] = $("#" + prop + "Submit");
        this.labels[key] = $("#" + prop + "Label");

        formHandler(this.forms[key]);
      }
    }

    this.render();
  },

  render: function () {
    var _this = this;

    for (var key in this.keys) {
      if (this.keys.hasOwnProperty(key)) {
        (function () {
          var el = key;
          var link = _this.links[el];
          var form = _this.forms[el];
          var button = _this.submitButtons[el];

          link.colorbox({
            inline: true,
            href: _this.dialogs[el],
            opacity: 0.5,
            title: link.attr("data-title"),
            onComplete: function() {
              form.find(":input:visible:enabled:first").focus();
            },
            onCleanup: function () {
              var hideLabel = function (_label) {
                if (_label.is(":visible")) {
                  _label.fadeOut(function () {
                    _label.removeClass("text-error");
                    _label.removeClass("text-success");
                  });
                }
              };
              hideLabel(_this.labels[el]);
            }
          });

          form.validate(_this.getValidators()[el]);
          button.click(function (_event) {
            _event.preventDefault();
            form.submit();
          });
        })();
      }
    }

    $('#logout').click(function(e) {
      e.preventDefault();
      e.stopPropagation();

      $.ajax({
        url: '/api/logout',
        success: function() {
          window.location.href = "/";
        }
      });
    });

    return this;
  }
});
