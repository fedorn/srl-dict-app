var currentType = function() {
  return Types.findOne({name: Session.get("type_name")});
}

Meteor.startup(function () {
  Deps.autorun(function () {
    if (! Session.get("type_name") || Types.find({name: Session.get("type_name")}).count() == 0) {
      var type = Types.findOne({}, {sort: {name: 1}});
      if (type)
        Router.setList(type.name);
    }
  });
});

////////// Helpers for in-place editing //////////

// Returns an event map that handles the "escape" and "return" keys and
// "blur" events on a text input (given by selector) and interprets them
// as "ok" or "cancel".
var okCancelEvents = function (selector, callbacks) {
  var ok = callbacks.ok || function () {};
  var cancel = callbacks.cancel || function () {};

  var events = {};
  events['keyup '+selector+', keydown '+selector+', focusout '+selector] =
    function (evt) {
      if (evt.type === "keydown" && evt.which === 27) {
        // escape = cancel
        cancel.call(this, evt);

      } else if (evt.type === "keyup" && evt.which === 13 ||
                 evt.type === "focusout") {
        // blur/return/enter = ok/submit if non-empty
        var value = String(evt.target.value || "");
        if (value)
          ok.call(this, value, evt);
        else
          cancel.call(this, evt);
      }
    };

  return events;
};

var activateInput = function (input) {
  input.focus();
  input.select();
};


Template.types.types = function () {
  return Types.find({}, {sort: {name: 1}});
};

Template.types.selected = function () {
  return Session.equals("type_name", this.name) ? ' selected' : ''
}

Template.args.args = function () {
  type = currentType();
  if (type) {
    return TypeArgs.find({type_id: type._id});
  }
}

Template.types.events({
  'change select': function(evt) {
    Router.setList(evt.target.value);
  }
})

Template.verbs.events(okCancelEvents(
  '#new-verb',
  {
    ok: function (text, evt) {
      Verbs.insert({
        inf: text,
        type_id: currentType()._id,
      });
      evt.target.value = '';
    }
  }));

Template.verbs.verbs = function () {
  var type = currentType();
  if (!type)
    return {};

  return Verbs.find({type_id: type._id}, {sort: {inf: 1}});
};




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
