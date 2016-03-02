globals = {}
//server = '127.0.0.1/'

var comp = () => {
  var fs = arguments
  var val = undefined
  return x => (val = x, _.each (fs, h => val = h (val)), val)
}

;(app = angular.module ('App', []))
  .factory ('AppData', [() => () => {}])
  .factory ('HttpSvc', [
    '$http',
    ($http) => {
      var get = url => config => $http.get (url, config)
      var post = url => (data, config) => $http.post ('api/' + url, data, config)
      
      return {
        _data: f => x => f (x.data),
        login: (user, pass) => post ('login') ({user: user, pass: pass}),
        register: obj => post ('register') (obj),
        logout: () => post ('logout') ()
      }
    }
])
  .controller ('AppCtrl', [
    '$scope', 'AppData',
    ($scope, AppData) => {
      $scope.appData = AppData
      
      // polls for the existence of the home page body
      var go_home = () => {
        $scope.appData.state = ''
        $scope.$apply ()
        $scope.appData.state = 'home'
        $scope.$apply ()
        if (!$ ('#home_page').children ().length) _.delay (go_home, 1)
      }
      _.delay (go_home, 1)
	}
])
  .controller ('RegisterCtrl', [
    '$scope', 'AppData', 'HttpSvc',
    ($scope, AppData, HttpSvc) => {
      $scope.register = () => {
        if ($scope.pass === $scope.pass2)
          HttpSvc.register ({
            user: $scope.user,
            pass: $scope.pass,
            fname: $scope.fname,
          })
            .then (
              HttpSvc._data (data => {
                document.cookie = 'sid=' + data
                alert ('Registered new user')
                AppData.fname = $scope.user
                AppData.state = 'home'
              },
              () => $scope.error = 'Failed to register new user'
            )
          )
        else
          $scope.error = 'Failed to register new user'
      }
    }
])
  .controller ('LoginCtrl', [
    '$scope', 'AppData', 'HttpSvc',
    ($scope, AppData, HttpSvc) => {
      $scope.login = () => {
        HttpSvc.login ($scope.user, $scope.pass)
          .then (
            HttpSvc._data (data => {
              document.cookie = 'sid=' + data
              alert ('Logged in')
              AppData.fname = $scope.user
              AppData.state = 'home'
            }),
            () => $scope.error = 'Failed to log in'
          )
      }
    }
])
  .controller ('LogoutCtrl', [
    '$scope', 'AppData', 'HttpSvc',
    ($scope, AppData, HttpSvc) => {
      HttpSvc.logout ()
        .then (
          HttpSvc._data (data => {
            // delete cookie
            document.cookie = 'sid=; expires=Thu, 01 Jan 1970 00:00:01 GMT;'
            alert ('Logged out')
            AppData.fname = undefined
            AppData.state = 'home'
          }),
          () => {
            $scope.error = 'Failed to log out'
            $scope.$apply ()
          }
        )
    }
])