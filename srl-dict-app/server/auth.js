Meteor.startup(function () {
  WebApp.connectHandlers.stack.splice(0, 0, {
    route: '/data.json',
    handle: WebApp.connectHandlers.stack[0].handle
  }, {
    route: '',
    handle: WebApp.__basicAuth__(function(user, pass){
      return Meteor.settings.user == user & Meteor.settings.password == pass;
    })
  });
});
