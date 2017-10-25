<?php

	/**
	 * Organization Class
	 *
	 * This model handle the organizations into the application
	 *
	 * @version  1.0
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com.mx>
	 */
	class Organization extends CROOD {

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

			$this->table = 					'organization';
			$this->table_fields = 			array('id', 'name', 'created', 'modified');
			$this->update_fields = 			array('name', 'modified');
			$this->search_fields = 			array();
			$this->singular_class_name = 	'Organization';
			$this->plural_class_name = 		'Organizations';

			# MetaModel
			$this->meta_id = 				'organization_id';
			$this->meta_table = 			'organization_meta';

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

				//Fetch metas
				if (is_array($args) && in_array('fetch_metas', $args)) {

					$this->metas = $this->getMetas();
				}

				// if(array_key_exists('fetch_categorias', $args) && $args['fetch_categorias']) {

				// 	$this->categorias = CategoriasPerfil::allByIdPerfil( $this->id, isset($args['categoria']) ? $args['categoria'] : array() );
				// }

				# ----------------------------------------------------------------------------------

				$args = $this->postInit($args);
			}
		}

		function assingUser($user) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO user_organization(user_id, project_id) VALUES(:user_id, :project_id)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':user_id', $user->id);
				$stmt->bindValue(':project_id', $this->id);
				$stmt->execute();
				$ret = true;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

		function assingProduct($product) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO organization_product(organization_id, product_id) VALUES(:organization_id, :product_id)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':organization_id', $this->id);
				$stmt->bindValue(':product_id', $product->id);
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
	 * Organizations Class
	 *
	 * This model handle the organizations into the application
	 *
	 * @version  1.0
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com.mx>
	 */
	class Organizations extends NORM {

		protected static $table = 					'organization';
		protected static $table_fields = 			array('id', 'name', 'created', 'modified');
		protected static $singular_class_name = 	'Organization';
		protected static $plural_class_name = 		'Organizations';
	}
?>