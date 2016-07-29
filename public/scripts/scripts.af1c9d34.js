!function(a){"use strict";a.module("diferentonasStaffApp",["ui.router","ngResource","ui.bootstrap","jcs-autoValidate","toastr","angular-confirm","satellizer"]).constant("RESTAPI",{url:"http://diferentonas.herokuapp.com/api"}).config(["$stateProvider","$urlRouterProvider",function(a,b){a.state("login",{url:"/",templateUrl:"views/login.html",controller:"AuthCtrl",controllerAs:"auth"}).state("home",{url:"/home",templateUrl:"views/main.html",controller:"MainCtrl",controllerAs:"main",data:{requiredLogin:!0}}),b.otherwise("/")}]).config(["$authProvider",function(a){a.google({url:"http://localhost:9000/api/cidadao/auth/google",clientId:"1061066859155-tngvmfeld8800lq6rmuu9dmq15301ucl.apps.googleusercontent.com",scope:["profile","email"]})}]).run(["$rootScope","$state","$auth",function(a,b,c){a.$on("$stateChangeStart",function(a,d){var e=!1;d.data&&d.data.requiredLogin&&(e=!0),e&&!c.isAuthenticated()&&(a.preventDefault(),b.go("login"))})}])}(angular),function(a){"use strict";function b(a,b,c,d,e,f,g,h){function i(){var b={titulo:r.message.title,conteudo:r.message.message,autor:"User01"};e.save(b,function(){h.success("Alerta enviado com sucesso"),r.message={title:"",message:""},a.saveMessageForm.$setPristine(),k()},function(){h.console.error("Não foi possível enviar o alerta")})}function j(a){e.delete({id:a.id},function(){h.success("Mensagem removida com sucesso"),k()},function(){h.error("Não foi possível remover a mensagem")})}function k(){r.messages=e.query(function(){},function(){h.error("Não foi possível carregar mensagens")})}function l(a){c({url:d.url+"/cidadaos",method:"GET",params:{query:a}}).then(function(a){return r.citizens=a.data,a.data.map(function(a){return a.login})},function(a){console.log("error")})}function m(a){r.citizens=[],r.selectedCitizen=a,r.searchCitizenTerm=a.login}function n(){r.staff=f.query(function(){},function(){h.error("Não foi possível carregar os funcionários")})}function o(){var a={id:r.selectedCitizen.id,ministerio:r.selectedCitizen.ministerioDeAfiliacao};c({url:d.url+"/cidadaos/"+r.selectedCitizen.id+"/funcionario",method:"POST",params:a}).then(function(a){h.success("Funcionário cadastrado com sucesso"),r.selectedCitizen={login:""},r.searchCitizenTerm="",n()},function(a){h.console.error("Não foi possível cadastrar o funcionário")})}function p(a){var b={id:a.id};c({url:d.url+"/cidadaos/"+a.id+"/funcionario",method:"DELETE",params:b}).then(function(a){h.success("Funcionário removido com sucesso"),n()},function(a){h.console.error("Não foi possível remover o usuário")})}function q(){return g.isAuthenticated()}var r=this;r.message={title:"",message:""},r.messages=[],r.searchMessageTerm="",r.citizens=[],r.staff=[],r.searchCitizenTerm="",r.selectedCitizen={login:""},r.currentUser=b.localStorage.currentUser,r.saveMessage=i,r.deleteMessage=j,r.isAuthenticated=q,r.getCitizens=l,r.selectCitizen=m,r.saveStaff=o,r.deleteStaff=p,k(),n()}a.module("diferentonasStaffApp").controller("MainCtrl",b),b.$inject=["$scope","$window","$http","RESTAPI","Message","Staff","$auth","toastr"]}(angular),function(a){"use strict";function b(a,b,c,d,e,f){function g(a){e.authenticate(a).then(function(a){console.log(a),f.success("Autenticado!"),e.setToken(a),c.localStorage.token=a,c.localStorage.currentUser=a,d.currentUser=JSON.parse(c.localStorage.currentUser),b.go("home")}).catch(function(a){f.error("Não foi possivel autenticar-se")})}function h(){return e.isAuthenticated()}function i(){e.logout()}var j=this;j.authenticate=g,j.isAuthenticated=h,j.logout=i}a.module("diferentonasStaffApp").controller("AuthCtrl",b),b.$inject=["$scope","$state","$window","$rootScope","$auth","toastr"]}(angular),function(a){"use strict";function b(){return{templateUrl:"views/directives/df-message-card.html",restrict:"E",scope:{message:"=",deleteFunction:"&"}}}a.module("diferentonasStaffApp").directive("dfMessageCard",b)}(angular),function(a){"use strict";function b(a,b){var c=a(b.url+"/mensagens/:id");return c}a.module("diferentonasStaffApp").factory("Message",b),b.$inject=["$resource","RESTAPI"]}(angular),function(a){"use strict";function b(a,b){var c=a(b.url+"/funcionarios/:id");return c}a.module("diferentonasStaffApp").factory("Staff",b),b.$inject=["$resource","RESTAPI"]}(angular),function(a){"use strict";function b(){return{templateUrl:"views/directives/df-staff-card.html",restrict:"E",scope:{citizen:"=",deleteFunction:"&"}}}a.module("diferentonasStaffApp").directive("dfStaffCard",b)}(angular),angular.module("diferentonasStaffApp").run(["$templateCache",function(a){"use strict";a.put("views/directives/df-message-card.html",'<div class="row"> <div class="col-xs-10"> <div class="row-element row-element-primary"> {{ message.criadaEm }} <strong>{{message.titulo}}</strong> </div> </div> <div class="col-xs-2 text-right"> <button class="btn btn-sm btn-default" ng-click="deleteFunction(message)" confirm="Deseja realmente remover este alerta?"> <span class="glyphicon glyphicon-trash"></span> </button> </div> </div>'),a.put("views/directives/df-staff-card.html",'<div class="row"> <div class="col-xs-10"> <div class="row-element row-element-secondary"> <span class="glyphicon glyphicon-star row-element-icon"></span> {{ citizen.login }} </div> </div> <div class="col-xs-2 text-right"> <button class="btn btn-sm btn-default" ng-click="deleteFunction(citizen)" confirm="Deseja realmente remover este alerta?"> <span class="glyphicon glyphicon-trash"></span> </button> </div> </div>'),a.put("views/login.html",'<div class="container-fluid main-pallet-three columns text-center"> <div class="row"> <div class="col-sm-4 col-sm-offset-4"> <img src="images/logo.bf0b4393.png" alt="As Diferentonas" width="300px" height="240px" class="logo"> <div ng-if="!auth.isAuthenticated()"> <img src="images/btn_google+_signin_dark_normal_web.53e122ff.png" ng-click="auth.authenticate(\'google\')" alt="Ent" width="193px" height="48px" class="signin-btn"> </div> <div ng-if="auth.isAuthenticated()"> <button class="btn btn-block btn-primary" ui-sref="home">Entrar novamente</button> <br> <button class="btn btn-block btn-danger" ng-click="auth.logout()">Sair</button> </div> </div> </div> </div>'),a.put("views/main.html",'<div class="container-fluid main-pallete-one"> <div class="row"> <div class="col-md-6 columns column-primary"> <div class="column-header"> <img src="images/logo-sm.26d80406.png" alt="As Diferentonas" width="100px" height="100px"> </div> <form name="saveMessageForm" novalidate ng-submit="main.saveMessage();" class="form-area"> <div class="form-group"> <input type="text" id="titulo" name="title" placeholder="Título do alerta" class="form-control" ng-model="main.message.title" required> </div> <div class="form-group"> <textarea id="mensagem" name="message" rows="8" cols="40" placeholder="Mensagem do alerta" class="form-control text-area" ng-model="main.message.message" required></textarea> </div> <div class="text-right"> <button type="submit" class="btn btn-lg btn-success">Publicar</button> </div> </form> <div class="input-group search-container"> <input type="text" class="form-control" placeholder="Pesquisar alerta..." ng-model="main.searchMessageTerm.titulo"> <span class="input-group-btn"> <button class="btn btn-default" type="button"><span class="glyphicon glyphicon-search"></span></button> </span> </div> <df-message-card ng-repeat="message in main.messages | filter:main.searchMessageTerm:strict" message="message" delete-function="main.deleteMessage(message)"> </df-message-card> </div> <div class="col-md-6 columns column-secondary main-pallet-two"> <div class="column-header"></div> <input type="text" ng-model="main.searchCitizenTerm" uib-typeahead="citizens for citizens in main.getCitizens($viewValue)" typeahead-min-length="4" typeahead-loading="loadingLocations" typeahead-no-results="noResults" class="form-control" placeholder="Para cadastrar um novo funcionário, comece pesquisando seu nome"> <div class="list-group"> <button type="button" class="list-group-item" ng-repeat="citizen in main.citizens" ng-click="main.selectCitizen(citizen)">{{citizen.login}}</button> </div> <div class="panel panel-default panel-theme" ng-if="main.selectedCitizen.login"> <div class="panel-body"> <form name="saveStaffForm" novalidate ng-submit="main.saveStaff();"> <div class="form-group"> <input type="text" id="titulo" name="login" class="form-control" ng-model="main.selectedCitizen.login" readonly> </div> <div class="form-group"> <input type="text" id="titulo" name="ministry" placeholder="Ministério de afiliação" class="form-control" ng-model="main.selectedCitizen.ministerioDeAfiliacao" required> </div> <div class="text-right"> <button type="submit" class="btn btn-lg btn-success">Cadastrar como funcionário do Governo</button> </div> </form> </div> </div> <df-staff-card ng-repeat="citizen in main.staff" citizen="citizen" delete-function="main.deleteStaff(citizen)"> </df-staff-card> </div> </div> </div>')}]);