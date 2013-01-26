<!DOCTYPE html>
<html ng-app="app">
    <head>
        <title></title>

        <link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap-combined.min.css" rel="stylesheet">      
		
		<script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.9.0/jquery.min.js"></script>
        <script src="http://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.0/jquery-ui.min.js"></script>
        <script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js"></script>
        <script src="http://cdnjs.cloudflare.com/ajax/libs/angular.js/1.1.1/angular.min.js"></script>
        <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.3/underscore-min.js"></script>		
		
		<script type="text/javascript">
			var app = angular.module("app", []);
			app.constant("serviceUrl", "http://localhost:8080/api/HelloService/");
			
			app.factory("api", function($http, serviceUrl) {
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
			
			app.controller("MyController", function($scope, api) {
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
	</head>
	<body ng-controller="MyController">
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
	</body>
</html>