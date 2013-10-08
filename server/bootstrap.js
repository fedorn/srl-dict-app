Meteor.startup(function () {
  if (Types.find().count() === 0) {
    var data = [
      {name: "Buying",
       args: ["whom", "who", "when", "price"]
      },
      {name: "Retiring",
       args: ["who", "from", "age", "position"]
      },
    ];

    for (var i = 0; i < data.length; i++) {
      var type_id = Types.insert({name: data[i].name});
      for (var j = 0; j < data[i].args.length; j++) {
        var arg = data[i].args[j];
        TypeArgs.insert({type_id: type_id, name: arg})
      }
    }
  }
});
