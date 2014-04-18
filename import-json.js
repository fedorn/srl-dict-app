#!/usr/bin/env node

/**
 * Module dependencies.
 */

var program = require('commander')
  , fs = require('fs')
  , MongoClient = require('mongodb').MongoClient
  , format = require('util').format;

var ObjectID = require('mongodb').ObjectID;

program
  .version('0.0.1')
  .option('-u, --mongourl <url>', 'URL to connecto to mongo')
  .option('-d, --drop', 'Drop db before import')
  .option('-j, --jsonfile [file]', 'File to import json from')

program.on('--help', function(){
  console.log('You can get url with `meteor mongo --url [site domain]`');
});

program.parse(process.argv);

if (program.drop) {
  console.log("Dropping tables...")
  // TODO: drop tables
}

if (program.jsonfile) {
  console.log("Importing data...")
  fs.readFile(program.jsonfile, 'utf8', function (err, data) {
    if (err) {
      console.log('Error: ' + err);
      return;
    }

    data = JSON.parse(data);

    importJson(data);
  });
}

var importJson = function (data) {
  MongoClient.connect(program.mongourl, function(err, db) {
    Types = db.collection("types")
    TypeArgs = db.collection("typeArgs")
    Verbs = db.collection("verbs")
    Args = db.collection("args")

    for (typeName in data) {
      console.log(typeName);
      Types.findOne({name: typeName}, function (err, type) {
        console.log(type._id);
      });
    }
    db.close();
  });
};
