Meteor.startup(function () {
  if (Types.find().count() === 0) {
    var data = [
      {
        name: "INJURE",
        args: ["Time", "Place", "Agent", "Victim", "Instrument"]
      },
      {
        name: "DIE",
        args: ["Time", "Place", "Person", "Killer", "Instrument"]
      },
      {
        name: "MOVEMENT-TRANSPORT",
        args: ["Time", "Agent", "Artifact", "Vehicle", "Price", "Origin", "Destination"]
      },
      {
        name: "TRANSFER-OWNERSHIP",
        args: ["Time", "Place", "Buyer", "Seller", "Beneficiary", "Artifact", "Price"]
      },
      {
        name: "TRANSFER-MONEY",
        args: ["Time", "Place", "Giver", "Recipient", "Beneficiary", "Money"]
      },
      {
        name: "START-ORG",
        args: ["Time", "Place", "Agent", "Org"]
      },
      {
        name: "MERGE",
        args: ["Time", "Place", "Org"]
      },
      {
        name: "DECLARE-BANKRUPTCY",
        args: ["Time", "Place", "Org"]
      },
      {
        name: "END-ORG",
        args: ["Time", "Place", "Org"]
      },
      {
        name: "CONTACT-MEET",
        args: ["Time", "Place", "Entity"]
      },
      {
        name: "START-POSITION",
        args: ["Time", "Place", "Person", "Entity", "Position"]
      },
      {
        name: "END-POSITION",
        args: ["Time", "Place", "Person", "Entity", "Position"]
      },
      {
        name: "NOMINATE",
        args: ["Time", "Place", "Person", "Agent", "Entity", "Position"]
      },
      {
        name: "ELECT",
        args: ["Time", "Place", "Person", "Entity", "Position"]
      },
      {
        name: "TRIAL-HEARING",
        args: ["Time", "Place", "Defendant", "Prosecutor", "Adjucator", "Crime"]
      },
      {
        name: "CHARGE-INDICT",
        args: ["Time", "Place", "Defendant", "Prosecutor", "Adjucator", "Crime"]
      },
      {
        name: "SUE",
        args: ["Time", "Place", "Defendant", "Prosecutor", "Adjucator", "Crime"]
      },
      {
        name: "FINE",
        args: ["Time", "Place", "Entity", "Money", "Adjucator", "Crime"]
      }
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
