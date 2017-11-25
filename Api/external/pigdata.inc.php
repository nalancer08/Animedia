<?php

	/**
	 * pigdata.inc.php
	 * Pig Data Class
	 *
	 * Version: 	1.0
	 * Author(s):	nalancer08 <github.com/nalancer08> <erick.sanchez@appbuilders.com.mx>
	 */

	class PigData {

		private $url;
		private $api_url = 'http://appbuilders.com.mx:1234/apis/pigdata/';
		private $debug_url = 'http://localhost:8080/appbuilders/apis/pigdata/';
		private $app_id = 'be72d1a7d3f0b1c52d95089056f202fe';
		private $umc_endpoint = 'umc/';
		private $am_endpoint = 'am/';
		private $debug = false;

		function __construct($debug = false) {

			$this->debug = $debug;
			$this->url = (!$this->debug) ? $this->api_url : $this->debug_url;
		}

		function symphony() {

			$url = "{$this->url}symphony";
			$params = array();
			$params['app_id'] = $this->app_id;
			$token = $this->curl_request($url, 'post', $params);

			if ($token) {

				$parse = json_decode($token);
				$data = $parse->data;
				
				if ($data != '') {
					return $data;
				}
			}
			return false;
		}

		function addResponse($params) {

			$url = "{$this->url}{$this->am_endpoint}response/new";
			$this->curl_request($url, 'post', $params);
		}

		function createUser($token, $params) {

			$url = "{$this->url}{$this->umc_endpoint}user/new/?token={$token}";
			$user = $this->curl_request($url, 'post', $params);

			if ($user) {

				$parse = json_decode($user);

				if ($parse->status == 200 || $parse->status == '200') {

					$data = $parse->data;
				
					if ($data != '') {
						return $data;
					}
				}
				return false;
			}
			return false;
		}

		function createProduct($token, $params) {

			$url = "{$this->url}{$this->am_endpoint}product/new/?token={$token}";
			$product = $this->curl_request($url, 'post', $params);

			if ($product) {

				$parse = json_decode($product);

				if ($parse->status == 200 || $parse->status == '200') {

					$data = $parse->data;
				
					if ($data != '') {
						return $data;
					}
				}
				return false;
			}
			return false;
		}

		private function curl_request($url, $method, $params = array(), $scheme = 'http') {

			global $site;
			# Create query string
			$query = http_build_query($params);
			if ($query && $method == 'get') {
				$url = "{$url}&{$query}";
			}
			# Open connection
			$ch = curl_init();
			# Set the url, number of POST vars, POST data, etc
			curl_setopt($ch, CURLOPT_URL, $url);
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
			if ($method == 'post') {
				curl_setopt($ch, CURLOPT_POST, count($params));
				curl_setopt($ch, CURLOPT_POSTFIELDS, $query);
			}
			
			if($scheme == 'https') {
				
				curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
				curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);
				curl_setopt($ch, CURLOPT_CAINFO, $site->baseDir('/cacert.pem'));
			}

			# Execute request
			$result = curl_exec($ch);
			# Close connection
			curl_close($ch);
			# Return API response
			return $result;
		}
	}
?>