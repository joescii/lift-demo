angular.module("Chat", ["lift-ng"])
.controller("chatMessages", ["$scope", function($scope) {
  $scope.msgs = [
    {name: "joe", msg: "you"},
    {name: "joe", msg: "suck"},
    {name: "joe", msg: "javascript"}
  ];
}])