#!/usr/bin/env node

/**
 * Module dependencies.
 */

var program = require('commander')
  , fs = require('fs')
  , mongoose = require('mongoose')

program
  .version('0.0.1')
  .option('-u, --mongourl <url>', 'URL to connect to mongo')
  .option('-d, --drop', 'Drop db before import')
  .option('-j, --jsonfile [file]', 'File to import json from')

program.on('--help', function(){
  console.log('You can get url with `meteor mongo --url [site domain]`.');
  console.log('When process of saving stops, you can exit with Ctrl-C.');
});

program.parse(process.argv);


var mongoose = require('mongoose');
mongoose.connect(program.mongourl);
var model = require('./mongoose-model.js');
var Type = model.Type;
var TypeArg = model.TypeArg;
var Verb = model.Verb;
var Arg = model.Arg;

var importing = function () {
  if (program.jsonfile) {
    console.log("Importing data... Press Ctrl-C to exit when process stops.")
    fs.readFile(program.jsonfile, 'utf8', function (err, data) {
      if (err) {
        console.log('Error: ' + err);
        return;
      }

      var data = JSON.parse(data);

      importJson(data);
    });
  }
}

var importJson = function (data) {
  for (typeName in data) {
    Type.findOne({name: typeName}, function (err, type) {
      for (verbInf in data[type.name]) {
        //console.log({inf: verbInf, type_id: type.id});
        Verb.findOne({inf: verbInf, type_id: type.id}, (function(verbInf) { return function (err, verb) {
          if (!verb) {
            var verb = new Verb({inf: verbInf, type_id: type.id});
            verb.save()
          }
          for (i = 0; i < data[type.name][verb.inf].length; ++i) {
            var argData = data[type.name][verb.inf][i];
            if (argData.type_arg != undefined) {
              TypeArg.findOne({type_id: type.id, name: argData.type_arg}, (function(argData) { return function(err, typeArg) {
                var arg = argFromData(verb, argData);
                arg.type_arg_id = typeArg.id;
                arg.save(function (err, arg) {
                  if (err) return console.error(err);
                  console.log('Saved: ' + arg);
                });
              } })(argData));
            } else {
              var arg = argFromData(verb, argData);
              arg.save(function (err, arg) {
                if (err) return console.error(err);
                console.log('Saved: ' + arg);
              });
            }
          }
        } })(verbInf));
      }
    });
  }
};

var argFromData = function (verb, argData) {
  var arg = new Arg();
  arg.verb_id = verb.id;
  if (argData.prep != undefined) {
    arg.prep = argData.prep;
  }
  if (argData.noun_case != undefined) {
    arg.noun_case = argData.noun_case;
  }
  if (argData.example != undefined) {
    arg.example = argData.example;
  }
  return arg;
};

if (program.drop) {
  console.log("Dropping tables...")
  Arg.remove({}, function(err) {
    console.log('Args removed');
    Verb.remove({}, function(err) {
      console.log('Verbs removed');
      importing();
    });
  });
} else {
  importing();
}
