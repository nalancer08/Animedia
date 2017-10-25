<?php

	class EndpointTask extends Endpoint {

		function init() {

			global $app;

			# Add endpoint routes
			$app->addRoute('post', '/task/new',          'EndpointTask::newTask');
			$app->addRoute('post', '/tasks',          'EndpointTask::tasks');
			$app->addRoute('post', '/task/edit',          'EndpointTask::editTask');
			$app->addRoute('post', '/task/close',          'EndpointTask::closeTask');
		}

		static function newTask() {

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

					# Getting post variables
					$parent_id = $request->post('parent_id', 0);
					$content = $request->post('content');
					$priority = $request->post('priority', 'normal');
					$shared = $request->post('shared', 0);
					$end = $request->post('end', '');

					$task = new Task();
					$task->parent_id = $parent_id;
					$task->content = $content;
					$task->priority = $priority;
					$task->shared = $shared;
					$task->end = $end;
					$task->save();

					if ($task) {

						// Fix the data, only with new tasks
						$task->created = date('d-F-Y', strtotime($task->created));

						$task->assignUser($user_id); // Assign the user
						$ret->codeResponse(201);
						$ret->data = $task;

					} else {
						$ret->codeResponse(500, "Please try again");
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

		static function tasks() {

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

					$tasks = Tasks::getUserTasks($user_id);

					if ($tasks) {

						$ret->codeResponse(200);
						$ret->data = $tasks;

					} else {
						$ret->codeResponse(500, "No tasks available");
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

		static function editTask() {

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

					$task_id = $request->post('task_id');
					$content = $request->post('content');

					$task = Tasks::getById($task_id);

					if ($task) {

						$task->content = $content;
						$task->save();

						$ret->codeResponse(202);

					} else {
						$ret->codeResponse(500, "Task doesn't exists");
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

		static function closeTask() {

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

					$task_id = $request->post('task_id');
					$task = Tasks::getById($task_id);

					if ($task) {

						$task->complete = 1;
						$task->save();
						$ret->codeResponse(200);

					} else {
						$ret->codeResponse(500, "Task doesn't exists");
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

		static function base() {

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