var currentType = function() {
  return Types.findOne({name: Session.get("type_name")});
}

var currentTypeArgs = function() {
  type = currentType();
  if (type) {
    return TypeArgs.find({type_id: type._id});
  } else {
    return [];
  }
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
var okCancelEvents = function (selector, callbacks, canBeEmpty) {
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
        if (value || canBeEmpty)
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

Template.types.type_name = function () {
  return Session.get("type_name");
};

Template.types.types = function () {
  return Types.find({}, {sort: {name: 1}});
};

Template.types.active = function () {
  return Session.equals("type_name", this.name) ? 'active' : ''
}

Template.type_args.type_args = function () {
  return currentTypeArgs();
}

Template.types.events({
  'click .dropdown-menu li a': function(evt) {
    Router.setList(evt.target.text);
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
  },
  false
));

Template.verbs.verbs = function () {
  var type = currentType();
  if (!type)
    return [];

  return Verbs.find({type_id: type._id}, {sort: {inf: 1}});
};

Template.verb_edit.args = function () {
  var verb_id = this._id;
  return _.map(currentTypeArgs().fetch(), function(type_arg) {
    var selector = {type_id: type_arg._id, verb_id: verb_id};
    var arg = Args.findOne(selector);
    return {
      name: type_arg.name,
      prep: arg ? arg.prep : '',
      noun_case: arg ? arg.noun_case : '',
      selector: selector,
      arg: function() {
        return arg = Args.findOne(selector);
      }
    };
  });
}

Template.arg.noun_cases = function () {
  return NounCases;
}

Template.arg.selected_case = function(value) {
  return (value == this ? ' selected' : '');
}

Template.arg.events(okCancelEvents(
  '.prep-input',
  {
    ok: function(value) {
      var arg = this.arg();
      if (value) {
        if (arg) {
          Args.update({_id: arg._id}, {$set: {prep: value}});
        } else {
          Args.insert(_.extend(this.selector, {prep: value}));
        }
      } else if (arg) {
        Args.update({_id: arg._id}, {$unset: {prep: value}})
      }
    }
  },
  true
));

Template.arg.events({
  'change .noun-case-select': function(evt) {
    var value = evt.target.value;
    var arg = this.arg();
    if (value) {
      if (arg) {
        console.log("if value if arg");
        Args.update({_id: arg._id}, {$set: {noun_case: value}});
      } else {
        console.log("if value else");
        Args.insert(_.extend(this.selector, {noun_case: value}));
      }
    } else if (arg) {
      console.log("else if arg");
      Args.update({_id: arg._id}, {$unset: {noun_case: value}})
    }
  }
});

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
