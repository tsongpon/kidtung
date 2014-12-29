// define angular module/app
var kidtungApp = angular.module('kidtungApp', []);

// create angular controller and pass in $scope and $http
function createTripController($scope, $http) {

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

    $scope.validateTripName = function () {
        $http.head('/api/kidtung/trips/' + $scope.name).
            success(function(data, status, headers, config) {
                $scope.validateTripMessage = "Trip name "+ $scope.name +" is not available";
                $scope.hasError = true;
            }).
            error(function(data, status, headers, config) {
                if(status == 404) {
                    $scope.hasError = false;
                    $scope.validateTripMessage = '';
                } else {
                    $scope.hasError = true;
                    $scope.validateTripMessage = 'Server is not available';
                }
            });
    };
}

// create angular controller and pass in $scope and $http
function addExpendController($scope, $http) {

    $scope.deleteExpend = function(expendCode, memberName) {
        var path = window.location.pathname;
        var tripCode = path.split("/")[2];
        $http.delete('/api/kidtung/trips/'+tripCode+'/members/'+memberName+'/expends/'+expendCode).
            success(function (data, status, headers, config) {
                initPaymentList($scope, $http);
            }).
            error(function (data, status, headers, config) {
                // called asynchronously if an error occurs
                // or server returns response with an error status.
            });
    };

    $scope.editExpend = function(expendCode, memberName) {
        var path = window.location.pathname;
        var tripCode = path.split("/")[2];
        $http.get('/api/kidtung/trips/'+tripCode+'/members/'+memberName+'/expends/'+expendCode).
            success(function (data, status, headers, config) {
                $scope.name = data.name;
                $scope.purpose = data.item;
                $scope.price = data.price;
                $scope.date = data.date;
                $scope.code = data.code;
            }).
            error(function (data, status, headers, config) {
                // called asynchronously if an error occurs
                // or server returns response with an error status.
            });
    };

// process the form
    $scope.expendForm = function () {
        var path = window.location.pathname;
        var tripCode = path.split("/")[2];
        if($scope.code=='' || $scope.code==null) {
            $http({
                method: 'POST',
                url: '/api/kidtung/trips/' + tripCode + '/members/' + $scope.name + '/expends',
                data: {
                    name: $scope.name,
                    item: $scope.purpose,
                    price: $scope.price,
                    date: $scope.date
                },  // pass in data as strings
                headers: {'Content-Type': 'application/json'}  // set the headers so angular passing info as form data (not request payload)
            }).success(function (data) {
                //$scope.address = data;
                $('#newExpend').modal('hide');
                $scope.name=null;
                $scope.purpose = null;
                $scope.price = null;
                $scope.date = null;
                $scope.code = null;
                initPaymentList($scope, $http);
            });
        } else {
            $http({
                method: 'PUT',
                url: '/api/kidtung/trips/' + tripCode + '/members/' + $scope.name + '/expends/'+$scope.code,
                data: {
                    name: $scope.name,
                    item: $scope.purpose,
                    price: $scope.price,
                    date: $scope.date
                },  // pass in data as strings
                headers: {'Content-Type': 'application/json'}  // set the headers so angular passing info as form data (not request payload)
            }).success(function (data) {
                //$scope.address = data;
                $('#newExpend').modal('hide');
                $scope.purpose = null;
                $scope.price = null;
                $scope.date = null;
                $scope.code = null;
                $scope.name = null;
                initPaymentList($scope, $http);
            });
        }
    };


    initPaymentList($scope, $http);
    var path = window.location.pathname;
    $scope.globalTripCode = path.split("/")[2];
}

function summaryController($scope, $http) {
    initSummary($scope, $http);
    var path = window.location.pathname;
    $scope.globalTripCode = path.split("/")[2];
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
            $scope.tripAverage = data.average;
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
            $scope.tripAverage = data.average;
            $scope.total = data.total;
        }).
        error(function (data, status, headers, config) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        });
};
