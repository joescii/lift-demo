angular.module("Chat", ["lift-ng", "ChatServer"])
.controller("chatMessages", ["$scope", "chatService", function($scope, chatService) {
  $scope.msgs = [];
  chatService.messages().then(function(msgs) {$scope.msgs = msgs});
  $scope.$on("NewMessage", function(e, msg) {
    console.log(msg);
    $scope.msgs.push(msg);
  })
}])
.controller("chatSubmission", ["$scope", "chatService", function($scope, chatService) {
  $scope.currentMessage = "";
  $scope.submit = function() {
    console.log("submitting "+$scope.currentMessage);
    chatService.submit($scope.currentMessage);
    $scope.currentMessage = "";
  }
}])