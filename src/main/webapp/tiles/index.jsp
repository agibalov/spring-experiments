<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html ng-app="app">
    <head>
        <title>test</title>

        <link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet">
        <link href="http://netdna.bootstrapcdn.com/font-awesome/3.0.2/css/font-awesome.css" rel="stylesheet"> 
        <link href="<c:url value="/static/angular-ui.min.css" />" rel="stylesheet" type="text/css" />		
		
		<script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.9.0/jquery.min.js"></script>
        <script src="http://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.0/jquery-ui.min.js"></script>
        <script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
        <script src="http://cdnjs.cloudflare.com/ajax/libs/angular.js/1.1.1/angular.min.js"></script>
        <script src="<c:url value="/static/angular-ui.min.js" />"></script>
        <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.3/underscore-min.js"></script>		
	</head>
	<body ng-controller="AppController">
		<div class="container">
			<div class="row">
				<div class="page-header">
					<h1>test <small>test</small></h1>
				</div>
			</div>
			<div class="row">
				<div class="span12">
					<form class="form-horizontal">
						<input type="text" ng-model="name" />
						<button ng-click="refreshName()" class="btn">Send</button>
					</form>
					<div>{{ lastNameResponse|json }}</div>
				</div>
			</div>
			<div class="row">
				<div class="page-header">
					<h1>test <small>test</small></h1>
				</div>
			</div>
			<div class="row">
				<div class="span12">
					<form class="form-horizontal">
						<input type="text" ng-model="numberA" />
						<input type="text" ng-model="numberB" />
						<button ng-click="refreshAdd()" class="btn">Send</button>
					</form>
					<div>{{ lastAddResponse|json }}</div>
				</div>
			</div>
		</div>
		
		<div
            ui-modal
            data-backdrop="static"
            class="modal hide"
            ng-controller="BackendMonitorModalController"
            ng-model="backendState.shouldDisplayPopup">
            <div class="modal-body">
                <h3 class="text-center" ng-show="backendState.isLoading">
                    <i class="icon-spinner icon-spin"></i> Loading
                </h3>
                <div class="alert alert-block alert-error" ng-show="backendState.isFailed">
                    <h4>Something bad happened</h4>
                    Try reloading the page - it helps normally.
                </div>
            </div>
        </div>        
        
        <script type="text/javascript">        
			var app = angular.module("app", ["ui"], function($httpProvider) {
				$httpProvider.responseInterceptors.push(function ($q, $timeout, backendMonitor) {
                    return function (promise) {
                        var responseDelay = 300;
                        return promise.then(
                            function (response) {
                                $timeout(function () {
                                	backendMonitor.notifyRequestSucceeded();
                                }, responseDelay);
                                return response;
                            }, function (response) {
                                $timeout(function () {
                                	backendMonitor.notifyRequestFailed();
                                }, responseDelay);
                                return $q.reject(response);
                            });
                    };
                });				
			});			
			app.constant("serviceUrl", "<c:url value="/api/HelloService/" />");
			
			angular.module("app").controller("BackendMonitorModalController", function ($scope, backendMonitor) {
                $scope.backendState = backendMonitor.getState();
            });
			
			angular.module("app").factory("backendMonitor", function () {
                var state = {
                    shouldDisplayPopup: false,
                    isLoading: false,
                    isFailed: false,
                    requestCount: 0,
                    failureCount: 0
                };

                function updateState() {
                    state.isLoading = state.requestCount > 0;
                    state.isFailed = state.failureCount > 0;
                    state.shouldDisplayPopup = state.isLoading || state.isFailed;
                };

                return {
                    getState: function () {
                        return state;
                    },
                    notifyRequestStarted: function () {
                        ++state.requestCount;
                        updateState();
                    },
                    notifyRequestSucceeded: function () {
                        --state.requestCount;
                        updateState();
                    },
                    notifyRequestFailed: function () {
                        --state.requestCount;
                        ++state.failureCount;
                        updateState();
                    }
                };
            });
			
			app.factory("api", function($http, backendMonitor, serviceUrl) {
				$http.defaults.transformRequest = function (data) {
					backendMonitor.notifyRequestStarted();
                    if (data === undefined) {
                        return data;
                    }

                    return angular.toJson(data);
                };
                
				return {
					SayHello: function(name, onSuccess) {
						$http.get(serviceUrl + "SayHello", { params: { name: name }}).success(function(result) {
							onSuccess(result);
						});
					},
					
					AddNumbers: function(numberA, numberB, onSuccess) {
						$http.post(serviceUrl + "AddNumbers", {numberA:numberA, numberB:numberB}).success(function(result) {
							onSuccess(result);
						});
					}
				};
			});		
			
			app.controller("AppController", function($scope, api) {
				$scope.name = "";
				$scope.lastNameResponse = null;
				$scope.refreshName = function() {
					api.SayHello($scope.name, function(result) {
						$scope.lastNameResponse = result;
					});
				};
				
				$scope.numberA = 0;
				$scope.numberB = 0;
				$scope.lastAddResponse = 0;
				$scope.refreshAdd = function() {
					api.AddNumbers($scope.numberA, $scope.numberB, function(result) {
						$scope.lastAddResponse = result;
					});
				}
			});	
        </script>
	</body>
</html>