<?php

	/**
	 * payload.inc.php
	 * Payload helper class
	 *
	 * Version: 	1.0
	 * Author(s):	biohzrdmx <github.com/biohzrdmx>
	 */

	class Payload {

		function __construct() {

			$this->result = 'error';
			$this->code = 500;
			$this->message = "Error";
		}

		/**
		 * Parse a Base64-encoded string, optionally returning a default value if decoding fails
		 * @param  string $data    A Base64-encoded string
		 * @param  mixed $default  A default value to return if decoding fails
		 * @return mixed           The decoded array/object or the specified default value
		 */
		static function fromBase64($data, $default = '') {
			$ret = base64_decode($data);
			return $ret ? $ret : $default;
		}

		/**
		 * Parse a JSON-encoded string, optionally returning a default value if decoding fails
		 * @param  string $data    A JSON-encoded string
		 * @param  mixed $default  A default value to return if decoding fails
		 * @return mixed           The decoded array/object or the specified default value
		 */
		static function fromJSON($data, $default = '') {
			$ret = json_decode($data);
			return $ret ? $ret : $default;
		}

		/**
		 * Parse a serialized string, optionally returning a default value if decoding fails
		 * @param  string $data    A serialized string
		 * @param  mixed $default  A default value to return if decoding fails
		 * @return mixed           The decoded array/object or the specified default value
		 */
		static function fromString($data, $default = '') {
			$ret = unserialize($data);
			return $ret ? $ret : $default;
		}

		/**
		 * Convert the payload into a Base64-encoded string
		 * @param  boolean $echo Whether to print-out the result or not
		 * @return string        A Base64-encoded string
		 */
		function toBase64($echo = false) {
			$ret = base64_encode($this);
			if ($echo) {
				echo $ret;
			}
			return $ret;
		}

		/**
		 * Convert the payload into a JSON-encoded string
		 * @param  boolean $echo Whether to print-out the result or not
		 * @return string        A JSON-encoded string
		 */
		function toJSON($echo = false) {
			$ret = json_encode($this);
			if ($echo) {
				echo $ret;
			}
			return $ret;
		}

		/**
		 * Convert the payload into a serialized string
		 * @param  boolean $echo Whether to print-out the result or not
		 * @return string        A serialized string
		 */
		function toString($echo = false) {
			$ret = serialize($this);
			if ($echo) {
				echo $ret;
			}
			return $ret;
		}

		function codeResponse($code, $message = '') {

			switch ($code) {

				case 200:
					$this->result = 'success';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Success";
				break;

				case 201:
					$this->result = 'success';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Successfully created";
				break;

				case 202:
					$this->result = 'success';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Successfully updated";
				break;

				case 203:
					$this->result = 'success';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Successfully deleted";
				break;

				case 400:
					$this->result = 'error';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Parameters incorrect";
				break;

				case 401:
					$this->result = 'error';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Parameters missing";
				break;

				case 500:
					$this->result = 'error';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Error";
				break;

				case 501:
					$this->result = 'error';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Error trying to create";
				break;

				case 502:
					$this->result = 'error';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Error trying to update";
				break;

				case 503:
					$this->result = 'error';
					$this->code = $code;
					$this->message = ($message != '') ? $message : "Error trying to delete";
				break;

				default:
				break;
			}

			return $this;
		}

	}

?>