<?php

	/**
	 * Pretty-print an array or object
	 * @param  mixed $a Array or object
	 */
	function print_a( $a ) {
		print( '<pre>' );
		print_r( $a );
		print( '</pre>' );
	}

	/**
	 * Get an item from an array, or a default value if it's not set
	 * @param  array $array    Array
	 * @param  mixed $key      Key or index, depending on the array
	 * @param  mixed $default  A default value to return if the item it's not in the array
	 * @return mixed           The requested item (if present) or the default value
	 */
	function get_item($array, $key, $default = '') {
		return isset( $array[$key] ) ? $array[$key] : $default;
	}

	/**
	 * Shim http_response_code for PHP < 5.4
	 */
	if (! function_exists('http_response_code') ) {
		function http_response_code($code = NULL) {
			if ($code !== NULL) {
				switch ($code) {
					case 100: $text = 'Continue'; break;
					case 101: $text = 'Switching Protocols'; break;
					case 200: $text = 'OK'; break;
					case 201: $text = 'Created'; break;
					case 202: $text = 'Accepted'; break;
					case 203: $text = 'Non-Authoritative Information'; break;
					case 204: $text = 'No Content'; break;
					case 205: $text = 'Reset Content'; break;
					case 206: $text = 'Partial Content'; break;
					case 300: $text = 'Multiple Choices'; break;
					case 301: $text = 'Moved Permanently'; break;
					case 302: $text = 'Moved Temporarily'; break;
					case 303: $text = 'See Other'; break;
					case 304: $text = 'Not Modified'; break;
					case 305: $text = 'Use Proxy'; break;
					case 400: $text = 'Bad Request'; break;
					case 401: $text = 'Unauthorized'; break;
					case 402: $text = 'Payment Required'; break;
					case 403: $text = 'Forbidden'; break;
					case 404: $text = 'Not Found'; break;
					case 405: $text = 'Method Not Allowed'; break;
					case 406: $text = 'Not Acceptable'; break;
					case 407: $text = 'Proxy Authentication Required'; break;
					case 408: $text = 'Request Time-out'; break;
					case 409: $text = 'Conflict'; break;
					case 410: $text = 'Gone'; break;
					case 411: $text = 'Length Required'; break;
					case 412: $text = 'Precondition Failed'; break;
					case 413: $text = 'Request Entity Too Large'; break;
					case 414: $text = 'Request-URI Too Large'; break;
					case 415: $text = 'Unsupported Media Type'; break;
					case 500: $text = 'Internal Server Error'; break;
					case 501: $text = 'Not Implemented'; break;
					case 502: $text = 'Bad Gateway'; break;
					case 503: $text = 'Service Unavailable'; break;
					case 504: $text = 'Gateway Time-out'; break;
					case 505: $text = 'HTTP Version not supported'; break;
					default:
					exit('Unknown http status code "' . htmlentities($code) . '"');
					break;
				}
				$protocol = (isset($_SERVER['SERVER_PROTOCOL']) ? $_SERVER['SERVER_PROTOCOL'] : 'HTTP/1.0');
				header($protocol . ' ' . $code . ' ' . $text);
				$GLOBALS['http_response_code'] = $code;
			} else {
				$code = (isset($GLOBALS['http_response_code']) ? $GLOBALS['http_response_code'] : 200);
			}
			return $code;
		}
	}

	/**
	 * Convert camelCase to snake_case
	 * @param  string $val Original string
	 * @return string      The converted string
	 */
	function camel_to_snake($val) {
		$val = preg_replace_callback('/[A-Z]/', create_function('$match', 'return "_" . strtolower($match[0]);'), $val);
		return ltrim($val, '_');
	}

	/**
	 * Convert snake_case to camelCase
	 * @param  string $val Original string
	 * @return string      The converted string
	 */
	function snake_to_camel($val) {
		$val = str_replace(' ', '', ucwords(str_replace('_', ' ', $val)));
		$val = strtolower(substr($val, 0, 1)).substr($val, 1);
		return $val;
	}

	/**
	 * Log something to file
	 * @param  mixed  $data     What to log
	 * @param  string $log_file Log name, without extension
	 * @return nothing
	 */
	function log_to_file($data, $log_file = '') {

		global $app;
		$log_file = $log_file ? $log_file : date('Y-m');
		$file = fopen( $app->baseDir("/log/{$log_file}.log"), 'a');
		$date = date('Y-m-d H:i:s');
		if ( is_array($data) || is_object($data) ) {
			$data = json_encode($data);
		}
		fwrite($file, "{$date} - {$data}\n");
		fclose($file);
	}

	function generate_password($length) {

		global $app;
		$ret = false;
		try {
			require_once $app->baseDir('/lib/Random.php');
			$random = new Random(false);
			$ret = $random->token($length);
		} catch(Exception $e) {
			error_log( $e->getMessage() );
		}

		return $ret;
	}

?>