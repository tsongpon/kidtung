// define angular module/app
var kidtungApp = angular.module('kidtungApp', []);

// create angular controller and pass in $scope and $http
function createTripController($scope, $http) {
    //$scope.formData = {};

    // process the form
    $scope.processForm = function() {
        var memberArray = document.getElementsByName('member');
        var size = memberArray.length;
        var mems = [];
        for(var i=0;i<size;i++) {
            mems[i] = memberArray[i].value;
        }
        $http({
            method  : 'PUT',
            url     : '/api/kidtung/trips/'+$scope.name,
            data: {
                id: 4,
                name: $scope.name,
                description: $scope.desc,
                members: mems
            },  // pass in data as strings
            headers : { 'Content-Type': 'application/json' }  // set the headers so angular passing info as form data (not request payload)
        })
            .success(function(data) {
                $scope.address = data;
            });
    };
}

// create angular controller and pass in $scope and $http
function addExpendController($scope, $http) {

// process the form
$scope.expendForm = function() {
$http({
method  : 'POST',
url     : '/api/kidtung/trips/01/members/pang/expends',
data: {
name: $scope.name,
purpose: $scope.purpose,
price: $scope.price,
date: $scope.date
},  // pass in data as strings
headers : { 'Content-Type': 'application/json' }  // set the headers so angular passing info as form data (not request payload)
})
.success(function(data) {
//$scope.address = data;
    $('#newExpend').modal('hide')
});
};
}

var init = function ($scope, $http) {
    $http.get('/api/kidtung/mock').
        success(function(data, status, headers, config) {
            $scope.memberExpends = data;
        }).
        error(function(data, status, headers, config) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        });
};
