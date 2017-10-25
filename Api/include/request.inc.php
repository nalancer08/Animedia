<?php

	/**
	 * request.inc.php
	 * Request helper class
	 *
	 * Version: 	1.1
	 * Author(s):	biohzrdmx <github.com/biohzrdmx>
	 * Reviews(s): 	nalancer08 <github.com/nalancer08> <erick.sanchez@appbuilders.com.mx>
	 */

	class Request {

		/**
		 * The format specifier for the expected response (html, json, xml, yaml, etc.)
		 * @var string
		 */
		public $format;

		/**
		 * HTTP method used to make the current request (get, post, etc.)
		 * @var string
		 */
		public $type;

		/**
		 * Request controller
		 * @var string
		 */
		public $controller;

		/**
		 * Request action
		 * @var string
		 */
		public $action;

		/**
		 * Request id
		 * @var string
		 */
		public $id;

		/**
		 * Request parts (controller, action, id and extra fragments)
		 * @var string
		 */
		public $parts;

		/*********************************************************************
		* 							Revision 1.1							 *
		*********************************************************************/

		/**
		* Request endpoint
		* @var String
		**/
		public $endpoint;

		/**
		* Request executed method
		* @var String
		**/
		public $method;
		
		/**
		* Request route executed (app class, match the route, and only pass the reference)
		* @var String
		**/
		public $route;

		/**
		 * Constructor
		 */
		function __construct() {
			$this->format = 'html';
			$this->type = 'get';
			$this->controller = 'index';
			$this->action = 'index';
			$this->id = '';
			$this->parts = array();
			$this->endpoint = '';
			$this->method = '';
			$this->route = '';
		}

		/**
		 * Check whether the current request is secure (HTTPS) or not
		 * @return boolean True if the request was made via HTTPS, False otherwise
		 */
		function secure() {
			return isset( $_SERVER['HTTPS'] );
		}

		/**
		 * Get a variable from the $_REQUEST superglobal
		 * @param  string $name    Variable name
		 * @param  string $default Default value to return if the variable is not set
		 * @return mixed           Variable value or $default
		 */
		function param($name, $default = '') {
			return isset( $_REQUEST[$name] ) ? $_REQUEST[$name] : $default;
		}

		/**
		 * Get a variable from the $_GET superglobal
		 * @param  string $name    Variable name
		 * @param  string $default Default value to return if the variable is not set
		 * @return mixed           Variable value or $default
		 */
		function get($name, $default = '') {
			return isset( $_GET[$name] ) ? $_GET[$name] : $default;
		}

		/**
		 * Get a variable from the $_POST superglobal
		 * @param  string $name    Variable name
		 * @param  string $default Default value to return if the variable is not set
		 * @return mixed           Variable value or $default
		 */
		function post($name, $default = '') {
			return isset( $_POST[$name] ) ? $_POST[$name] : $default;
		}

		/**
		 * Get a variable from the $_SESSION superglobal
		 * @param  string $name    Variable name
		 * @param  string $default Default value to return if the variable is not set
		 * @return mixed           Variable value or $default
		 */
		function session($name, $default = '') {
			return isset( $_SESSION[$name] ) ? $_SESSION[$name] : $default;
		}

		/**
		 * Get a file from the $_FILES superglobal
		 * @param  string $name File key
		 * @return mixed        Array with file properties or Null
		 */
		function files($name) {
			return isset( $_FILES[$name] ) ? $_FILES[$name] : null;
		}
	}

?>