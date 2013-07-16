# This is the entry point for the entire app.
"use strict"

requirejs.config
	baseUrl: "/assets/javascripts"
	paths:
		'jquery': 'jquery'
		'underscore': 'libs/underscore-min'
		'backbone': 'libs/backbone-min',
		'create': 'libs/create'
		'foundation': 'foundation'
		'dataTables': 'libs/jquery.dataTables'
	shim:
		'backbone':
			deps: ['underscore', 'jquery']
			exports: 'Backbone'
		'create':
			deps: ['jquery', 'underscore', 'backbone', 'libs/vie-2.1.0', 'libs/hallo']
		'foundation/app':
			deps: ['foundation/modernizr.foundation', 'foundation/jquery.foundation.topbar', 'foundation/jquery.foundation.navigation', 'foundation/jquery.foundation.forms', 'foundation/jquery.foundation.buttons', 'foundation/jquery.foundation.mediaQueryToggle', 'foundation/responsive-tables', 'libs/jquery.dataTables']
		'libs/jquery.dataTables':
			deps: ['jquery']
require ['app', 'foundation/app'], (App, foundation) ->
	App.initialize()