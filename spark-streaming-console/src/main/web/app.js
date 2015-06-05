angular.module('console', [])
.controller('AppController', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {
    $scope.status = null;

    poll();

    function poll() {
        $timeout(function() {
            return $http.get('/status').then(function(response) {
                $scope.status = response.data;
            }).then(poll);
        }, 500);
    };
}]);
