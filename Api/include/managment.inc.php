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
		public function register($request, $response, $agent = 'unknown', $user = 0) {

			global $app;
			$dbh = $app->getDatabase();

			$size = mb_strlen($response->getBody(), '8bit'); // It's in bytes
			$route = $request->route;

			if ( $user <= 0 ) {
				$user = isset($site->user->id) ? $site->user->id : 1;
			}

			try {
				$sql = "INSERT INTO managment_request(id, user_id, request_ip, route, size, key_slug, created, modified) VALUES(:id, :user_id, :request_ip, :route, :size, :key_slug, :created, :modified)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':id', 0);
				$stmt->bindValue(':user_id', $user);
				$stmt->bindValue(':request_ip', '192.169.1.65');
				$stmt->bindValue(':route', $route);
				$stmt->bindValue(':size', $size);
				$stmt->bindValue(':key_slug', $agent);
				$stmt->bindValue(':created', date('Y-m-d H:i:s'));
				$stmt->bindValue(':modified', date('Y-m-d H:i:s'));
				$stmt->execute();
				$ret = true;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			// print_a($request);
			// print_a($response);
			// exit;

			# Register events into DB
		}


		/********************************************************************************************************
		*											ToolBelt Events 											*
		*********************************************************************************************************/
		
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

		public function getUsers() {

			global $app;
			$dbh = $app->getDatabase();
			$result = null;

			// Remember, here we dont user ORM, cause only generated problems
			try {

				$sql = "SELECT id, login, nicename, email, status FROM user;";
				$stmt = $dbh->prepare($sql);
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
	}
?>