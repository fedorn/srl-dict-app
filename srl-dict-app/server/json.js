Meteor.startup(function () {
  HTTP.methods({
    'data.json': function() {
      var dataFromArg = function(arg) {
        var dataArg = {}
        if (arg.prep) {
          dataArg.prep = arg.prep;
        }
        if (arg.noun_case) {
          dataArg.noun_case = arg.noun_case;
        }
        if (arg.example) {
          dataArg.example = arg.example;
        }
        if (arg.type_arg_id) {
          dataArg.type_arg = TypeArgs.find({_id: arg.type_arg_id}).fetch()[0].name;
        }
        return dataArg;
      };

      this.setContentType('application/json');
      var data = {};

      Args.find().forEach(function(arg) {
        var verb = Verbs.find({_id: arg.verb_id}).fetch()[0];
        var type = Types.find({_id: verb.type_id}).fetch()[0];
        if (!data[type.name]) { data[type.name] = {}; }
        if (!data[type.name][verb.inf]) { data[type.name][verb.inf] = []; }
        var dataArg = dataFromArg(arg);
        data[type.name][verb.inf].push(dataArg);
      });
      return EJSON.stringify(data);
    }, 'event-types.json': function() {
      var typeSystem = {};
      Types.find().forEach(function(type) {
        typeSystem[type.name] = [];
        TypeArgs.find({type_id: type._id}).forEach(function(typeArg) {
          typeSystem[type.name].push(typeArg.name);
        });
      });
      return EJSON.stringify(typeSystem);
    }
  });
});
