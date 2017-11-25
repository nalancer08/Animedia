<?php

	/**
	 * Anime Class
	 *
	 * Anime FTW
	 *
	 * @version  1
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com>
	 */
	class Anime extends CROOD {

		public $id;
		public $name;
		public $description;
		public $year;
		public $latest;
		public $created;
		public $modified;

		/**
		 * Initialization callback
		 * @return nothing
		 */
		function init($args = false) {

			$now = date('Y-m-d H:i:s');

			$this->table = 					'anime';
			$this->table_fields = 			array('id', 'name', 'description', 'year', 'latest', 'created', 'modified');
			$this->update_fields = 			array('name', 'description', 'year', 'latest', 'modified');
			$this->singular_class_name = 	'Anime';
			$this->plural_class_name = 		'Animes';

			# MetaModel
			$this->meta_id = 				'anime_id';
			$this->meta_table = 			'anime_meta';


			if (!$this->id) {

				$this->id = 0;
				$this->name = '';
				$this->description = '';
				$this->year = 0;
				$this->latest = 1; // 0: No && 1:SI
				$this->created = $now;
				$this->modified = $now;
			}

			else {

				$args = $this->preInit($args);

				# Enter your logic here
				# ----------------------------------------------------------------------------------

				#Initialize cover
				$this->cover = '';

				# Gettign meta attachment
				$attachment_id = $this->getMeta('cover');
				if ($attachment_id) {
					$image = Attachments::getById($attachment_id);
					$this->cover = $image->getImage('url', 'large');
				}

				# If have cover url, replace current cover
				$coverUrl = $this->getMeta('cover_url');
				if ($coverUrl != '') {
					$this->cover = $coverUrl;
				}

				unset($this->metas);
				# ----------------------------------------------------------------------------------

				$this->postInit($args);
			}
		}

		/**
		* This method allows assign a genre with an anime.
		* @param user_id: Id of the user
		* @return true or false, depends if its works
		*/
		function assignGenre($genre_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO anime_genre(anime_id, genre_id) VALUES(:anime_id, :genre_id)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':anime_id', $this->id);
				$stmt->bindValue(':genre_id', $genre_id);
				$stmt->execute();
				$ret = true;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

		function getGenres() {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "SELECT g.name FROM anime_genre ag INNER JOIN genre g ON g.id = ag.genre_id
						WHERE ag.anime_id = :anime_id;";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':anime_id', $this->id);
				$stmt->execute();
				$ret = $stmt->fetchAll();
				//$ret = $stmt->fetchAll(PDO::FETCH_CLASS, "Genre");
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

		function getMedias($type = 'chapter', $audio = 'jp/spa') {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "SELECT m.id, m.number, m.name, m.type, m.audio
						FROM media m WHERE m.anime_id = :anime_id AND type = '{$type}' 
						AND m.audio = '{$audio}' ORDER BY m.number DESC;";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':anime_id', $this->id);
				//$stmt->bindValue(':type', $type);
				$stmt->execute();
				$ret = $stmt->fetchAll(PDO::FETCH_CLASS, "Media");
			} catch (PDOException $e) {
				$ret = $e->getMessage();
			}

			return $ret;
		}

		function getAvailableAudios($type = 'chapter') {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "SELECT DISTINCT(m.audio) FROM media m WHERE m.anime_id = :anime_id;";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':anime_id', $this->id);
				//$stmt->bindValue(':type', $type);
				$stmt->execute();
				$ret = $stmt->fetchAll();
			} catch (PDOException $e) {
				$ret = $e->getMessage();
			}

			return $ret;
		}
	}

	# ==============================================================================================

	/**
	 * Animes Class
	 *
	 * Animes
	 *
	 * @version 1.0
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com>
	 */
	class Animes extends NORM {

		protected static $table = 					'anime';
		protected static $table_fields = 			array('id', 'name', 'description', 'year', 'latest', 'created', 'modified');
		protected static $singular_class_name = 	'Anime';
		protected static $plural_class_name = 		'Animes';

		/**
		* This method allow get the projects from a user
		* @param user_id: Integer user id
		* @return Projects array
		**/
		static function getByAlphabet($char) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();
			$projects = array();

			try {
				$sql = "SELECT a.* FROM anime a WHERE name REGEXP '^[:char].*$'";
				//$sql = "SELECT a.* FROM anime a WHERE name LIKE ':char%';";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':char', $char);
				$stmt->execute();
				$ret = $stmt->fetchAll(PDO::FETCH_CLASS, "Anime");
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			return $ret;
		}

		/**
		* This method allow get the projects from a user
		* @param user_id: Integer user id
		* @return Projects array
		**/
		static function searchByName($name) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();

			try {
				$sql = "SELECT a.* FROM anime a WHERE name LIKE '%{$name}%';";
				$stmt = $dbh->prepare($sql);
				//$stmt->bindValue(':name', "'%{$name}%'");
				$stmt->execute();
				$ret = $stmt->fetchAll(PDO::FETCH_CLASS, "Anime");
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			return $ret;
		}

		static function searchByGenres($genres) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();

			try {
				$sql = "SELECT DISTINCT a.* FROM anime_genre ag INNER JOIN anime a ON a.id = ag.anime_id
						WHERE ag.genre_id IN(:genres);";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':genres', $genres);
				$stmt->execute();
				$ret = $stmt->fetchAll(PDO::FETCH_CLASS, "Anime");
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			return $ret;
		}

		static function customSearch($search) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();

			try {
				$sql = "SELECT a.* fROM anime a WHERE a.name LIKE '%:search%';";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':search', $search);
				$stmt->execute();
				$ret = $stmt->fetchAll(PDO::FETCH_CLASS, "Anime");
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			return $ret;
		}
	}
?>