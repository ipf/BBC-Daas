define ['backbone', 'models/Tag'], (Backbone, Tag) ->

	TagCollection = Backbone.Collection.extend
		model: Tag
		url: "/api/tags"
	TagCollection