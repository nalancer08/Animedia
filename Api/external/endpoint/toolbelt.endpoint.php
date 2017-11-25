<?php

	class EndpointToolBelt extends Endpoint {

		function init() {

			
			global $app;
			# Add endpoint routes
			$app->addRoute('get', '/toolbelt/status',          'EndpointToolBelt::status');

			$app->addRoute('post', '/toolbelt/synchronize',     'EndpointToolBelt::synchronize');

			$app->addRoute('get', '/toolbelt/routes',          'EndpointToolBelt::routes');

			$app->addRoute('post', '/toolbelt/load/global',          'EndpointToolBelt::globalLoad');
			$app->addRoute('post', '/toolbelt/load/route',          'EndpointToolBelt::loadByRoute');
			$app->addRoute('post', '/toolbelt/load/routes',          'EndpointToolBelt::routesStatics');
			$app->addRoute('post', '/toolbelt/load/user/:id',          'EndpointToolBelt::loadByUser');
			$app->addRoute('post', '/toolbelt/load/user/statics/:id',          'EndpointToolBelt::userStatics');

			$app->addRoute('post', '/toolbelt/distribution/global',          'EndpointToolBelt::globalDistribution');
			// $app->addRoute('post', '/toolbelt/settings/turn',          'EndpointToolBelt::userResume');
			// $app->addRoute('post', '/toolbelt/settings/turn/route',          'EndpointToolBelt::userResume');
			// $app->addRoute('post', '/toolbelt/settings/turn/user:/id',          'EndpointToolBelt::userResume');

			$app->addRoute('post', '/toolbelt/users', 						  'EndpointToolBelt::users');
			$app->addRoute('post', '/toolbelt/users/status', 						  'EndpointToolBelt::users');

			$app->addRoute('post', '/toolbelt/api',								'EndpointToolBelt::getApi');

			# Override default route
			$app->setDefaultRoute('/toolbelt/status'); # App endpoint route
		}

		# df79b5a58fcc78ee50a8d2ed93ed9b2c2ba6c9fdc146f04edf207c479aca86ab.32407f13b8a1ceb1556221546346abee
		# android - 23aeadddd74928cadf2c0a3b144ab0462f214364470168c6565768e0fb60faa6.e6fda0f0d3e0adfff69e334462d1ef6a
		# select count(route) as populate, SUM(size) as bytes from managment_request WHERE route = "/status";

		static function status() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			Authentication::requireTokenForToolBeltTransactions();

			$ret = new Payload();
			$ret->codeResponse(200);
			$ret->data = array(
				'time' => time(),
				'status' => $managment->isOn(),
				'version' => $app->getGlobal('app_version'),
				'dragon_fly_version' =>'Hyper V.1.1',
				'toolbelt_verision' => '1.1'
			);

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function synchronize() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();
			$dbh = $app->getDatabase();

			Authentication::requireTokenForToolBeltTransactions();

			# Initialize payload
			$ret = new Payload();
			$uuid = $request->post('app_uuid', '');

			if ($uuid != '') {

				# Synchronize api/pig_data/toolbelt
				$managment->synchronize($uuid);
				$ret->codeResponse(200);
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function routes() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();
			$dbh = $app->getDatabase();

			Authentication::requireTokenForToolBeltTransactions();

			# Initialize payload
			$ret = new Payload();

			# Getting the global charge
			$result = $managment->getRoutes();
			if ( $result ) {

				$ret->codeResponse(200);
				$ret->data = $result;
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function globalLoad() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();
			$dbh = $app->getDatabase();

			Authentication::requireTokenForToolBeltTransactions();

			# Initialize payload
			$ret = new Payload();

			# Getting date filter if it's exists
			$filter = $managment->getDateFilter($request);

			# Getting the global charge with filter
			$result = $managment->getGlobalLoad($filter);
			if ( $result ) {
				$ret->codeResponse(200);
				$ret->data = $result;
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function loadByUser($id) {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			Authentication::requireTokenForToolBeltTransactions();

			$user_id = get_item($id, 1, 0);

			# Initialize payload
			$ret = new Payload();

			# Getting date filter if it's exists
			$filter = $managment->getDateFilter($request);

			# Getting the global charge
			$result = $managment->getLoadByUser($user_id, $filter);
			if ( $result ) {

				$ret->codeResponse(200);
				$ret->data = $result;
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function loadByRoute() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			Authentication::requireTokenForToolBeltTransactions();

			# Getting date filter if it's exists
			$filter = $managment->getDateFilter($request);

			$route = $request->post('route');

			# Initialize payload
			$ret = new Payload();
			$result = $managment->getRouteLoad($route, $filter);

			if ( $result ) {

				$ret->codeResponse(200);
				$ret->data = $result;
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function routesStatics() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			Authentication::requireTokenForToolBeltTransactions();

			# Getting date filter if it's exists
			$filter = $managment->getDateFilter($request);

			# Initialize payload
			$ret = new Payload();
			$routes = $managment->routesStaticsResume($filter);

			if ( $routes ) {

				$ret->codeResponse(200);
				$ret->data = $routes;
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function userStatics($id) {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			Authentication::requireTokenForToolBeltTransactions();
			$user_id = get_item($id, 1, 0);

			# Getting date filter if it's exists
			$filter = $managment->getDateFilter($request);

			# Initialize payload
			$ret = new Payload();
			$results = $managment->userStaticsResume($user_id, $filter);

			if ( $results ) {

				$ret->codeResponse(200);
				$ret->data = $results;
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function globalDistribution() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			Authentication::requireTokenForToolBeltTransactions();

			# Getting date filter if it's exists
			$filter = $managment->getDateFilter($request);

			# Initialize payload
			$ret = new Payload();
			$result = $managment->getGlobalDistribution($filter);

			if ( $result ) {

				$ret->codeResponse(200);
				$ret->data = $result;
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function users() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			Authentication::requireTokenForToolBeltTransactions();

			# Initialize payload
			$ret = new Payload();

			# Getting page
			$page = $request->post('page', 1);

			$maxItems = 50;
			$users = $managment->getUsers($maxItems, $page);
			$count = Users::count();
			$pages = ($maxItems > $count) ? 1 : ($count / $maxItems);

			$ret->codeResponse(200);
			$ret->data = $users;
			$ret->count = $count;
			$ret->pages = $pages;
			
			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		static function getApi() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			Authentication::requireTokenForToolBeltTransactions();

			# Initialize payload
			$ret = new Payload();

			# Getting the api file
			$file = $managment->getApi();

			if ($file) {
				$ret->codeResponse(200);
				$ret->data = $file;
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			return $response->respond();
		}

		// static function turnOnOffApi() {}

		// static function turnOnOffRoute() {}
		
		// static function turnOnOffUser($id) {}
	}
?>