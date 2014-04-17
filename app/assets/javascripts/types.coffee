require(["webjars!knockout.js", 'webjars!jquery.js', "/routes.js", "webjars!bootstrap.js"], (ko) ->

  typesPerPage = 10

  # Models for the types page
  class TypesModel
    constructor: () ->
      self = @
      # the list of types
      @types = ko.observableArray()

      # the type fields
      @nameField = ko.observable()
      @descriptionField = ko.observable()
      @uriPatternField = ko.observable()
      @businessFunctionField = ko.observable()
      @subFunctionField = ko.observable()
      @systemField = ko.observable()
      @lobField = ko.observable()

      # save a new type
      @add = () ->
        @ajax(routes.controllers.TypeController.add(), {
          data: JSON.stringify({
            name: @nameField(),
            description: @descriptionField(),
            businessFunction: @businessFunctionField(),
            subFunction: @subFunctionField(),
            system: @systemField(),
            lob: @lobField(),
            uriPattern: @uriPatternField()
          })
          contentType: "application/json"
        }).done(() ->
          $("#addTypeModal").modal("hide")
          self.nameField(null)
          self.descriptionField(null)
          self.businessFunctionField(null)
          self.subFunctionField(null)
          self.systemField(null)
          self.lobField(null)
          self.uriPatternField(null)
        )

      # get the types
      @getAll = () ->
        @ajax(routes.controllers.TypeController.getAll(0, typesPerPage))
          .done((data, status, xhr) ->
            self.loadTypes(data, status, xhr)
          )

    # Convenience ajax request function
    ajax: (route, params) ->
      $.ajax($.extend(params, route))

    # Handle the types response
    loadTypes: (data, status, xhr) ->
      @types(data)

  # Setup
  model = new TypesModel
  ko.applyBindings(model)
  # Load types data
  model.getAll()

  # Server Sent Events handling
  events = new EventSource(routes.controllers.MainController.events().url)
  events.addEventListener("type", (e) ->
    # Only add the data to the list if we're on the first page
    if model.prevTypesUrl() == null
      type = JSON.parse(e.data)
      model.types.unshift(type)
      # Keep types per page limit
      if model.types().length > typesPerPage
        model.types.pop()
  , false)
)
