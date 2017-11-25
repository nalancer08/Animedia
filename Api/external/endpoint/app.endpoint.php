<?php

	class EndpointApp extends Endpoint {

		function init() {

			
			global $app;
			# Add endpoint routes
			$app->addRoute('*', '/status',          'EndpointApp::status');
			$app->addRoute('*', '/token',          'EndpointApp::token');

			# Override default route
			$app->setDefaultRoute('/status');

			#20747630d1e1b9ffa4993a11a96d2c3e0f4f8fe6256d9f4c176b69a02481291b.be72d1a7d3f0b1c52d95089056f202fe
		}

		/**
		 * ALL :: /status
		 */
		static function status() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			# Token
			$client = Authentication::requireToken();

			# Initialize payload
			$ret = new Payload();

			# Set payload
			$ret->codeResponse(200);
			$ret->data = array(
				'time' => time(),
				'sandbox' => $app->sandbox,
				'version' => $app->getGlobal('app_version'),
				'pig_data_app_uuid' => $managment->getOptionValue('pd_app_uuid')
			);
			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, $client['key'], 0);
			return $response->respond();
		}

		static function token() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			# Initialize payload
			$ret = new Payload();
			$ret->result = 'error';

			# Get POST variables
			$app_id = $request->post('app_id');

			# Generate token
			$token = Authentication::generateToken($app_id);
			if ($token) {
				$ret->codeResponse(200);
				$ret->result = 'success';
				$ret->data = $token;				
			} else {
				$ret->message = 'La applicación especificada no es válida';
			}
			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, 'system', 0);
			return $response->respond();
		}
	}
?>