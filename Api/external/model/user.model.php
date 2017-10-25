<?php

	/**
	 * User Class
	 *
	 * Provides the abstraction layer for the User object.
	 *
	 * @version  1.0
	 * @author   Raul Vera <raul.vera@thewebchi.mp>
	 * @uses     CROOD
	 * @category Core modules
	 */
	class User extends CROOD {

		public $id;
		public $login;
		public $slug;
		public $fbid;
		public $email;
		public $password;
		public $nicename;
		public $status;
		public $type;
		public $created;
		public $modified;

		/**
		 * Initialization callback
		 * @return nothing
		 */
		function init($args = false) {

			$this->meta_id = 'user_id';
			$this->meta_table = 'user_meta';

			$this->table = 'user';
			$this->table_fields = array('id','slug', 'login', 'fbid', 'email', 'password', 'nicename', 'status', 'type', 'created', 'modified');
			$this->update_fields = array('slug', 'login', 'fbid', 'email', 'password', 'nicename', 'status', 'type', 'modified');
			$this->singular_class_name = 'User';
			$this->plural_class_name = 'Users';

			# MetaModel
			$this->meta_table = 'user_meta';
			$this->meta_id = 'user_id';

			if (! $this->id ) {

				$now = date('Y-m-d H:i:s');
				$this->id = 0;
				$this->login = '';
				$this->slug = '';
				$this->fbid = '';
				$this->email = '';
				$this->password = '';
				$this->nicename = '';
				$this->status = 'Active';
				$this->type = 'User';
				$this->created = $now;
				$this->modified = $now;

			} else {

				$args = $this->preInit($args);

				# Enter your logic here
				# ----------------------------------------------------------------------------------

				if (is_array($args)) {
					// var_dump($args);
					# Fetch metas
					if ( in_array('fetch_metas', $args) ) {
						$this->metas = $this->getMetas();
					}
					# Unset fields
					if ( isset( $args['unset_fields'] ) ) {
						foreach ($args['unset_fields'] as $field) {
							unset($this->$field);
						}
					}
				}

				$this->hash = md5($this->email);

				# ----------------------------------------------------------------------------------

				$this->postInit($args);
			}
		}

		/**
		 * Save the model
		 * @return boolean True if the model was updated, False otherwise
		 */
		function save() {

			# Sanitization
			if ( empty($this->login) ) {
				return false;
			}

			$this->modified = date('Y-m-d H:i:s');
			$this->nicename = $this->nicename ? $this->nicename : $this->email;
			$this->login = $this->login ? $this->login : $this->email;

			if( substr($this->password, 0, 4) != '$2a$' ) {
				$this->password = Users::hashPassword($this->password);
			}

			return parent::save();
		}

		/* HELPERS */
		/* -------------------------------------------------------------------------------------- */

		/**
		 * Save the model
		 * @return string Full name of the current user (first name and last name)
		 */
		function getFullName() {

			$ret = '';

			if(isset($this->metas)) {

				$ret .= isset($this->metas->first_name) ? $this->metas->first_name : '';
				$ret .= ' ';
				$ret .= isset($this->metas->last_name) ? $this->metas->last_name : '';
				$ret = trim($ret);
			}

			return $ret ?: '-';
		}

		/**
		* This method allow to match a organization with a user
		**/
		function assingOrganization($organization_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO user_organization(user_id, organization_id) VALUES(:user_id, :organization_id)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $this->id);
				$stmt->bindValue(':organization_id', $organization_id);
				$stmt->execute();
				$ret = true;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

		function getAssignedOrganization() {

			global $app;
			$dbh = $app->getDatabase();
			$ret = '';

			try {
				$sql = "SELECT organization_id FROM user_organization WHERE user_id = :user_id";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $this->id);
				$stmt->execute();
				$ret = $stmt->fetch();

				if (isset($ret->organization_id)) {
					$organization_id = $ret->organization_id;
					if ($organization_id > 0) {
						$ret = organizations::getById($organization_id);
					}
				}

			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}
	}

	# ==============================================================================================

	/**
	 * Users Class
	 *
	 * Handles the user account mechanism.
	 *
	 * @version 1.0
	 * @author  Raul Vera <raul.vera@thewebchi.mp>
	 */
	class Users extends NORM {

		protected static $user_id;

		/**
		 * Initialization function
		 */
		static function init() {
			global $app;
			# Initialize some defaults
			self::$user_id = 0;
		}

		protected static $table = 'user';
		protected static $table_fields = array('id', 'slug', 'fbid', 'login', 'email', 'password', 'nicename', 'status', 'type', 'created', 'modified');
		protected static $singular_class_name = 'User';
		protected static $plural_class_name = 'Users';

		/**
		 * Retrieve the current user
		 * @return mixed User object on success, Null otherwise
		 */
		static function getCurrentUser() {
			$ret = self::getById( self::$user_id );
			return $ret;
		}

		/**
		 * Retrieve the current user Id
		 * @return integer Current user Id
		 */
		static function getCurrentUserId() {
			return self::$user_id;
		}

		/**
		 * Recover a previous session
		 * @return boolean True if the user was re-logged in, False otherwise
		 */
		static function checkLogin() {
			global $app;
			$ret = false;
			$name = sprintf('%s_login%s', $app->toAscii( $app->getGlobal('app_name') ), $app->hashPassword('cookie'));
			$cookie = isset($_COOKIE[$name]) ? $_COOKIE[$name] : null;
			if ($cookie) {
				$id = self::getCookieData($cookie);
				$user = self::getById($id);
				# Check user and password
				if ( $user && self::checkCookie($cookie) ) {
					# Save user id
					self::$user_id = $user->id;
					$ret = true;
				}
			}
			return $ret;
		}

		/**
		 * Check if there's a valid user logged in, otherwise send it to the sign-in page
		 * @return boolean True if the current user is set/valid, otherwise it will be redirected
		 */
		static function requireLogin($redirect = '/sign-in') {
			global $app;
			header("Expires: on, 01 Jan 1970 00:00:00 GMT");
			header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
			header("Cache-Control: no-store, no-cache, must-revalidate");
			header("Cache-Control: post-check=0, pre-check=0", false);
			header("Pragma: no-cache");
			# Check user
			if ( self::$user_id ) {
				return true;
			} else {
				$path = $app->getCurRequest();
				$path = ltrim($path, '/');
				$_SESSION['login_redirect'] = "/{$path}";
			}
			if ($redirect) {
				$app->redirectTo( $app->urlTo($redirect) );
				exit;
			}
			return false;
		}

		/**
		 * Sign a new user in, replaces previous user (if any)
		 * @param  string  $user     User name
		 * @param  string  $password Plain-text password
		 * @param  boolean $remember Whether to set the cookie for 12 hours (normal) or 2 weeks (remember)
		 * @return boolean           True on success, False otherwise
		 */
		static function login($user, $password, $remember = false) {
			global $app;
			$ret = false;
			$user = self::getByLogin($user);

			if ($user) {
				$auth = self::checkPassword($password, $user->password);

				if ($auth) {
					$expires = strtotime($remember ? '+15 day' : '+12 hour');
					$cookie = Users::buildCookie($expires, $user->id);
					$name = sprintf('%s_login%s', $app->toAscii( $app->getGlobal('app_name') ), $app->hashPassword('cookie'));
					# Set user id
					self::$user_id = $user->id;
					# And set cookie
					$ret = setcookie($name, $cookie, $expires, '/');
					# Run hooks
					if ($user->id) {
						$app->executeHook('users.login', $user->id);
					}
				}
			}
			return $ret;
		}

		/**
		 * Set the current user
		 * @param integer $user_id  User ID
		 * @param boolean $remember Remember user or not
		 */
		static function setCurrentUser($user_id, $remember = false) {
			global $app;
			$ret = false;
			$user = self::getById($user_id);
			if ($user) {
				$expires = strtotime($remember ? '+15 day' : '+12 hour');
				$cookie = self::buildCookie($expires, $user->id);
				$name = sprintf('%s_login%s', $app->toAscii( $app->getGlobal('app_name') ), $app->hashPassword('cookie'));
				# Set user id
				self::$user_id = $user->id;
				# And set cookie
				$ret = setcookie($name, $cookie, $expires, '/');
				# Run hooks
				if ($user->id) {
					$app->executeHook('users.login', $user->id);
				}
			}
			return $ret;
		}

		/**
		 * Change user, saving the current one
		 * @param  integer $user_id User ID
		 * @return boolean          True on success, False otherwise
		 */
		static function switchUser($user_id) {
			global $app;
			$ret = false;
			$user = self::getById($user_id);
			if ($user && $app->user) {
				# Save old user
				$expires = strtotime('+12 hour');
				$cookie = self::buildCookie($expires, $app->user->id);
				$name = sprintf('%s_old_login%s', $app->toAscii( $app->getGlobal('app_name') ), $app->hashPassword('cookie'));
				# Set cookie
				$ret = setcookie($name, $cookie, $expires, '/');
				# And now set the new user
				self::setCurrentUser($user_id);
			}
			return $ret;
		}

		/**
		 * Check whether the user was switched or not
		 * @return boolean True if the user has been switched
		 */
		static function isUserSwitched() {
			global $app;
			$name = sprintf('%s_old_login%s', $app->toAscii( $app->getGlobal('app_name') ), $app->hashPassword('cookie'));
			$cookie = isset($_COOKIE[$name]) ? $_COOKIE[$name] : null;
			return ($cookie != null);
		}

		/**
		 * Check whether the user was switched or not
		 * @return boolean True if the user has been switched
		 */
		static function restoreUser() {
			global $app;
			$ret = false;
			$old_name = sprintf('ggi_old_login%s', $app->hashPassword('cookie'));
			$old_cookie = isset($_COOKIE[$old_name]) ? $_COOKIE[$old_name] : null;
			if ($old_cookie) {
				# Set new user
				$expires = strtotime('+12 hour');
				$name = sprintf('%s_login%s', $app->toAscii( $app->getGlobal('app_name') ), $app->hashPassword('cookie'));
				# Delete old cookie
				setcookie($old_name, '', strtotime('-1 hour'), '/');
				# Update cookie
				$ret = setcookie($name, $old_cookie, $expires, '/');
			}
			return $ret;
		}

		/**
		 * Sign the current user out
		 * @return boolean     True on success, False otherwise
		 */
		static function logout() {
			global $app;
			# Run hooks
			if (self::$user_id) {
				$app->executeHook('users.logout', self::$user_id);
			}
			# Sign user out
			self::$user_id = 0;
			$name = sprintf('%s_login%s', $app->toAscii( $app->getGlobal('app_name') ), $app->hashPassword('cookie'));
			return setcookie($name, '', strtotime('-1 hour'), '/');
		}

		/**
		 * Hash a plain-text password
		 * @param  string $password Plain-text password to hash
		 * @return string           Hashed password
		 */
		static function hashPassword($password) {
			$hasher = new PasswordHash(8, FALSE);
			$hash = $hasher->HashPassword($password);
			return $hash;
		}

		/**
		 * Check whether the password is valid or not
		 * @param  string $password    Plain-text password
		 * @param  string $stored_hash Hashed password, usually from the database
		 * @return boolean             True if the password is valid, False otherwise
		 */
		static function checkPassword($password, $stored_hash) {
			$hasher = new PasswordHash(8, FALSE);
			return $hasher->CheckPassword($password, $stored_hash);
		}

		/**
		 * Create a hardened cookie
		 * @param  timestamp $expires When the cookie will expire
		 * @param  string    $data    Data to save (application state)
		 * @return string             Hardened cookie data
		 */
		protected static function buildCookie($expires, $data) {
			global $app;
			# Get secret key
			$secret = $app->getGlobal('pass_salt');
			# Build cookie
			$cookie = sprintf("exp=%s&data=%s", urlencode($expires), urlencode($data));
			# Calculate the MAC (message authentication code)
			$mac = hash_hmac("sha256", $cookie, $secret);
			# Append MAC to the cookie and return it
			return $cookie . '&digest=' . urlencode($mac);
		}

		/**
		 * Get cookie stored data
		 * @param  string $cookie Cookie data
		 * @return mixed          String with cookie data or False on error
		 */
		protected static function getCookieData($cookie) {
			global $app;
			# Get cookie vars
			parse_str($cookie, $vars);
			return isset( $vars['data'] ) ? $vars['data'] : null;
		}

		/**
		 * Check whether the cookie is valid or not
		 * @param  string $cookie Cookie data
		 * @return boolean        True if the cookie is valid, False otherwise
		 */
		protected static function checkCookie($cookie) {
			global $app;
			# Get secret key
			$secret = $app->getGlobal('pass_salt');
			# Get cookie vars
			parse_str($cookie, $vars);
			if( empty($vars['exp']) || $vars['exp'] < time() ) {
				# Cookie has expired
				return false;
			}
			# Generate a valid cookie, both should match
			$str = self::buildCookie($vars['exp'], $vars['data']);
			if ($str != $cookie) {
				# Cookie has been forged
				return false;
			}
			# Otherwise the cookie is valid
			return true;
		}

		/**
		* This method allow to search a user throw email or nicename
		**/
		static function search($search) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "SELECT user.id, user.fbid, user.nicename, user.email FROM user
						WHERE user.email LIKE '%{$search}%' OR user.nicename LIKE '%{$search}%' LIMIT 5;";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':search', $search);
				$stmt->execute();
				$ret = $stmt->fetchAll();

			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

	}
?>