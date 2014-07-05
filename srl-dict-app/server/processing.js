Meteor.methods({
  callProcessing: function() {
    this.unblock();
    return HTTP.post(Meteor.settings.processingUrl)
  }
});
