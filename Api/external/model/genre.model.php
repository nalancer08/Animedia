<?php

	/**
	 * Genre Class
	 *
	 * Genre FTW
	 *
	 * @version  1
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com>
	 */
	class Genre extends CROOD {

		public $id;
		public $name;
		public $created;
		public $modified;

		/**
		 * Initialization callback
		 * @return nothing
		 */
		function init($args = false) {

			$now = date('Y-m-d H:i:s');

			$this->table = 					'genre';
			$this->table_fields = 			array('id', 'name', 'created', 'modified');
			$this->update_fields = 			array('name', 'modified');
			$this->singular_class_name = 	'Genre';
			$this->plural_class_name = 		'Genres';

			# MetaModel
			$this->meta_id = 				'';
			$this->meta_table = 			'';


			if (! $this->id ) {

				$this->id = 0;
				$this->name = '';
				$this->created = $now;
				$this->modified = $now;
			}

			else {

				$args = $this->preInit($args);

				# Enter your logic here
				# ----------------------------------------------------------------------------------
				# ----------------------------------------------------------------------------------

				$this->postInit($args);
			}
		}
	}

	# ==============================================================================================

	/**
	 * Genres Class
	 *
	 * Genres
	 *
	 * @version 1.0
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com>
	 */
	class Genres extends NORM {

		protected static $table = 					'genre';
		protected static $table_fields = 			array('id', 'name', 'created', 'modified');
		protected static $singular_class_name = 	'Genre';
		protected static $plural_class_name = 		'Genres';
	}
?>