<?php

	class Authentication {

		static function generateToken($app_id) {
			global $app;
			$app_key = $app->getOption('app_key');
			$app_clients = $app->getOption('app_clients');
			# HMAC-hash the token
			$digest = hash_hmac('sha256', $app_key, $app_id);
			$token = "{$digest}.{$app_id}";
			$ret = isset( $app_clients[$app_id] ) ? $token : false;
			return $ret;
		}

		static function checkToken($app_id, $token) {
			global $app;
			$check = self::generateToken($app_id);
			$app_clients = $app->getOption('app_clients');
			# Check HMAC-hashed token
			$ret = ( md5($check) === md5($token) && isset( $app_clients[$app_id] ) );
			return $ret;
		}

		static function requireToken() {

			global $app;
			$response = $app->getResponse();
			$request = $app->getRequest();

			# Revision 1.1 - Getting the clients
			$app_clients = $app->getOption('app_clients');

			# Get request paramaters
			$token = $request->get('token');
			# Extract the app ID
			$app_id = substr($token, strrpos($token, '.') + 1);
			# And check the token
			if ( !$app_id || !$token || !self::checkToken($app_id, $token) ) {
				# Create a new payload
				$ret = new Payload();
				$ret->result = 'error';
				$ret->message = 'A valid App Token is required for accesing this API endpoint.';
				# Return payload
				$response->setStatus(403);
				$response->setBody( $ret->toJSON() );
				$response->respond();
				exit;
			} else {
				$app_clients[$app_id]['id'] = $app_id;
				return $app_clients[$app_id];
			}
		}

		static function requiereTokenFrom($client) {

			global $app;
			$response = $app->getResponse();
			$request = $app->getRequest();

			# Revision 1.1 - Getting the clients
			$app_clients = $app->getOption('app_clients');

			# Get request paramaters
			$token = $request->get('token');
			# Extract the app ID
			$app_id = substr($token, strrpos($token, '.') + 1);
			# And check the token
			if ( !$app_id || !$token || !self::checkToken($app_id, $token) ) {
				# Create a new payload
				$ret = new Payload();
				$ret->result = 'error';
				$ret->message = 'A valid App Token is required for accesing this API endpoint.';
				# Return payload
				$response->setStatus(403);
				$response->setBody( $ret->toJSON() );
				$response->respond();
				exit;
			} else {
				if ( $app_clients[$app_id]['key'] != $client ) {
					# Create a new payload
					$ret = new Payload();
					$ret->result = 'error';
					$ret->message = 'No valid App Token, it doesn\'t correspond to from ToolBelt.';
					# Return payload
					$response->setStatus(500);
					$response->setBody( $ret->toJSON() );
					$response->respond();
				} 
			}

		}

		static function requireTokenForToolBeltTransactions() {

			Authentication::requiereTokenFrom('toolbelt');
		}


		static function requireBearer($user, $bearer) {
			
			global $app;
			$ret = false;
			$check = $app->hashToken($user);
			$ret = ($bearer == $check);
			return $ret;
		}

		// static function getRequestingAppId() {
		// 	global $app;
		// 	$request = $app->getRequest();
		// 	$ret = '';
		// 	# Get request paramaters
		// 	$token = $request->get('token');
		// 	if ($token) {
		// 		# Extract the app ID
		// 		$ret = substr($token, strrpos($token, '.') + 1);
		// 	}
		// 	return $ret;
		// }
	}
?>