/*

jQuery Tags Input Plugin 1.3.3
Copyright (c) 2011 XOXCO, Inc
Documentation for this plugin lives here:
http://xoxco.com/clickable/jquery-tags-input
Licensed under the MIT license:
http://www.opensource.org/licenses/mit-license.php

ben@xoxco.com

 */

(function($) {

	var delimiter = new Array();
	var tagsCallbacks = new Array();
	
	var saveTagsList = function (elem, tagslist) {
		var str = [];
		
		for (var i = 0; i < tagslist.length; i++)
			str[i] = tagslist[i].value;
		
		elem.val(JSON.stringify(str));
		elem.data("tagslist", tagslist);
	};
	
	$.fn.doAutosize = function(o){
		var minWidth = $(this).data('minwidth');
		var maxWidth = $(this).data('maxwidth');
		var val = '';
		var input = $(this);
		var testSubject = $('#'+$(this).data('tester_id'));

		if (val === (val = input.val())) {return;}

//		Enter new content into testSubject
		var escaped = val.replace(/&/g, '&amp;').replace(/\s/g,' ').replace(/</g, '&lt;').replace(/>/g, '&gt;');
		testSubject.html(escaped);
//		Calculate new width + whether to change
		var testerWidth = testSubject.width(),
		newWidth = (testerWidth + o.comfortZone) >= minWidth ? testerWidth + o.comfortZone : minWidth,
				currentWidth = input.width(),
				isValidWidthChange = (newWidth < currentWidth && newWidth >= minWidth)
				|| (newWidth > minWidth && newWidth < maxWidth);

//		Animate width
		if (isValidWidthChange) {
			input.width(newWidth);
		}
	};
	
	$.fn.resetAutosize = function(options){
		// alert(JSON.stringify(options));
		var minWidth = $(this).data('minwidth') || options.minInputWidth || $(this).width(),
		maxWidth = $(this).data('maxwidth') || options.maxInputWidth || ($(this).closest('.tagsinput').width() - options.inputPadding),
		val = '',
		input = $(this),
		testSubject = $('<tester/>').css({
			position: 'absolute',
			top: -9999,
			left: -9999,
			width: 'auto',
			fontSize: input.css('fontSize'),
			fontFamily: input.css('fontFamily'),
			fontWeight: input.css('fontWeight'),
			letterSpacing: input.css('letterSpacing'),
			whiteSpace: 'nowrap'
		}),
		testerId = $(this).attr('id')+'_autosize_tester';
		if(! $('#'+testerId).length > 0){
			testSubject.attr('id', testerId);
			testSubject.appendTo('body');
		}

		input.data('minwidth', minWidth);
		input.data('maxwidth', maxWidth);
		input.data('tester_id', testerId);
		input.css('width', minWidth);
	};

	/**
	 * Creates a new tag from input
	 */
	$.fn.addTag = function (value, options) {
		options = jQuery.extend({
			focus: false,
			callback: true
		}, options);
		
		this.each(function () {
			var $this = $(this);
			var id = $this.attr('id');
			var counter = $this.data("counter");
			var tagslist = $this.data("tagslist");
			var skipTag = false;
			
			if (!counter)
				counter = 1;
			if (!tagslist)
				tagslist = [];

			value = $.trim(value);
			
			if (value != '') {
				if (options.unique) {
					for (var i = 0; i < tagslist.length; i++) {
						if (tagslist[i].value === value) {
							skipTag = true;
							break;
						}
					}
					
					if (skipTag) {
	//					Marks fake input as not_valid to let styling it
						$('#' + id + '_tag').addClass('not_valid');
					}
				}
	
				if (!skipTag) {
					var tagElem = $('<span>').addClass('tag').append(
							$('<span>').text(value).append('&nbsp;&nbsp;'),
							$('<a>', {
								href: '#',
								title: 'Removing tag',
								text: 'x',
								"data-index": counter
							}).click(function () {
								return $('#' + id).removeTag(parseInt($(this).attr("data-index")));
							})
					).insertBefore('#' + id + '_addTag');
	
					tagslist.push({
						index: counter,
						value: value,
						tagElem: tagElem
					});

					$('#' + id + '_tag').val('');
					if (options.focus) {
						$('#' + id + '_tag').focus();
					} else {
	//					$('#'+id+'_tag').blur();
					}

					saveTagsList($this, tagslist);
					$this.data("counter", ++counter);

					if (options.callback && tagsCallbacks[id] && tagsCallbacks[id]['onAddTag']) {
						var f = tagsCallbacks[id]['onAddTag'];
						f.call(this, value);
					}
					if(tagsCallbacks[id] && tagsCallbacks[id]['onChange'])
					{
						var i = tagslist.length;
						var f = tagsCallbacks[id]['onChange'];
						f.call(this, $(this), tagslist[i-1]);
					}	
				}
			}
		});	

		return false;
	};

	/**
	 * Removes the current selected tag
	 */
	$.fn.removeTag = function (index) {
		this.each(function () {
			var id = $(this).attr('id');
			var old = $(this).data("tagslist");
			var entry = null;
			
			if (index == -1) {
				entry = old[old.length - 1];
				old.splice(old.length - 1, 1);
			}
			else {
				for (var i = 0; i < old.length; i++) {
					if (old[i].index === index) {
						entry = old[i];
						old.splice(i, 1);
						break;
					}
				}
			}
			
			if (entry) {
				entry.tagElem.remove();
				saveTagsList($(this), old);

				if (tagsCallbacks[id] && tagsCallbacks[id]['onRemoveTag']) {
					var f = tagsCallbacks[id]['onRemoveTag'];
					f.call(this, entry.value);
				}
			}
		});

		return false;
	};

	/**
	 * Initializes the tag input class
	 */
	$.fn.tagsInput = function(options) {
		var settings = jQuery.extend({
			interactive: true,
			defaultText: 'add a tag',
			minChars: 0,
			width: '300px',
			height: '100px',
			autocomplete: {selectFirst: false },
			hide: true,
			delimiter: ',',
			unique: true,						// are entries unique?
			removeWithBackspace: true,
			placeholderColor: '#666666',
			autosize: true,
			comfortZone: 20,
			inputPadding: 6*2,
			containerClass: ''
		}, options);

		this.each(function() {
			if (settings.hide)
				$(this).hide();
			var id = $(this).attr('id');
			if (!id || delimiter[$(this).attr('id')])
				id = $(this).attr('id', 'tags' + new Date().getTime()).attr('id');

			var data = jQuery.extend({
				pid:id,
				realInput: id,
				holder: id+'_tagsinput',
				inputWrapper: id+'_addTag',
				fakeInput: id+'_tag'
			}, settings);

			delimiter[id] = data.delimiter;

			if (settings.onAddTag || settings.onRemoveTag || settings.onChange) {
				tagsCallbacks[id] = new Array();
				tagsCallbacks[id]['onAddTag'] = settings.onAddTag;
				tagsCallbacks[id]['onRemoveTag'] = settings.onRemoveTag;
				tagsCallbacks[id]['onChange'] = settings.onChange;
			}

			var markup = '<div id="' + data.holder + '" class="tagsinput ' + settings.containerClass + '"><div id="'+id+'_addTag">';

			if (settings.interactive)
				markup = markup + '<input id="' + data.fakeInput + '" value="" data-default="'+settings.defaultText+'" />';

			markup = markup + '</div><div class="tags_clear"></div></div>';

			$(markup).insertAfter(this);

			$("#" + data.holder).css('width',settings.width);
			$("#" + data.holder).css('min-height',settings.height);
			$("#" + data.holder).css('height','100%');

			if ($("#" + data.realInput).val() != '')
				$.fn.tagsInput.importTags($("#" + data.realInput),$("#" + data.realInput).val());
			if (settings.interactive) {
				$("#" + data.fakeInput).val($("#" + data.fakeInput).attr('data-default'));
				$("#" + data.fakeInput).css('color',settings.placeholderColor);
				$("#" + data.fakeInput).resetAutosize(settings);

				$("#" + data.holder).bind('click',data,function(event) {
					$("#" + event.data.fakeInput).focus();
				});

				$("#" + data.fakeInput).bind('focus',data, function(event) {
					if ($("#" + event.data.fakeInput).val()==$("#" + event.data.fakeInput).attr('data-default')) {
						$("#" + event.data.fakeInput).val('');
					}
//					$("#" + event.data.fakeInput).css('color','#000000');	
				});

				if (settings.autocomplete_url != undefined) {
					autocomplete_options = {source: settings.autocomplete_url};
					for (attrname in settings.autocomplete) {
						autocomplete_options[attrname] = settings.autocomplete[attrname];
					}

					if (jQuery.Autocompleter !== undefined) {
						$("#" + data.fakeInput).autocomplete(settings.autocomplete_url, settings.autocomplete);
						$("#" + data.fakeInput).bind('result',data,function(event,data,formatted) {
							if (data) {
								$('#'+id).addTag(data[0] + "",{focus:true,unique:(settings.unique)});
							}
						});
					} else if (jQuery.ui.autocomplete !== undefined) {
						$("#" + data.fakeInput).autocomplete(autocomplete_options);
						$("#" + data.fakeInput).bind('autocompleteselect',data,function(event,ui) {
							$("#" + event.data.realInput).addTag(ui.item.value,{focus:true,unique:(settings.unique)});
							return false;
						});
					}
				} else {
//					if a user tabs out of the field, create a new tag
//					this is only available if autocomplete is not used.
					$("#" + data.fakeInput).bind('blur',data,function(event) {
						var d = $(this).attr('data-default');
						if ($("#" + event.data.fakeInput).val()!='' && $("#" + event.data.fakeInput).val()!=d) {
							if( (event.data.minChars <= $("#" + event.data.fakeInput).val().length) && (!event.data.maxChars || (event.data.maxChars >= $("#" + event.data.fakeInput).val().length)) )
								$("#" + event.data.realInput).addTag($("#" + event.data.fakeInput).val(),{focus:true,unique:(settings.unique)});
						} else {
							$("#" + event.data.fakeInput).val($("#" + event.data.fakeInput).attr('data-default'));
							$("#" + event.data.fakeInput).css('color',settings.placeholderColor);
						}
						return false;
					});
				}
//				if user types a comma, create a new tag
				$("#" + data.fakeInput).bind('keypress',data, function(event) {
					if (event.data.delimiter.indexOf(String.fromCharCode(event.which)) != -1 || event.which == 13 ) {
						event.preventDefault();
						if ((event.data.minChars <= $("#" + event.data.fakeInput).val().length) && (!event.data.maxChars || (event.data.maxChars >= $("#" + event.data.fakeInput).val().length)) )
							$("#" + event.data.realInput).addTag($("#" + event.data.fakeInput).val(),
									{ focus:true, unique: (settings.unique) });
						$("#" + event.data.fakeInput).resetAutosize(settings);
						return false;
					} else if (event.data.autosize) {
						$("#" + event.data.fakeInput).doAutosize(settings);

					}
				});
//				Delete last tag on backspace
				data.removeWithBackspace && $("#" + data.fakeInput).bind('keydown', function(event) {
					if (event.keyCode == 8 && $(this).val() == '') {
						event.preventDefault();
						var last_tag = $(this).closest('.tagsinput').find('.tag:last').text();
						var id = $(this).attr('id').replace(/_tag$/, '');
						last_tag = last_tag.replace(/[\s]+x$/, '');
						$('#' + id).removeTag(-1);
						$(this).trigger('focus');
					}});
				$("#" + data.fakeInput).blur();

//				Removes the not_valid class when user changes the value of the fake input
				if (data.unique) {
					$("#" + data.fakeInput).keydown(function(event){
						if(event.keyCode == 8 || String.fromCharCode(event.which).match(/\w+|[·ÈÌÛ˙¡…Õ”⁄Ò—,/]+/)) {
							$(this).removeClass('not_valid');
						}
					});
				}
			} // if settings.interactive
		});

		return this;
	};

	// clear all existing tags and import new ones from a string
	$.fn.importTags = function (str) {
		id = $(this).attr('id');
		$('#' + id + '_tagsinput .tag').remove();
		$.fn.tagsInput.importTags(this, str);
	}

	$.fn.tagsInput.importTags = function (obj, val) {
		if (!val)
			val = '[]';
		var id = $(obj).attr('id');
		var tags = JSON.parse(val);
		
		for (i = 0; i < tags.length; i++) {
			$(obj).addTag(tags[i], { focus:false, callback:false });
		}
		if(tagsCallbacks[id] && tagsCallbacks[id]['onChange'])
		{
			var f = tagsCallbacks[id]['onChange'];
			f.call(obj, obj, tags[i]);
		}
	};

})(jQuery);