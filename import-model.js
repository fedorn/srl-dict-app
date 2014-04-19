var mongoose = require('mongoose');

var Schema = mongoose.Schema
  , ObjectId = Schema.ObjectId;

var typeSchema = Schema({
  name: String
});

exports.Type = mongoose.model('types', typeSchema)

var typeArgSchema = Schema({
  type_id: ObjectId,
  name: String
});

exports.TypeArg = mongoose.model('typeArgs', typeArgSchema, 'typeArgs')

var verbSchema = Schema({
  type_id: ObjectId,
  inf: String
});

exports.Verb = mongoose.model('verbs', verbSchema);

var argSchema = Schema({
  verb_id: ObjectId,
  prep: String,
  noun_case: String,
  type_arg_id: ObjectId,
  example: String
});

exports.Arg = mongoose.model('args', argSchema);
