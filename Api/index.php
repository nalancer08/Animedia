<?php
	/**
	 * Dragonfly
	 * Version: 	1.0
	 * Author(s):	biohzrdmx <github.com/biohzrdmx>
	 */

	# Define the root path
	define( 'BASE_PATH', dirname(__FILE__) );

	# Include required files
	include BASE_PATH . '/include/config.inc.php';
	include BASE_PATH . '/include/app.inc.php';
	include BASE_PATH . '/include/request.inc.php';
	include BASE_PATH . '/include/response.inc.php';
	include BASE_PATH . '/include/managment.inc.php';
	include BASE_PATH . '/include/payload.inc.php';

	# Initialize environment
	$app = new App($settings);

	# Initialize plugins
	foreach ($app->getPlugins() as $plugin) {
		$file = $app->baseDir("/plugins/{$plugin}/plugin.php");
		include $file;
	}

	# External functions
	include $app->baseDir('/external/functions.inc.php');

	# Do routing
	$app->routeRequest();
?>