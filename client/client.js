// When editing verb text, ID of the verb
Session.setDefault('editing_verb', null);

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
  }
));

Template.verbs.verbs = function () {
  var type = currentType();
  if (!type)
    return [];

  return Verbs.find({type_id: type._id}, {sort: {inf: 1}});
};

Template.verb.editing = function () {
  return Session.equals('editing_verb', this._id);
};

Template.verb.events({
  'click .display .inf-edit': function (evt, tmpl) {
    Session.set('editing_verb', this._id);
    Deps.flush(); // update DOM before focus
    activateInput(tmpl.find("#verb-input"));
  },
  'click .destroy': function () {
    Args.find({verb_id: this._id}).forEach(function(arg) {
      Args.remove({_id: arg._id});
    });
    Verbs.remove(this._id);
  },
});

Template.verb.events(okCancelEvents(
  '#verb-input',
  {
    ok: function (value) {
      Verbs.update(this._id, {$set: {inf: value}});
      Session.set('editing_verb', null);
    },
    cancel: function () {
      Session.set('editing_verb', null);
    }
  }));

Template.verb_edit.args = function() {
  return Args.find({verb_id: this._id});
}

Template.verb_edit.events({
  'click .addarg': function() {
    Args.insert({verb_id: this._id, type_arg_id: TypeArgs.findOne({type_id: currentType()._id})._id, noun_case: NounCases[0]});
  }
});

Template.arg.noun_cases = function() {
  return NounCases;
}

Template.arg.selected_case = function(value) {
  return (value == this ? ' selected' : '');
}

Template.arg.type_args = function() {
  return currentTypeArgs();
}

Template.arg.selected_type_arg = function(value) {
  return (value == this._id ? ' selected' : '');
}

Template.arg.events(okCancelEvents(
  '.prep-input',
  {
    ok: function(value) {
      if (value) {
        Args.update({_id: this._id}, {$set: {prep: value}});
      } else {
        Args.update({_id: this._id}, {$unset: {prep: value}})
      }
    }
  },
  true
));

Template.arg.events({
  'change .noun-case-select': function(evt) {
    var value = evt.target.value;
    Args.update({_id: this._id}, {$set: {noun_case: value}});
  },
  'change .type-arg-select': function(evt) {
    var value = evt.target.value;
    console.log(value);
    Args.update({_id: this._id}, {$set: {type_arg_id: value}});
  },
  'click .remove': function() {
    Args.remove(this._id);
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
