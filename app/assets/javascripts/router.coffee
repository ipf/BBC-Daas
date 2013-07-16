define ['backbone', 'views/Tag'], (Backbone, TagListView) ->

	AppRouter = Backbone.Router.extend
		routes:
			"*actions": "defaultRoute"

	initialize = ->
		appRouter = new AppRouter
		appRouter.on 'route:defaultRoute', (action) ->
			# automagically determine action to be called
			model = getViewFromAction action
			listView = new TagListView()

		Backbone.history.start()
		appRouter.navigate()

	getViewFromAction = (action) ->
		dashPosition = action.indexOf('-')
		dashCamel = action.replace('-' + action.charAt(dashPosition + 1), action.charAt(dashPosition + 1).toUpperCase())
		dashCamel = dashCamel.charAt(0).toUpperCase() + dashCamel.slice(1) + "View"

	initialize: initialize