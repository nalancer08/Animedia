<?php

	class EndpointUser extends Endpoint {

		function init() {

			global $app;
			# Add endpoint routes
			$app->addRoute('post', '/login',          'EndpointUser::login');
			$app->addRoute('post', '/register',          'EndpointUser::register');
			$app->addRoute('post', '/user/search',          'EndpointUser::searchUser');
		}

		/**
		* This service allows login into the application, it dont create any cookie
		**/
		static function login() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			# Require authentication
			$client = Authentication::requireToken();

			# Initialize payload
			$ret = new Payload();

			# Get POST variables
			$email = $request->post('email', '');
			$password = $request->post('password', '');
			$fbid = $request->post('fbid', '');
			$nicename = $request->post('nicename', '');

			# Validate
			if ( !$email || (!$password) ) {
				$ret->message = 'Todos los campos son obligatorios';
			} else {

				$user = Users::getByEmail($email);

				# Check if the user is valid
				if ( $user ) {
					if (!$fbid) { // LOGIN THROW EMAIL
						if ( Users::checkPassword($password, $user->password) ) {

							// $avatar = $user->getMeta('avatar');
							// $avatar = $avatar ? $app->urlTo("/upload/{$avatar}?".uniqid()) : ( $user->fbid ? "https://graph.facebook.com/{$user->fbid}/picture?width=250&height=250" : "http://www.gravatar.com/avatar/{$user->hash}?s=150&d=mm" );
							
							# Setting other values to the user
							//$user->organization = $user->getAssignedOrganization();
							$user->avatar = 'http://localhost/appbuilders/du/images/icons/user.png';
							unset( $user->created );
							unset( $user->modified );
							unset( $user->password );
							unset( $user->type );
							unset( $user->status );
							unset( $user->metas );
							unset( $user->hash );
							// $user->hash = md5($user->email);
							// $user->avatar = $avatar;
							$bearer = $app->hashToken($user->id);
							$bearer = Tokenizr::getToken($bearer);
							#
							$ret->codeResponse(200);
							$ret->data = array(
								'user' => $user,
								'bearer' => $bearer
							);
						} else {
							$ret->message = 'La contraseña introducida no es válida para el usuario';
						}
					} else { // LOGIN THROW FACEBOOK

						if ($user->fbid == $fbid) {

							# Setting other values to the user
							//$user->organization = $user->getAssignedOrganization();
							$user->avatar = "https://graph.facebook.com/{$fbid}/picture?width=250&height=250";

							unset( $user->created );
							unset( $user->modified );
							unset( $user->password );
							unset( $user->type );
							unset( $user->status );
							unset( $user->metas );
							unset( $user->hash );
							// $user->hash = md5($user->email);
							// $user->avatar = $avatar;
							$bearer = $app->hashToken($user->id);
							$bearer = Tokenizr::getToken($bearer);
							#
							$ret->codeResponse(200);
							$ret->data = array(
								'user' => $user,
								'bearer' => $bearer
							);
						} else {
							$ret->message = 'La contraseña introducida no es válida para el usuario';
						}
					}
				} else {

					if ($fbid) {

						$user = new User();
						$user->login = $email;
						$user->email = $email;
						$user->fbid = $fbid;
						$user->password = $fbid;
						$user->type = 'Basic'; // Medium -- Pro -- Administrator
						$user->nicename = $nicename;
						$user->save();

						# Update metas
						$permissions = array('create_tasks', 'create_projects', 'close_tasks', 'open_tasks', 'join_to_projects', 'join_to_organization');
						$user->updateMeta('permissions', $permissions);

						# Setting other values to the user
						//$user->organization = $user->getAssignedOrganization();
						$user->avatar = "https://graph.facebook.com/{$fbid}/picture?width=250&height=250";

						unset( $user->created );
						unset( $user->modified );
						unset( $user->password );
						unset( $user->type );
						unset( $user->status );
						unset( $user->metas );
						unset( $user->hash );
						// $user->hash = md5($user->email);
						// $user->avatar = $avatar;
						$bearer = $app->hashToken($user->id);
						$bearer = Tokenizr::getToken($bearer);
						#
						$ret->codeResponse(200);
						$ret->data = array(
							'user' => $user,
							'bearer' => $bearer
						);
					
					} else {
						$ret->message = 'El usuario especificado no existe';
					}
				}
			}
			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, $client['key'], ($user) ? $user->id : '');
			return $response->respond();
		}

		/**
		* This service allow to be register into the system
		**/
		static function register() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			# Require authentication
			Authentication::requireToken();

			# Initialize payload
			$ret = new Payload();

			# Get GET variables
			$organization_id = $request->get('thread', '');

			# Get POST variables
			$email = $request->post('email');
			$first_name = $request->post('first_name');
			$last_name = $request->post('last_name');
			$password = $request->post('password');

			$user = Users::getByLogin($email);

			if (!$user) { // New user

				$user = new User();
				$user->login = $email;
				$user->email = $email;
				$user->password = $password;
				$user->type = 'Basic'; // Medium -- Pro -- Administrator
				$user->nicename = "{$first_name} {$last_name}";
				$user->save();

				# Update metas
				$permissions = array('create_tasks', 'create_projects', 'close_tasks', 'open_tasks', 'join_to_projects', 'join_to_organization');
				$user->updateMeta('permissions', $permissions);

				# Checking organization id
				if ($organization_id != '') {
					$organization_id = Tokenizr::checkToken($organization_id) ? Tokenizr::getData($organization_id) : false;
					if ($organization_id != false) {
						$user->assingOrganization($organization_id);
					}
				}

				$ret->codeResponse(200);
				$ret->data = $user;
				$ret->message = "User created succesfully";
		    
		    } else { // El usuario ya existe

		    	$ret->codeResponse(500);
		    	$ret->message = 'User exists';
		    }

		    # Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response);
			return $response->respond();
		}

		/**
		* This service allow to search a user with nicename and email
		*
		**/
		static function searchUser() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			# Token
			$client = Authentication::requireToken();

			# Payload
			$ret = new Payload();

			# Bearear
			$bearer = $request->post('bearer');
			$user_id = $request->post('user_id', 0);
			$bearer = Tokenizr::getData($bearer);

			if ($bearer != '' && $user_id != 0) {

				if (Authentication::requireBearer($user_id, $bearer)) {

					$search = $request->post('search');
					$users = Users::search($search);
					if ($users != false) {
						$ret->codeResponse(200);
						$ret->data = $users;
					} else {
						$ret->codeResponse(500, 'No users found it');
					}
				} else {
					$ret->codeResponse(500, "No match");
				}
			} else {
				$ret->codeResponse(400);
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, $client['key'], $user_id);
			return $response->respond();
		}
	}
?>