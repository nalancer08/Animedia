<?php

	/**
	 * app.inc.php
	 * This class is the core of Dragonfly, so please try to keep it backwards-compatible if you modify it.
	 *
	 * Version: 	1.1
	 * Author(s):	biohzrdmx <github.com/biohzrdmx>
	 * Reviews(s):	nalancer08 <github.com/nalancer08> <erick.sanchez@appbuilders.com.mx>
	 */

	class App {

		protected $base_url;
		protected $base_dir;
		protected $routes;
		protected $default_route;
		protected $dirs;
		protected $request;
		protected $response;
		protected $managment;
		protected $request_string;
		protected $params;
		protected $plugins;
		protected $pass_salt;
		protected $token_salt;
		protected $hooks;
		protected $profile;
		protected $version;
		protected $version_req;
		protected $version_expr;
		protected $dbh;

		/**
		 * Class constructor
		 */
		function __construct($settings) {
			
			# Load settings
			$this->profile = $settings[PROFILE];
			$this->globals = $settings['shared'];
			$this->base_dir = BASE_PATH;
			$this->version = VERSION;
			$this->base_url = $this->profile['site_url'];
			# Version pattern (defaults to 'X.X/')
			$this->version_expr = '/^(\d\.\d)\//';
			$this->version_req = $this->version;
			# Create arrays
			$this->routes = array();
			$this->params = array();
			$this->hooks = array();
			$this->plugins = $this->profile['plugins'];
			# Initialize variables
			$this->pass_salt = $settings['shared']['pass_salt'];
			$this->token_salt = $settings['shared']['token_salt'];
			$this->app_name = $settings['shared']['app_name'];
			$this->request = new Request();
			$this->response = new Response();
			$this->managment = new Managment();
			# Default dirs
			$this->dirs = array(
				'plugins' => '/plugins'
			);
			# Create database connection
			try {
				switch ( $this->profile['db_driver'] ) {
					case 'sqlite':
						$dsn = sprintf('sqlite:%s', $this->profile['db_file']);
						$this->dbh = new PDO($dsn);
						break;
					case 'mysql':
						$dsn = sprintf('mysql:host=%s;dbname=%s', $this->profile['db_host'], $this->profile['db_name']);
						$this->dbh = new PDO($dsn, $this->profile['db_user'], $this->profile['db_pass']);
						break;
				}
				# Change error and fetch mode
				if ($this->dbh) {
					$this->dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
					$this->dbh->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_OBJ);
				}
			} catch (PDOException $e) {
				error_log( $e->getMessage() );
				$this->errorMessage( 'Database error ' . $e->getCode() );
			}
		}

		/**
		 * Get app version
		 * @return string The app version
		 */
		function getVersion() {

			return $this->version;
		}

		/**
		 * Get the requested version of the API
		 * @return string The version of the API, as specified in the current request
		 */
		function getRequestVersion() {

			return $this->version_req;
		}

		/**
		 * Get the version-matching regular expression
		 * @return string The current pattern
		 */
		function getVersionExpr() {

			return $this->version_expr;
		}

		/**
		 * Set the version-matching regular expression
		 * @param string $pattern The new pattern
		 */
		function setVersionExpr($pattern) {

			$this->version_expr = $pattern;
		}

		/**
		 * Get base folder
		 * @param  string  $path Path to append
		 * @param  boolean $echo Whether to print the resulting string or not
		 * @return string        The well-formed path
		 */
		function baseDir($path = '', $echo = false) {

			$ret = sprintf('%s%s', $this->base_dir, $path);
			if ($echo) {
				echo $ret;
			}
			return $ret;
		}

		/**
		 * Get base URL
		 * @param  string  $path     Path to append
		 * @param  boolean $echo     Whether to print the resulting string or not
		 * @param  string  $protocol Protocol to override default http (https, ftp, etc)
		 * @return string            The well-formed URL
		 */
		function baseUrl($path = '', $echo = false, $protocol = null) {
			$base_url = rtrim($this->base_url, '/');
			if (!$protocol && isset($_SERVER['HTTPS']) ) {
				$base_url = str_replace('http://', 'https://', $base_url);
			} else if ($protocol) {
				$protocol .= strrpos($protocol, ':') > 0 ? '' : ':';
				$base_url = str_replace('http:', $protocol, $base_url);
			}
			if ( !empty($path) && $path[0] != '/' ) {
				$path = '/' . $path;
			}
			$ret = sprintf('%s%s', $base_url, $path);
			# Print and/or return the result
			if ($echo) {
				echo $ret;
			}
			return $ret;
		}

		/**
		 * Get the specified directory path
		 * @param  string  $dir  Directory name
		 * @param  boolean $full Whether to return a relative or fully-qualified path
		 * @return mixed         The path to the specified directory or False if it doesn't exist
		 */
		function getDir($dir, $full = true) {
			if ( isset( $this->dirs[$dir] ) ) {
				return ($full ? $this->baseDir( $this->dirs[$dir] ) : $this->dirs[$dir]);
			}
			return false;
		}

		/**
		 * Set the path to the specified directory
		 * @param string $dir  Directory name, if it exists it will be overwritten
		 * @param string $path Path to the directory, relative to the site root
		 */
		function setDir($dir, $path) {

			$this->dirs[$dir] = $path;
		}

		/**
		 * Add a new route
		 * @param  string  $route     Parametrized route
		 * @param  string  $functName Handler function name
		 * @param  boolean $insert    If set, the route will be inserted at the beginning
		 */
		function addRoute($method, $route, $functName, $insert = false) {

			$method = strtolower($method);
			if (! isset( $this->routes[$method] ) ) {
				$this->routes[$method] = array();
			}
			if ($insert) {
				$this->routes[$method] = array_reverse($this->routes[$method], true);
			    $this->routes[$method][$route] = $functName;
			    $this->routes[$method] = array_reverse($this->routes[$method], true);
			} else {
				$this->routes[$method][$route] = $functName;
			}
		}

		/**
		 * Removes the specified route
		 * @param  string $route Parametrized route
		 * @return boolean       True if the route was found and removed, false otherwise
		 */
		function removeRoute($route) {

			if ( isset( $this->routes[$route] ) ) {
				unset( $this->routes[$route] );
				return true;
			}
			return false;
		}

		/**
		 * Get the default route
		 * @return string The default route
		 */
		function getDefaultRoute() {

			return $this->default_route;
		}

		/**
		 * Set the default route
		 * @param string $route Full route, defaults to '/home'
		 */
		function setDefaultRoute($route) {

			$this->default_route = $route;
		}

		/**
		 * Process current request
		 * @return boolean TRUE if routing has succeeded, FALSE otherwise
		 */
		function routeRequest() {

			# Routing stuff, first get the site url
			$site_url = trim($this->base_url, '/');

			# Remove the protocol from it
			$domain = preg_replace('/^(http|https):\/\//', '', $site_url);

			# Now remove the path
			$segments = explode('/', $domain, 2);
			if (count($segments) > 1) {
				$domain = array_pop($segments);
			}

			# Get the request and remove the domain
			$request = trim($_SERVER['REQUEST_URI'], '/');
			$request = preg_replace("/".str_replace('/', '\/', $domain)."/", '', $request, 1);
			$request = ltrim($request, '/');

			# Remove version string
			if ( preg_match($this->version_expr, $request, $matches) === 1 ) {
				# Save the specified version
				$this->version_req = $matches[1];
			}
			$request = preg_replace("/^{$this->version_req}\//", '', $request);

			# Save current request string
			$this->request_string = $request;

			# Get the parameters
			$segments = explode('?', $request);
			if (count($segments) > 1) {
				$params_str = array_pop($segments);
				parse_str($params_str, $this->params);
			}

			# And the segments
			$cur_route = array_shift($segments);
			$segments = explode('/', $cur_route);

			# Now make sure the current route begins with '/' and doesn't end with '/'
			$cur_route = '/' . $cur_route;
			$cur_route = rtrim($cur_route, '/');

			# Make sure we have a valid route
			if ( empty($cur_route) ) {
				$cur_route = $this->default_route;
			}

			$this->request->type = strtolower( isset( $_SERVER['HTTP_X_HTTP_METHOD_OVERRIDE'] ) ? $_SERVER['HTTP_X_HTTP_METHOD_OVERRIDE'] : $_SERVER['REQUEST_METHOD'] );

			if (! $this->matchRoute($cur_route) ) {
				# Nothing was found, show a 404 page
				$this->errorMessage('No routes match the current request');
				return false;
			} else {
				ob_start();
				$this->response->write( ob_get_clean() );
				return true;
			}
		}

		/**
		 * Try to match the given route with one of the registered handlers and process it
		 * @param  string $route  		The route to match
		 * @return boolean        		TRUE if the route matched with a handler, FALSE otherwise
		 */
		function matchRoute($spec_route) {

			# And try to match the route with the registered ones
			$matches = array();
			# Get request type
			$type = $this->request->type;
			# Now try to find an appropiate handler according to the type
			if ( isset( $this->routes[$type] ) ) {
				foreach ($this->routes[$type] as $route => $handler) {
					# Compile route into regular expression
					$a = preg_replace('/[\-{}\[\]+?.;,\\\^$|#\s]/', '\\$&', $route); // escapeRegExp
					$b = preg_replace('/\((.*?)\)/', '(?:$1)?', $a);                // optionalParam
					$c = preg_replace('/(\(\?)?:\w+/', '([^\/]+)', $b);             // namedParam
					$d = preg_replace('/\*\w+/', '(.*?)', $c);                      // splatParam
					$pattern = "~^{$d}$~";
					$handler_parts = explode('::', $handler);
					if ( preg_match($pattern, $spec_route, $matches) == 1) {

						# Revision 1.1
						# Added Api Managment, right now, you can turn off the api
						if ( !$this->managment->isOn() && $handler_parts[0] != 'EndpointToolBelt' ) {
							http_response_code(500);
							exit;
						}

						# We've got a match, try to route with this handler
						$this->request->endpoint = $handler_parts[0];
						$this->request->method = $handler_parts[1];
						$this->request->route = $route;
						$ret = call_user_func($handler, $matches);
						if ($ret) {
							# Exit the loop only if the handler did its job
							return true;
						}
					}
				}
			}
			# Now try with the wildcard ones
			if ( isset( $this->routes['*'] ) ) {
				foreach ($this->routes['*'] as $route => $handler) {
					# Compile route into regular expression
					$a = preg_replace('/[\-{}\[\]+?.;,\\\^$|#\s]/', '\\$&', $route); // escapeRegExp
					$b = preg_replace('/\((.*?)\)/', '(?:$1)?', $a);                // optionalParam
					$c = preg_replace('/(\(\?)?:\w+/', '([^\/]+)', $b);             // namedParam
					$d = preg_replace('/\*\w+/', '(.*?)', $c);                      // splatParam
					$pattern = "~^{$d}$~";
					$handler_parts = explode('::', $handler);
					if ( preg_match($pattern, $spec_route, $matches) == 1) {

						# Revision 1.1
						# Added Api Managment, right now, you can turn off the api
						if ( !$this->managment->isOn() && $handler_parts[0] != 'EndpointToolBelt' ) {
							http_response_code(500);
							exit;
						}

						# We've got a match, try to route with this handler
						$this->request->endpoint = $handler_parts[0];
						$this->request->method = $handler_parts[1];
						$this->request->route = $route;
						$ret = call_user_func($handler, $matches);
						if ($ret) {
							# Exit the loop only if the handler did its job
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * Get the registered routes
		 * @return array The registered routes
		 */
		function getRoutes() {
			return $this->routes;
		}

		/**
		 * Get the current request object
		 * @return object The current request object
		 */
		function getRequest() {

			return $this->request;
		}

		/**
		 * Get the current response object
		 * @return object The current response object
		 */
		function getResponse() {

			return $this->response;
		}

		/**
		 * Get the current managment object
		 * @return object The current managment object
		 */
		function getManagment() {

			return $this->managment;
		}

		/**
		 * Get the current request string
		 * @return string The current request string
		 */
		function getCurRequest() {

			return $this->request_string;
		}

		/**
		 * Sanitize the given string (slugify it)
		 * @param  string $str       The string to sanitize
		 * @param  array  $replace   Optional, an array of characters to replace
		 * @param  string $delimiter Optional, specify a custom delimiter
		 * @return string            Sanitized string
		 */
		function toAscii($str, $replace = array(), $delimiter = '-') {

			setlocale(LC_ALL, 'en_US.UTF8');
			# Remove spaces
			if( !empty($replace) ) {
				$str = str_replace((array)$replace, ' ', $str);
			}
			# Remove non-ascii characters
			$clean = iconv('UTF-8', 'ASCII//TRANSLIT', $str);
			# Remove non alphanumeric characters and lowercase the result
			$clean = preg_replace("/[^a-zA-Z0-9\/_|+ -]/", '', $clean);
			$clean = strtolower(trim($clean, '-'));
			# Remove other unwanted characters
			$clean = preg_replace("/[\/_|+ -]+/", $delimiter, $clean);
			return $clean;
		}

		/**
		 * Get a well formed url to the specified route or page slug
		 * @param  string  $route    Route or page slug
		 * @param  boolean $echo     Whether to print out the resulting url or not
		 * @param  string  $protocol Protocol to override default http (https, ftp, etc)
		 * @return string            The resulting url
		 */
		function urlTo($route, $echo = false, $protocol = null) {

			$url = $this->baseUrl($route, false, $protocol);
			if ($echo) {
				echo $url;
			}
			return $url;
		}

		/**
		 * Check if the current request was made via AJAX
		 * @return boolean Whether the request was made via AJAX or not
		 */
		function isAjaxRequest() {

			return (!empty($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH']) == 'xmlhttprequest');
		}

		/**
		 * Check if the current request was made via HTTPS
		 * @return boolean Whether the request was made via HTTPS or not
		 */
		function isSecureRequest() {

			return ( isset( $_SERVER['HTTPS'] ) );
		}

		/**
		 * Get registered plugins
		 * @return array Array of registered plugins
		 */
		function getPlugins() {

			return $this->plugins;
		}

		/**
		 * Hash the specified token
		 * @param  mixed  $action  Action name(s), maybe a single string or an array of strings
		 * @param  boolean $echo   Whether to output the resulting string or not
		 * @return string          The hashed token
		 */
		function hashToken($action, $echo = false) {

			if ( is_array($action) ) {
				$action_str = '';
				foreach ($action as $item) {
					$action_str .= $item;
				}
				$ret = md5($this->token_salt.$action_str);
			} else {
				$ret = md5($this->token_salt.$action);
			}
			if ($echo) {
				echo $ret;
			}
			return $ret;
		}

		/**
		 * Hash the specified password
		 * @param  string  $password 	Plain-text password
		 * @param  boolean $echo   		Whether to output the resulting string or not
		 * @return string          		The hashed password
		 */
		function hashPassword($password, $echo = false) {

			$ret = md5($this->pass_salt.$password);
			if ($echo) {
				echo $ret;
			}
			return $ret;
		}

		/**
		 * Validate the given token with the specified action
		 * @param  string $token  Hashed token
		 * @param  string $action Action name
		 * @return boolean        True if the token is valid, False otherwise
		 */
		function validateToken($token, $action) {

			$check = $this->haskToken($action);
			return ($token == $check);
		}

		/**
		 * Register a hook listener
		 * @param  string  $hook      Hook name
		 * @param  string  $functName Callback function name
		 * @param  boolean $prepend   Whether to add the listener at the beginning or the end
		 */
		function registerHook($hook, $functName, $prepend = false) {

			if (! isset( $this->hooks[$hook] ) ) {
				$this->hooks[$hook] = array();
			}
			if ($prepend) {
				array_unshift($this->hooks[$hook], $functName);
			} else {
				array_push($this->hooks[$hook], $functName);
			}
		}

		/**
		 * Execute a hook (run each listener incrementally)
		 * @param  string $hook   	Hook name
		 * @param  mixed  $params 	Parameter to pass to each callback function
		 * @return mixed          	The processed data or the same data if no callbacks were found
		 */
		function executeHook($hook, $param = '') {

			if ( isset( $this->hooks[$hook] ) ) {
				$hooks = $this->hooks[$hook];
				$ret = true;
				foreach ($hooks as $hook) {
					$ret = call_user_func($hook, $param);
				}
				return $ret;
			}
			return false;
		}

		/**
		 * Get the specified option from the current profile
		 * @param  string $key     Option name
		 * @param  string $default Default value
		 * @return mixed           The option value (array, string, integer, boolean, etc)
		 */
		function getOption($key, $default = '') {

			$ret = $default;
			if ( isset( $this->profile[$key] ) ) {
				$ret = $this->profile[$key];
			}
			return $ret;
		}

		/**
		 * Get the specified option from the global profile
		 * @param  string $key     Option name
		 * @param  string $default Default value
		 * @return mixed           The option value (array, string, integer, boolean, etc)
		 */
		function getGlobal($key, $default = '') {

			$ret = $default;
			if ( isset( $this->globals[$key] ) ) {
				$ret = $this->globals[$key];
			}
			return $ret;
		}

		/**
		 * Return the current database connection object
		 * @return object 			PDO instance for the current connection
		 */
		function getDatabase() {

			return $this->dbh;
		}

		/**
		 * Display a generic error message
		 * @param  string $message The error message
		 */
		function errorMessage($message) {

			$markup = '<!DOCTYPE html> <html lang="en"> <head> <meta charset="UTF-8"> <title>{$title}</title> <style> body { font-family: sans-serif; font-size: 14px; background: #F8F8F8; } div.center { width: 960px; margin: 0 auto; padding: 1px 0; } p.message { padding: 15px; border: 1px solid #DDD; background: #F1F1F1; color: #656565; } </style> </head> <body> <div class="center"> <p class="message">{$message}</p> </div> </body> </html>';
			$markup = str_replace('{$title}', $this->getGlobal('app_name'), $markup);
			$markup = str_replace('{$message}', $message, $markup);
			echo $markup;
			exit;
		}
	}
?>