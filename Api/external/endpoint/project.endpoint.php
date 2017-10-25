<?php

	class EndpointProject extends Endpoint {

		function init() {

			global $app;

			# Add endpoint routes
			$app->addRoute('post', '/project/new',          'EndpointProject::newProject');
			$app->addRoute('post', '/projects',          'EndpointProject::projects');
			$app->addRoute('post', '/project',          'EndpointProject::project');
			$app->addRoute('post', '/project/edit',          'EndpointProject::editProject');
			$app->addRoute('post', '/project/close',          'EndpointProject::closeProject');

			# Add endpoints to create tasks into a porject
			$app->addRoute('post', '/project/task/new',          'EndpointProject::newProjectTask');
			$app->addRoute('post', '/project/tasks',          'EndpointProject::projectTasks');
		}

		static function newProject() {

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
					$name = $request->post('name', '');
					$description = $request->post('description', '');

					# Getting image
					$file = $request->files('image');

					$project = new Project();
					$project->name = $name;
					$project->description = $description;
					$project->save();

					if ($project) {

						$project->cover = '';

						# Saving and match the image
						if ( $file && $file['name'] ) {
							$attachment = Attachments::upload($file);
							$project->updateMeta('image', $attachment->id);
							if ($attachment) {
								$project->cover = $attachment->getImage();
							}
						}

						$project->assingUser($user_id);
						$ret->codeResponse(201);
						$ret->data = $project;

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

		static function projects() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			# Token
			$client = Authentication::requireToken();

			# Payload
			$ret = new Payload();

			# Bearear
			$user_id = $request->post('user_id', 0);
			$bearer = Tokenizr::getData($request->post('bearer'));

			if ($bearer != '' && $user_id != 0) {

				if (Authentication::requireBearer($user_id, $bearer)) {

					$projects = Projects::getUserProjects($user_id);

					if ($projects) {

						$ret->codeResponse(200);
						$ret->data = $projects;

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

		static function project() {

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
					$project_id = $request->post('project_id');

					# Getting project
					$project = Projects::getById($project_id);
					if ($project) {

						$project->tasks = Tasks::getProjectTasks($project->id);
						$project->tasksCount = Tasks::getProjectTasksCount($project->id);
						$ret->codeResponse(200);
						$ret->data = $project;
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

		static function editProject() {

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

					$project_id = $request->post('project_id');
					$name = $request->post('name');
					$description = $request->post('description');
					
					# Getting image
					$file = $request->files('image');

					$project = Projects::getById($project_id);

					if ($project) {

						$project->name = $name;
						$project->description = $description;
						$project->save();

						$project->cover = '';

						# Saving and match the image
						if ( $file && $file['name'] ) {
							$attachment = Attachments::upload($file);
							$project->updateMeta('image', $attachment->id);
							if ($attachment) {
								$project->cover = $attachment->getImage();
							}
						}

						$ret->codeResponse(202);
						$ret->data = $project;

					} else {
						$ret->codeResponse(500, "Project doesn't exists");
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

		static function closeProject() {

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

					$project_id = $request->post('project_id');
					$project = Projects::getById($project_id);

					if ($project) {

						$project->complete = 1;
						$project->save();
						$ret->codeResponse(200);

					} else {
						$ret->codeResponse(500, "Project doesn't exists");
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

		static function newProjectTask() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			# Token
			$client = Authentication::requireToken();

			# Payload
			$ret = new Payload();

			# Bearear
			$user_id = $request->post('user_id', 0);
			$bearer = Tokenizr::getData($request->post('bearer'));
			$project_id = $request->post('project_id', 0);

			if ($bearer != '' && $user_id != 0 && $project_id != 0) {

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

						$task->assignProject($project_id); // Assign the user
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

		static function projectTasks() {

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
					$project_id = $request->post('project_id');

					$tasks = Tasks::getProjectTasks($project_id);

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