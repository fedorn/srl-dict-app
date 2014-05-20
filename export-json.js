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
  .option('-j, --jsonfile [file]', 'File to export json to')

program.on('--help', function() {
  console.log('You can get url with `meteor mongo --url [site domain]`.');
  console.log('When process of saving stops, you can write file with Ctrl-C.');
});

program.parse(process.argv);

var mongoose = require('mongoose');
mongoose.connect(program.mongourl);
var model = require('./mongoose-model.js');
var Type = model.Type;
var TypeArg = model.TypeArg;
var Verb = model.Verb;
var Arg = model.Arg;

var data = {}

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
  return dataArg;
};

var writeFile = function() {
  console.log('Writing file...');
  fs.writeFile(program.jsonfile, JSON.stringify(data, null, 4), function (err) {
    if (err) throw err;
    console.log('It\'s saved!');
    process.exit();
  });
}

Arg.find({}, function(err, args) {
  //console.log(args);
  var unprocessed = args.length;
  for (var i = 0; i < args.length; ++i) {
    (function(arg) {
      Verb.findOne({_id: arg.verb_id}, function(err, verb) {
        Type.findOne({_id: verb.type_id}, function(err, type) {
          if (!data[type.name]) {
            data[type.name] = {};
          }
          if (!data[type.name][verb.inf]) {
            data[type.name][verb.inf] = [];
          }
          if (arg.type_arg_id) {
            TypeArg.findOne({_id: arg.type_arg_id}, function(err, typeArg) {
              var dataArg = dataFromArg(arg);
              dataArg["type_arg"] = typeArg.name;
              data[type.name][verb.inf].push(dataArg);
              console.log('Saved: ' + arg);
              unprocessed--;
              if (unprocessed == 0) {
                writeFile();
              }
            });
          } else {
            unprocessed--;
            if (unprocessed == 0) {
              writeFile();
            }
          }
        });
      });
    })(args[i]);
  }
});
