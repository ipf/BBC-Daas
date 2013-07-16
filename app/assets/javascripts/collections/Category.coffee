define ['backbone', 'models/Category'], (Backbone, Category) ->

	CategoryCollection = Backbone.Collection.extend
		model: Category
		url: "/api/categories"
	CategoryCollection