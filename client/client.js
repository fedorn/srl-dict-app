Meteor.startup(function () {
  Deps.autorun(function () {
    if (! Session.get("type_name") || Types.find({name: Session.get("type_name")}).count() == 0) {
      var type = Types.findOne({}, {sort: {name: 1}});
      if (type)
        Router.setList(type.name);
    }
  });
});

Template.types.types = function () {
  return Types.find({}, {sort: {name: 1}});
};

Template.types.selected = function () {
  return Session.equals("type_name", this.name) ? ' selected' : ''
}

Template.types.args = function () {
  type = Types.findOne({name: Session.get("type_name")})
  if (type) {
    return TypeArgs.find({type_id: type._id});
  }
}

Template.types.events({
  'change select': function(evt) {
    Router.setList(evt.target.value);
  }
})

// Track type in URL
var SrlRouter = Backbone.Router.extend({
  routes: {
    ":type_name": "main"
  },
  main: function (type_name) {
    var oldType = Session.get("type_name");
    if (oldType !== type_name) {
      Session.set("type_name", type_name);
      //мне наверно тоже надо будет еще что-то чистить
      //Session.set("tag_filter", null);
    }
  },
  setList: function (type_name) {
    this.navigate(type_name, true);
  }
});

Router = new SrlRouter;

Meteor.startup(function () {
  Backbone.history.start({pushState: true});
});
