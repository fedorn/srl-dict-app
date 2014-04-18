#!/usr/bin/env node

/**
 * Module dependencies.
 */

var program = require('commander')
  , fs = require('fs')
  , MongoClient = require('mongodb').MongoClient
  , format = require('util').format;


program
  .version('0.0.1')
  .option('-u, --mongourl <url>', 'URL to connecto to mongo')
  .option('-j, --jsonfile <file>', 'File to import json from')
  .parse(process.argv);

fs.readFile(program.jsonfile, 'utf8', function (err, data) {
  if (err) {
    console.log('Error: ' + err);
    return;
  }

  data = JSON.parse(data);

  importJson(data);
});

var importJson = function (data) {
  MongoClient.connect(program.mongourl, function(err, db) {
    var collection = db.collection('types');
    collection.find().toArray(function(err, results) {
      console.dir(results);
      // Let's close the db
      db.close();
    });
  });
};
