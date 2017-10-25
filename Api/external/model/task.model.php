<?php

	/**
	 * Task Class
	 *
	 * Task FTW
	 *
	 * @version  2.0
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com.mx>
	 */
	class Task extends CROOD {

		public $id;
		public $parent_id;
		public $content;
		public $priority;
		public $complete;
		public $shared;
		public $start;
		public $end;
		public $created;
		public $modified;

		/**
		 * Initialization callback
		 * @return nothing
		 */
		function init($args = false) {

			$now = date('Y-m-d H:i:s');

			$this->table = 					'task';
			$this->table_fields = 			array('id', 'parent_id', 'content', 'priority', 'complete', 'shared', 'start', 'end', 'created', 'modified');
			$this->update_fields = 			array('parent_id', 'content', 'priority', 'complete', 'shared', 'start', 'end', 'modified');
			$this->singular_class_name = 	'Task';
			$this->plural_class_name = 		'Tasks';

			# MetaModel
			$this->meta_id = 				'';
			$this->meta_table = 			'';


			if (!$this->id ) { // New element

				$this->id = 0;
				$this->parent_id = 0; // Began unique
				$this->content = '';
				$this->priority = 'normal'; // Began in normal priority
				$this->complete = 0; // 0 = false && 1 = true
				$this->shared = 0; // 0 = no && 1 = yes
				$this->start = $now; // Start when its creatde
				$this->end = '';
				$this->created = $now;
				$this->modified = $now;
			
			} else { // Retriving element

				$args = $this->preInit($args);

				# Enter your logic here
				# ----------------------------------------------------------------------------------

				$this->created = date('d-F-Y', strtotime($this->created));

				# ----------------------------------------------------------------------------------

				$this->postInit($args);
			}
		}

		public function assignUser($user_id) {

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

		public function assignProject($project_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO project_task(project_id, task_id) VALUES(:project_id, :task_id)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':project_id', $project_id);
				$stmt->bindValue(':task_id', $this->id);
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
	class Tasks extends NORM {

		protected static $table = 					'task';
		protected static $table_fields = 			array('id', 'parent_id', 'content', 'priority', 'complete', 'shared', 'start', 'end', 'created', 'modified');
		protected static $singular_class_name = 	'Task';
		protected static $plural_class_name = 		'Tasks';

		/**.New functions **/

		static function getUserTasks($user_id, $complete = 0) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();
			$tasks = null;

			try {
				$sql = "SELECT t.id, t.* FROM user_task ut INNER JOIN task t ON ut.task_id = t.id
						WHERE ut.user_id = :user_id AND t.parent_id = 0 AND t.complete = :complete";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $user_id);
				$stmt->bindValue(':complete', $complete);
				$stmt->execute();
				$ret = $stmt->fetchAll(PDO::FETCH_CLASS, "Task");

			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			return $ret;
		}

		static function getProjectTasks($project_id, $complete = 0) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();
			$tasks = null;

			try {
				$sql = "SELECT t.* FROM project_task pt INNER JOIN task t ON pt.task_id = t.id
						WHERE pt.project_id = :project_id AND t.parent_id = 0 AND t.complete = :complete;";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':project_id', $project_id);
				$stmt->bindValue(':complete', $complete);
				$stmt->execute();
				$ret = $stmt->fetchAll(PDO::FETCH_CLASS, "Task");

			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			return $ret;
		}

		static function getProjectTasksCount($project_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();
			$tasks = null;

			try {
				$sql = "SELECT COUNT(pt.project_id) AS total FROM project_task pt WHERE pt.project_id = :project_id;";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':project_id', $project_id);
				$stmt->execute();
				$ret = $stmt->fetch();
				$ret = $ret->total;

			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			return $ret;
		}



		/** Old functions **/

		static function getTaskpriority($project_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();
			$priority = 0;

			try {
				$sql = "SELECT COUNT(*) AS priority FROM task t WHERE t.project_id = :project_id";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':project_id', $project_id);
				$stmt->execute();
				$row = $stmt->fetch();
				$priority = $row->priority;
				// $ret = $rows;
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			return $priority + 1;
		}

		static function getByUserId($user_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();
			$projects = null;

			try {
				$sql = "SELECT project_id FROM user_project WHERE user_id = :user_id";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $user_id);
				$stmt->execute();
				$rows = $stmt->fetchAll();
				$projects = $rows;
				// $ret = $rows;
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			foreach ($projects as $project) {
				$ret[] = Projects::getById($project->project_id);
			}

			return $ret;
		}

		/**
		* Depreciated
		**/
		static function getByProjectIdd($project_id) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = array();
			$tasks = null;

			try {
				$sql = "SELECT task_id FROM project_task WHERE project_id = :project_id 
							AND task_id IN ( SELECT id FROM task WHERE parent_id = 0 )";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':project_id', $project_id);
				$stmt->execute();
				$rows = $stmt->fetchAll();
				$tasks = $rows;
				// $ret = $rows;
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}

			foreach ($tasks as $task) {
				$ret[] = Tasks::getById($task->task_id);
			}

			return $ret;
		}
	}

?>