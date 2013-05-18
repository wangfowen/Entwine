Entwine.views.LoginView = Backbone.View.extend({
	loginForm: null,
	signupButton: null,
	loginButton: null,
	backButton: null,
	submitButton: null,
	
	confirmPasswordContainer: null,
	signupButtonContainer: null,
	loginButtonContainer: null,
	
	emailInput: null,
	passwordInput: null,
	confirmPasswordInput: null,
	
	validationParams: {
		rules: {},
		messages: {},
		errorClass: "text-error",
		errorPlacement: function (_error, _element) {
			_error.insertBefore(_element);
		}
	},
	
	initialize: function () {
		this.loginForm = $("#loginForm");
		this.signupButton = $("#signupButton");
		this.loginButton = $("#loginButton");
		this.backButton = $("#backButton");
		this.submitButton = $("#submitButton");
		
		this.confirmPasswordContainer = $("#confirmPasswordContainer");
		this.signupButtonContainer = $("#signupButtonContainer");
		this.loginButtonContainer = $("#loginButtonContainer");

		this.emailInput = $("#email");
		this.passwordInput = $("#password");
		this.confirmPasswordInput = $("#confirmPassword");

		this.loginForm.validate(this.validationParams);
		this.setLoginValidation();
		
		var _this = this;
		
		this.loginForm.on({
			keypress: function (_event) {
				if (_event.keyCode == 13)
					_this.loginForm.submit();
			}
		})
		
		this.submitButton.on({
			click: function (_event) {
				_event.preventDefault();
				_this.loginForm.submit();
			}
		})
		
		this.signupButton.on({
			click: function (_event) {
				_event.preventDefault();
				_this.showSignup();
			}
		});
		
		this.loginButton.on({
			click: function (_event) {
				_event.preventDefault();
				_this.loginForm.submit();
			}
		});
		
		this.backButton.on({
			click: function (_event) {
				_event.preventDefault();
				_this.hideSignup();
			}
		});
	},
	
	setLoginValidation: function () {
		this.loginForm.attr("action", this.loginForm.attr("data-login"));
		
		this.emailInput.rules("add", {
			email: true,
			required: true,
			messages: {
				email: "please enter a valid email",
				required: "please enter your email"
			}
		});
		
		this.passwordInput.rules("add", {
			required: true,
			messages: {
				required: "please enter your password"
			}
		});
		
		this.passwordInput.rules("remove", "minlength");
		this.confirmPasswordInput.rules("remove");
	},
	
	setSignupValidation: function () {
		this.loginForm.attr("action", this.loginForm.attr("data-signup"));
		
		this.emailInput.rules("add", {
			email: true,
			required: true,
			messages: {
				email: "please enter a valid email",
				required: "please enter an email"
			}
		});
		
		this.passwordInput.rules("add", {
			required: true,
			minlength: 6,
			messages: {
				required: "please enter a password",
				minlength: "your password is too short"
			}
		});
		
		this.confirmPasswordInput.rules("add", {
			required: true,
			minlength: 6,
			equalTo: "#password",
			messages: {
				required: "please confirm your password",
				minlength: "your password is too short",
				equalTo: "your passwords don't match"
			}
		});
	},
	
	showSignup: function () {
		var _this = this;
		
		this.confirmPasswordContainer.fadeIn();
		this.loginButtonContainer.fadeOut(function () {
			_this.setSignupValidation();
			_this.signupButtonContainer.fadeIn();
		});
	},
	
	hideSignup: function () {
		var _this = this;
		
		this.confirmPasswordContainer.fadeOut();
		this.signupButtonContainer.fadeOut(function () {
			_this.setLoginValidation();
			_this.loginButtonContainer.fadeIn();
		})
	}
});