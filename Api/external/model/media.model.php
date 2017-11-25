<?php

	/**
	 * Media Class
	 *
	 * Media FTW
	 *
	 * @version  2.0
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com.mx>
	 */
	class Media extends CROOD {

		public $id;
		public $anime_id;
		public $number;
		public $name;
		public $description;
		public $type;
		public $audio;
		public $created;
		public $modified;

		/**
		 * Initialization callback
		 * @return nothing
		 */
		function init($args = false) {

			$now = date('Y-m-d H:i:s');

			$this->table = 					'media';
			$this->table_fields = 			array('id', 'anime_id', 'number', 'name', 'description', 'type', 'audio', 'created', 'modified');
			$this->update_fields = 			array('number', 'anime_id', 'name', 'description', 'type', 'audio', 'modified');
			$this->singular_class_name = 	'Media';
			$this->plural_class_name = 		'Medias';

			# MetaModel
			$this->meta_id = 				'media_id';
			$this->meta_table = 			'media_meta';


			if (!$this->id ) { // New element

				$this->id = 0;
				$this->anime_id = 0;
				$this->number = 0;
				$this->name = "";
				$this->description = "";
				$this->type = "chapter"; // Type: 1 - chapter, 2 - ova, 3 - movie, 4 - special
				$this->audio = "jp/spa"; // Audio: 1 - jp/spa, 2 - latino
				$this->created = $now;
				$this->modified = $now;
			
			} else { // Retriving element

				$args = $this->preInit($args);

				# Enter your logic here
				# ----------------------------------------------------------------------------------

				$this->created = date('d-F-Y', strtotime($this->created));
				$this->url = $this->getMeta('url');
				unset($this->metas);

				# ----------------------------------------------------------------------------------

				$this->postInit($args);
			}
		}

		public function assignGenres($user_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO user_task(user_id, task_id) VALUES(:user_id, :task_id)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $user_id);
				$stmt->bindValue(':task_id', $this->id);
				$stmt->execute();
				$ret = true;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

		public function beAssignedToAnime($anime_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO anime_media(anime_id, media_id) VALUES(:anime_id, :media_id)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':anime_id', $anime_id);
				$stmt->bindValue(':media_id', $this->id);
				$stmt->execute();
				$ret = true;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}
	}

	# ==============================================================================================

	/**
	 * Tasks Class
	 *
	 * Tasks
	 *
	 * @version 1.0
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com>
	 */
	class Medias extends NORM {

		protected static $table = 					'media';
		protected static $table_fields = 			array('id', 'anime_id', 'number', 'name', 'description', 'type', 'audio', 'created', 'modified');
		protected static $singular_class_name = 	'Media';
		protected static $plural_class_name = 		'Medias';
	}
?>