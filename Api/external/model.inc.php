<?php

	/**
	 * Model class
	 *
	 * A simple wrapper for data models.
	 * You must override the init() method.
	 */
	abstract class Model {

		/**
		 * Constructor
		 */
		function __construct() {
			$params = func_get_args();
			$this->init($params);
		}

		/**
		 * Initialization callback, must be overriden in your extended classes
		 */
		abstract function init();
	}

?>