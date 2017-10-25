<?php

	/**
	 * Tokenizr
	 * @author 	biohzrdmx <github.com/biohzrdmx>
	 * @version 1.0
	 * @license MIT
	 * @example Basic usage:
	 *
	 *    Use getToken() to tokenize your data. It will return an string with the source data and its message digest:
	 *
	 *      $token = Tokenizr::getToken('something');
	 *
	 *    You can then save the token wherever you want. To check it back, use checkToken()
	 *
	 *      $valid = Tokenizr::checkToken('something.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');
	 *
	 *    To get back the data, use getData()
	 *
	 *      $data = Tokenizr::getData('something.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');
	 *
	 *    Note that the data and its message digest are separated by a DOT, so it wouldn't be a good idea to try
	 *    to tokenize strings containing dots (duh). Data structures (arrays/objects) aren't supported as-is, if
	 *    you want to tokenize them, you'll have to serialize and/or base64-encode them before using this class.
	 *
	 *    Also note that the data is saved in plain sight, THIS CLASS IS NOT DESIGNED TO ENCRYPT DATA. The purpose
	 *    of this class is to provide a way to check if the data (from a cookie, for example) has been generated
	 *    by your code and not forged. The checksum is reliable as long as you NEVER DISCLOSE YOUR SALTS.
	 *
	 */
	class Tokenizr {

		static function getToken($data) {
			global $app;
			$ret = false;
			$key = $app->getGlobal('token_salt');
			$hash = hash_hmac('sha256', $data, $key);
			$ret = "{$data}.{$hash}";
			return $ret;
		}

		static function checkToken($token) {
			global $app;
			$ret = false;
			$key = $app->getGlobal('token_salt');
			$parts = explode('.', $token);
			$data = get_item($parts, 0);
			$hash = get_item($parts, 1);
			if ($data && $hash) {
				$check = hash_hmac('sha256', $data, $key);
				$ret = $hash === $check;
			}
			return $ret;
		}

		static function getData($token) {
			global $app;
			$ret = false;
			$key = $app->getGlobal('token_salt');
			$parts = explode('.', $token);
			$ret = get_item($parts, 0);
			return $ret;
		}
	}

?>