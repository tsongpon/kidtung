// define angular module/app
var kidtungApp = angular.module('kidtungApp', []);

// create angular controller and pass in $scope and $http
function createTripController($scope, $http) {
    //$scope.formData = {};

    // process the form
    $scope.processForm = function () {
        var memberArray = document.getElementsByName('member');
        var size = memberArray.length;
        var mems = [];
        for (var i = 0; i < size; i++) {
            mems[i] = memberArray[i].value;
        }
        $http({
            method: 'PUT',
            url: '/api/kidtung/trips/' + $scope.name,
            data: {
                id: 4,
                name: $scope.name,
                description: $scope.desc,
                members: mems
            },  // pass in data as strings
            headers: {'Content-Type': 'application/json'}  // set the headers so angular passing info as form data (not request payload)
        })
            .success(function (data) {
                $scope.address = data;
            });
    };
}

// create angular controller and pass in $scope and $http
function addExpendController($scope, $http) {

// process the form
    $scope.expendForm = function () {
        var path = window.location.pathname;
        var tripCode = path.split("/")[2];
        $http({
            method: 'POST',
            url: '/api/kidtung/trips/'+tripCode+'/members/'+$scope.name+'/expends',
            data: {
                name: $scope.name,
                item: $scope.purpose,
                price: $scope.price,
                date: $scope.date
            },  // pass in data as strings
            headers: {'Content-Type': 'application/json'}  // set the headers so angular passing info as form data (not request payload)
        })
            .success(function (data) {
                //$scope.address = data;
                $('#newExpend').modal('hide');
                initPaymentList($scope, $http);
            });
    };

    initPaymentList($scope, $http);
}

function summaryController($scope, $http) {
    initSummary($scope, $http);
}

var initPaymentList = function ($scope, $http) {
    var path = window.location.pathname;
    var tripCode = path.split("/")[2];

    $http.get('/api/kidtung/trips/'+tripCode+'/members').
        success(function (data, status, headers, config) {
            $scope.allMember = data;
        }).
        error(function (data, status, headers, config) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        });

    $http.get('/api/kidtung/trips/'+tripCode+'/expends').
        success(function (data, status, headers, config) {
            $scope.allExpends = data;
        }).
        error(function (data, status, headers, config) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        });

    $http.get('/api/kidtung/trips/'+tripCode+'/reports').
        success(function (data, status, headers, config) {
            $scope.total = data.total;
        }).
        error(function (data, status, headers, config) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        });
};

var initSummary = function ($scope, $http) {
    var path = window.location.pathname;
    var tripCode = path.split("/")[2];
    $http.get('/api/kidtung/trips/'+tripCode+'/reports/eachmember').
        success(function (data, status, headers, config) {
            $scope.totalForEach = data;
        }).
        error(function (data, status, headers, config) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        });

    $http.get('/api/kidtung/trips/'+tripCode+'/reports').
        success(function (data, status, headers, config) {
            $scope.total = data.total;
        }).
        error(function (data, status, headers, config) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        });
};
