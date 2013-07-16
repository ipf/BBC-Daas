define ['backbone', 'collections/Category', 'models/Category'], (Backbone, CategoryCollection, Category) ->

	CategoryListView = Backbone.View.extend
		initialize: ->
			@.render(this)

		el: $ '#list-categories'
		template: _.template($('#list-categories-template').html())

		render: (Obj) ->
			categoryCollection = new CategoryCollection()
			categoryCollection.fetch
				success: (data) ->
					Obj.$el.html(Obj.template(categories: data.models, title: "Available Categories"))

	TagListView