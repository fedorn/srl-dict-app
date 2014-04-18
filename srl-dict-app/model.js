Types = new Meteor.Collection("types", {idGeneration: 'MONGO'})
TypeArgs = new Meteor.Collection("typeArgs", {idGeneration: 'MONGO'})
Verbs = new Meteor.Collection("verbs", {idGeneration: 'MONGO'})
Args = new Meteor.Collection("args", {idGeneration: 'MONGO'})
NounCases = [["nomn", "Именительный"], ["gent", "Родительный"], ["datv", "Дательный"], ["accs", "Винительный"], ["ablt", "Творительный"], ["loct", "Предложный"]]
