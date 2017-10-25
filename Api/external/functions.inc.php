<?php

	# Utility functions
	include $app->baseDir('/external/utilities.inc.php');

	# Base classes
	include $app->baseDir('/external/model.inc.php');
	include $app->baseDir('/external/norm.inc.php');
	include $app->baseDir('/external/crood.inc.php');
	include $app->baseDir('/external/endpoint.inc.php');
	include $app->baseDir('/external/tokenizr.inc.php');
	include $app->baseDir('/external/validator.inc.php');
	include $app->baseDir('/external/authentication.inc.php');

	# Endpoints
	include $app->baseDir('/external/endpoint/toolbelt.endpoint.php');
	include $app->baseDir('/external/endpoint/app.endpoint.php');
	include $app->baseDir('/external/endpoint/user.endpoint.php');
	include $app->baseDir('/external/endpoint/task.endpoint.php');
	include $app->baseDir('/external/endpoint/project.endpoint.php');

	# Models
	include $app->baseDir('/external/model/user.model.php');
	include $app->baseDir('/external/model/attachment.model.php');

	include $app->baseDir('/external/model/project.model.php');
	include $app->baseDir('/external/model/task.model.php');
	include $app->baseDir('/external/model/organization.model.php');

	# Incluimos las librearías de control
	include $app->baseDir('/lib/PasswordHash.php');
	include $app->baseDir('/lib/Random.php');
	// include $app->baseDir('/external/csbuddy.inc.php');

	# Enable CORS for all domains - TURN OFF FOR PRODUCTION!!!
	header('Access-Control-Allow-Origin:*');

	# Sandbox or not
	$app->sandbox = get_item($_GET, 'sandbox', false);

	# Register endpoints
	$endpoints = array();
	$endpoints['app'] = new EndpointApp();
	$endpoints['user'] = new EndpointUser();
	$endpoints['task'] = new EndpointTask();
	$endpoints['project'] = new EndpointProject();
	$endpoints['toolbelt'] = new EndpointToolBelt();

	if ($app->sandbox) {
		# Include experimental models
		// include $app->baseDir('/external/model/test.model.php');
		# And register experimental endpoints
		// $endpoints['test'] = new EndpointTest();
	}

?>