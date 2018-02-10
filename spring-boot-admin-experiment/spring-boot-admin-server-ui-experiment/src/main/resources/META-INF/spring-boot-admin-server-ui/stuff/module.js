(function() {
    'use strict';
    var module = angular.module('sba-stuff', ['sba-core']);
    sbaModules.push(module.name);

    module.controller('stuffCtrl', ['$scope', function($scope) {
        $scope.message = 'hello!';
        $scope.counter = 0;

        $scope.buttonClick = function() {
            ++$scope.counter;
        };
    }]);

    module.config(['$stateProvider', function($stateProvider) {
        $stateProvider.state('stuff', {
            url: '/stuff',
            template: `
<div class="container">
    <h3>Stuff</h3>
    <p>Controller says: {{message}}</p>
    <p>Counter: {{counter}} <button type="button" ng-click="buttonClick()">Click me!</button></p>
</div>
`,
            controller: 'stuffCtrl'
        });
    }]);

    module.run(['MainViews', function(MainViews) {
        MainViews.register({
            order: 200,
            title: 'Stuff',
            state: 'stuff'
        });
    }]);
})(sbaModules, angular);
