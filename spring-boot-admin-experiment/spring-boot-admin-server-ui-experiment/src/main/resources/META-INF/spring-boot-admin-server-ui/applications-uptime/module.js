(function() {
    'use strict';
    var module = angular.module('sba-applications-uptime', ['sba-applications']);
    sbaModules.push(module.name);

    module.controller('uptimeCtrl', ['$scope', 'application', 'uptimePoller', function($scope, application, uptimePoller) {
        $scope.app = application;
        $scope.uptimePoller = uptimePoller;
    }]);

    module.config(['$stateProvider', function($stateProvider) {
        $stateProvider.state('applications.uptime', {
            url: '/uptime',
            template: `
<div class="container">
    <p>Uptime: {{uptimePoller.uptime}}</p>
    <pre>{{app|json}}</pre>
</div>
`,
            controller: 'uptimeCtrl',
            resolve: {
                uptimePoller: ['application', '$timeout', function(application, $timeout) {
                    var uptimePoller = {
                        uptime: -1,
                        shouldStop: false
                    };
                    uptimePoller.start = function() {
                        var that = this;
                        function loop() {
                            application.getMetrics().then(function(response) {
                                var metricsData = response.data;
                                that.uptime = metricsData['uptime'];
                                if(!that.shouldStop) {
                                    $timeout(loop, 1000);
                                }
                            });
                        };

                        loop();
                    };
                    uptimePoller.stop = function() {
                        this.shouldStop = true;
                    };
                    return uptimePoller;
                }]
            },
            onEnter: ['uptimePoller', function(uptimePoller) {
                uptimePoller.start();
            }],
            onExit: ['uptimePoller', function(uptimePoller) {
                uptimePoller.stop();
            }]
        });
    }]);

    module.run(['ApplicationViews', '$sce', function(ApplicationViews, $sce) {
        ApplicationViews.register({
            order: 100,
            title: $sce.trustAsHtml('<i class="fa fa-arrow-circle-up fa-fw"></i>Uptime'),
            state: 'applications.uptime',
            show: function(application) {
                return true;
            }
        });
    }]);
})(sbaModules, angular);
