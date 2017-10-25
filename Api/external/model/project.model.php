<?php

	/**
	 * Project Class
	 *
	 * Project FTW
	 *
	 * @version  1
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com>
	 */
	class Project extends CROOD {

		public $id;
		public $name;
		public $description;
		public $conplete;
		public $created;
		public $modified;

		/**
		 * Initialization callback
		 * @return nothing
		 */
		function init($args = false) {

			$now = date('Y-m-d H:i:s');

			$this->table = 					'project';
			$this->table_fields = 			array('id', 'name', 'description', 'complete', 'created', 'modified');
			$this->update_fields = 			array('name', 'description', 'complete', 'modified');
			$this->singular_class_name = 	'Project';
			$this->plural_class_name = 		'Projects';

			# MetaModel
			$this->meta_id = 				'project_id';
			$this->meta_table = 			'project_meta';


			if (! $this->id ) {

				$this->id = 0;
				$this->name = '';
				$this->description = '';
				$this->complete = 0; // 0: No && 1:SI
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
				$attachment_id = $this->getMeta('image');
				if ($attachment_id) {
					$image = Attachments::getById($attachment_id);
					$this->cover = $image->getImage();
				}
				unset($this->metas);
				# ----------------------------------------------------------------------------------

				$this->postInit($args);
			}
		}

		/**
		* This method allows assign a project with a user.
		* @param user_id: Id of the user
		* @return true or false, depends if its works
		*/
		function assingUser($user_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO user_project(user_id, project_id) VALUES(:user_id, :project_id)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $user_id);
				$stmt->bindValue(':project_id', $this->id);
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
	 * Projects Class
	 *
	 * Projects
	 *
	 * @version 1.0
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com>
	 */
	class Projects extends NORM {

		protected static $table = 					'project';
		protected static $table_fields = 			array('id', 'name', 'description', 'complete', 'created', 'modified');
		protected static $singular_class_name = 	'Project';
		protected static $plural_class_name = 		'Projects';

		/**
		* This method allow get the projects from a user
		* @param user_id: Integer user id
		* @return Projects array
		**/
		static function getUserProjects($user_id, $complete = 0) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();
			$projects = array();

			try {
				$sql = "SELECT p.id, p.* FROM user_project up INNER JOIN project p ON up.project_id = p.id
						WHERE up.user_id = :user_id AND p.complete = :complete;";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $user_id);
				$stmt->bindValue(':complete', $complete);
				$stmt->execute();
				$ret = $stmt->fetchAll(PDO::FETCH_CLASS, "Project");
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
		static function getByUserId($user_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();
			$projects = array();

			try {
				$sql = "SELECT project_id FROM user_project WHERE user_id = :user_id";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $user_id);
				$stmt->execute();
				$rows = $stmt->fetchAll();
				$projects = $rows;
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			foreach ($projects as $project) {
				$ret[] = Projects::getById($project->project_id);
			}

			return $ret;
		}
	}
?>