<?php

	/**
	 * managmnet.inc.php
	 * Api managment class
	 *
	 * Version: 	1.0
	 * Author(s):	nalancer08 <github.com/nalancer08> <erick.sanchez@appbuilders.com.mx>
	 */

	class Managment {

		/**
		 * The format specifier for the expected response (html, json, xml, yaml, etc.)
		 * @var string
		 */
		public $status;

		function __construct() {

			# Initializaing variables
			$this->status = true;

			# Making callbacks
			$this->checkStatus();
		}

		/**
		* This method check in data base, if API can be used
		**/
		private function checkStatus() {

			# DB check
			$this->status = true;
			# true is on, false is off
		}

		/**
		* This method allows to anyone check if the API is turn on
		**/
		public function isOn() {

			return $this->status;
		}

		public function isBlocked($id) {

			global $app;
			$dbh = $app->getDatabase();
			$result = true;

			try {
				$sql = "SELECT mb.status AS code, u.status AS state FROM managment_block mb INNER JOIN user u on user_id = :user_id";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $id);
				$stmt->execute();
				$result = $stmt->fetch();

				if (  $result->code != '' && $result->state != '' ) {
					if ( $result->code != 1 && $result->code != 2 && $result->state != 'Banned' ) {
						$result = false;
					}
				}

			} catch (PDOException $e) {
				echo $e->getMessage();
			}
			return $result;
		}

		/**
		* This method allows register a request with response
		* This method gonna calculate the size from the route, and then register to mesuare the api calls and use
		**/
		public function register($request, $response, $agent = 'unknown', $user = 0, $uuid = 0) {

			global $app;
			//$dbh = $app->getDatabase();

			$size = mb_strlen($response->getBody(), '8bit'); // It's in bytes
			$route = $request->route;

			if ($user <= 0) {
				$user = isset($site->user->id) ? $site->user->id : 1;
			}

			$params = array();
			$params['user_uuid'] = ($uuid != 0) ? $uuid : $user;
			$params['app_uuid'] = $this->getOptionValue('pd_app_uuid');
			$params['request_ip'] = $this->get_client_ip();
			$params['request_client'] = $this->get_client_ua();
			$params['route'] = $route;
			$params['size'] = $size;
			$params['key_slug'] = $agent;

			$pd = new PigData(true);
			$pd->addResponse($params);

			// if ( $user <= 0 ) {
			// 	$user = isset($site->user->id) ? $site->user->id : 1;
			// }

			// try {
			// 	$sql = "INSERT INTO managment_request(id, user_id, request_ip, route, size, key_slug, created, modified) VALUES(:id, :user_id, :request_ip, :route, :size, :key_slug, :created, :modified)";
			// 	$stmt = $dbh->prepare($sql);
			// 	$stmt->bindValue(':id', 0);
			// 	$stmt->bindValue(':user_id', $user);
			// 	$stmt->bindValue(':request_ip', '192.169.1.65');
			// 	$stmt->bindValue(':route', $route);
			// 	$stmt->bindValue(':size', $size);
			// 	$stmt->bindValue(':key_slug', $agent);
			// 	$stmt->bindValue(':created', date('Y-m-d H:i:s'));
			// 	$stmt->bindValue(':modified', date('Y-m-d H:i:s'));
			// 	$stmt->execute();
			// 	$ret = true;
			// } catch (PDOException $e) {
			// 	echo $e->getMessage();
			// }

			// print_a($request);
			// print_a($response);
			// exit;

			# Register events into DB
		}

		/**
		* This method allow synchronize pig data products and application
		* This proccess is handle by uuid's
		**/
		public function synchronize($uuid) {

			global $app;
			$this->addOption('pd_app_uuid', $uuid);
		}


		/********************************************************************************************************
		*											ToolBelt Options 											*
		*********************************************************************************************************/
		
		public function addOption($field, $value) {

			global $app;
			$dbh = $app->getDatabase();

			try {

				$sql = "SELECT mo.value FROM managment_options mo WHERE mo.opt = '{$field}'";
				$stmt = $dbh->prepare($sql);
				$stmt->execute();
				$result = $stmt->fetch();

				if (!$result) {

					$sql = "INSERT INTO managment_options(id, opt, value, created, modified) VALUES(:id, :opt, :value, :created, :modified)";
					$stmt = $dbh->prepare($sql);
					$stmt->bindValue(':id', 0);
					$stmt->bindValue(':opt', $field);
					$stmt->bindValue(':value', $value);
					$stmt->bindValue(':created', date('Y-m-d H:i:s'));
					$stmt->bindValue(':modified', date('Y-m-d H:i:s'));
					$stmt->execute();
					$ret = true;
				}

			} catch (PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getOption($field) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {

				$sql = "SELECT mo.* FROM managment_options mo WHERE mo.opt = '{$field}'";
				$stmt = $dbh->prepare($sql);
				$stmt->execute();
				$ret = $stmt->fetch();

			} catch (PDOException $e) {
				echo $e->getMessage();
			}
			return $ret;
		}

		public function getOptionValue($field) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {

				$sql = "SELECT mo.value FROM managment_options mo WHERE mo.opt = '{$field}'";
				$stmt = $dbh->prepare($sql);
				$stmt->execute();
				$ret = $stmt->fetch();
				$ret = $ret->value;

			} catch (PDOException $e) {
				echo $e->getMessage();
			}
			return $ret;
		}

		public function updateOption() {}

		public function removeOption() {}

		/******************************************************************************************************
		*											Load Functions 											  *
		******************************************************************************************************/

		public function getRoutes() {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();

			try {

				$sql = "SELECT DISTINCT(route) FROM managment_request;";
				$stmt = $dbh->prepare($sql);
				$stmt->execute();
				$results = $stmt->fetchAll();

				foreach ($results as $result) {
					
					$ret[] = $result->route;
				}

			} catch (PDOException $e) {
				echo $e->getMessage();
			}
			return $ret;
		}

		public function getGlobalLoad($filter) {

			global $app;
			$dbh = $app->getDatabase();
			$result = null;

			try {
				$sql = $this->matchSubQueryOperator("SELECT SUM(mr.size) AS bytes FROM managment_request mr", $filter);
				$stmt = $dbh->prepare($sql);
				$stmt->execute();
				$result = $stmt->fetch();

			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $result;
		}

		public function getLoadByUser($id, $filter) {

			global $app;
			$dbh = $app->getDatabase();
			$result = null;

			try {
				$sql = $this->matchSubQueryOperator("SELECT SUM(mr.size) AS bytes FROM managment_request mr WHERE mr.user_id = :user_id", $filter);
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $id);
				$stmt->execute();
				$result = $stmt->fetch();

			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $result;
		}

		public function getRouteLoad($route, $filter, $user = null) {

			global $app;
			$dbh = $app->getDatabase();
			$result = null;

			try {

				if ( !$user ) {
					$sql = $this->matchSubQueryOperator("SELECT COUNT(route) AS population, SUM(size) AS bytes FROM managment_request WHERE route = :route", $filter);
					$stmt = $dbh->prepare($sql);
				} else {
					$sql = $this->matchSubQueryOperator("SELECT COUNT(route) AS population, SUM(size) AS bytes FROM managment_request WHERE route = :route AND user_id = :user_id", $filter);
					$stmt = $dbh->prepare($sql);
					$stmt->bindValue(':user_id', $user);
				}

				$stmt->bindValue(':route', $route);
				$stmt->execute();
				$result = $stmt->fetch();
				return $result;

			} catch (PDOException $e) {
				echo $e->getMessage();
			}
			return $result;
		}

		public function routesStaticsResume($filter) {

			global $app;
			$routes = $this->getRoutes();
			$relations = array();

			foreach ($routes as $route) {
				
				$relations[$route] = $this->getRouteLoad($route, $filter);
			}

			return $relations;
		}

		public function userStaticsResume($user, $filter) {

			global $app;
			$routes = $this->getRoutes();
			$relations = array();

			foreach ($routes as $route) {
				
				$relations[$route] = $this->getRouteLoad($route, $filter, $user);
			}
			return $relations;
		}

		/******************************************************************************************************
		*											Distribution Functions 									  *
		******************************************************************************************************/

		public function getDistributionKeys() {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();

			try {

				$sql = "SELECT DISTINCT(key_slug) AS name FROM managment_request;";
				$stmt = $dbh->prepare($sql);
				$stmt->execute();
				$results = $stmt->fetchAll();

				foreach ($results as $result) {
					$ret[] = $result->name;
				}

			} catch (PDOException $e) {
				echo $e->getMessage();
			}
			return $ret;
		}

		public function getGlobalDistribution($filter) {

			global $app;
			$dbh = $app->getDatabase();
			$keys = $this->getDistributionKeys();
			$result = null;

			foreach ($keys as $key) {
					
				try {

					$sql = $this->matchSubQueryOperator("SELECT COUNT(size) AS result FROM managment_request mr WHERE key_slug = :key;", $filter);
					$stmt = $dbh->prepare($sql);
					$stmt->bindValue(':key', $key);
					$stmt->execute();
					$ret = $stmt->fetch();
					$result[$key] = $ret->result;

				} catch (PDOException $e) {
					echo $e->getMessage();
				}
			}
			return $result;
		}

		/******************************************************************************************************
		*											Users Functions 		     							  *
		******************************************************************************************************/

		public function getUsers($maxItems, $page) {

			global $app;
			$dbh = $app->getDatabase();
			$result = null;

			// Remember, here we dont user ORM, cause only generated problems
			try {

				$sql = "SELECT id, login, slug, fbid, email, nicename, status, type, created, modified FROM user;
						LIMIT :page, :max";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':page', $page); 
				$stmt->bindValue(':max', $maxItems); 
				$stmt->execute();
				$result = $stmt->fetchAll();

			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $result;
		}

		public function blockUser($id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO managment_block(id, user_id, status, message, created, modified) VALUES(:id, :user_id, :status, :message, :created, :modified)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':id', 0);
				$stmt->bindValue(':user_id', $id);
				$stmt->bindValue(':status', 1);
				$stmt->bindValue(':message', "Banned");
				$stmt->bindValue(':created', date('Y-m-d H:i:s'));
				$stmt->bindValue(':modified', date('Y-m-d H:i:s'));
				$stmt->execute();
				$ret = true;

				if ( class_exists('Users') ) {

					$user = Users::getById($id);
					$user->status = "Banned";
					$user->save();
				}


			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

		public function unlockUser($id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "UPDATE managment_block SET status = :status, modified = :modified WHERE user_id = :user_id;";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $id);
				$stmt->bindValue(':status', 0);
				$stmt->bindValue(':modified', date('Y-m-d H:i:s'));
				$stmt->execute();
				$ret = true;

				if ( class_exists('Users') ) {

					$user = Users::getById($id);
					$user->status = "Active";
					$user->save();
				}


			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

		/******************************************************************************************************
		*									     	Dynamics ads 											  *
		******************************************************************************************************/
		
		/**
		* This method allow to get api.json file
		* from APIO folder, remeber, use apio gonna be a
		* standard for all the apis
		**/
		public function getApi() {
			
			global $app;
			$ret = '';

			$file = file_get_contents('././apio/api.json');
			return $file;
		}

		/******************************************************************************************************
		*										Helper Functions 											  *
		******************************************************************************************************/
	
		public function getDateFilter($request) {

			global $app;
			$ret = '';

			if ( $request ) {

				$begin = $request->post('begin');
				$end = $request->post('end');

				if ( $begin != '' && $end != '' ) {
					$ret = " AND created BETWEEN '{$begin}' AND '{$end}'";
				}
			}

			return $ret;
		}

		protected function matchSubQueryOperator($query, $ad) {

			$sql = $query;

			if ( $ad != '' ) {

				if ( !preg_match('/WHERE/i', $query) ) {
					
					$from = '/'.preg_quote('AND', '/').'/';
					$ad = preg_replace($from, 'WHERE', $ad, 1);
				}

				$sql = $sql . $ad;
			}

			return $sql;
		}

			/**
	 * Get the client's IP address
	 * @return string The client's IP address
	 */
	protected function get_client_ip() {
		$ret = "0.0.0.0";
		if (isset($_SERVER)) {
			$ret = $_SERVER["REMOTE_ADDR"];
			if ( isset($_SERVER["HTTP_X_FORWARDED_FOR"]) ) {
				$ret = $_SERVER["HTTP_X_FORWARDED_FOR"];
			}
			if ( isset($_SERVER["HTTP_CLIENT_IP"]) ) {
				$ret = $_SERVER["HTTP_CLIENT_IP"];
			}
		}
		return $ret;
	}

	/**
	 * Get the client's user-agent
	 * @return string The client's user-agent
	 */
	protected function get_client_ua() {
		$ret = "Unknown";
		if (isset($_SERVER)) {
			if ( isset($_SERVER["HTTP_USER_AGENT"]) ) {
				$ret = $_SERVER["HTTP_USER_AGENT"];
			}
		}
		return $ret;
	}
	}
?>