<?php

	/**
	 * Endpoint class
	 *
	 * A simple wrapper for API endpoints.
	 * You must override the init() method.
	 */
	abstract class Endpoint {

		protected $versions;

		/**
		 * Constructor
		 */
		function __construct() {
			global $app;
			# Initialize supported API versions
			$this->versions = array( $app->getVersion() );
			# Run callback
			$this->init();
		}

		/**
		 * Initialization callback, must be overriden in your extended classes
		 */
		abstract function init();
	}

?>