define ['backbone', 'collections/Tag', 'models/Tag'], (Backbone, TagCollection, Tag) ->

	TagListView = Backbone.View.extend
		initialize: ->
			@.render(this)

		el: $ '#list-tags'
		template: _.template($('#list-tags-template').html())

		render: (Obj) ->
			tagCollection = new TagCollection()
			tagCollection.fetch
				success: (data) ->
					Obj.$el.html(Obj.template(tags: data.models, title: "Available Tags"))

	TagListView