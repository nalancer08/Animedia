<?php

	/**
	 * Camera Class
	 *
	 * Camera FTW
	 *
	 * @version  1
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com>
	 */
	class Camera extends CROOD {

		public $id;
		public $type;
		public $lat;
		public $lng;
		public $created;
		public $modified;

		/**
		 * Initialization callback
		 * @return nothing
		 */
		function init($args = false) {

			$now = date('Y-m-d H:i:s');

			$this->table = 					'camera';
			$this->table_fields = 			array('id', 'type', 'lat', 'lng', 'created', 'modified');
			$this->update_fields = 			array('type', 'lat', 'lng', 'modified');
			$this->singular_class_name = 	'Camera';
			$this->plural_class_name = 		'Camaras';

			# MetaModel
			$this->meta_id = 				'';
			$this->meta_table = 			'';


			if (! $this->id ) {

				$this->id = 0;
				$this->type = '';
				$this->lat = '';
				$this->lng = '';
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


		function getAreas($status = '') {

			global $site;
			$dbh = $site->getDatabase();
			$ret = array();

			if ( $status == '') {
				$sql = "SELECT a.*, ea.activa FROM empleado_areas ea, area_tecnica a WHERE ea.id_empleado = :id_empleado AND a.id = ea.id_area";
				// $sql = "SELECT * FROM empleado_areas WHERE id_empleado = :id_empleado";
			} else if ( $status == 'active' ) {
				$sql = "SELECT a.*, ea.activa FROM empleado_areas ea, area_tecnica a WHERE ea.id_empleado = :id_empleado AND ea.activa = 1 AND a.id = ea.id_area";
				//$sql = "SELECT * FROM empleado_areas WHERE id_empleado = :id_empleado AND activa = 1";
			}

			global $site;
			$dbh = $site->getDatabase();
			$ret = 0;

			try {
				//$sql = "SELECT * FROM empleado_areas WHERE id_empleado = :id_empleado";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':id_empleado', $this->id);
				$stmt->execute();
				$rows = $stmt->fetchAll();
				// $ret = $rows;
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}
			return $ret;
		}

		function changeStatus($id_area, $id_perfil, $status = false, $razon = '') {

			global $site;
			$dbh = $site->getDatabase();
			$ret = false;

			$status = $status === false ? !$this->checkStatus($id_area, $id_perfil) : $status;
			$categoria = $this->checkCategoria($id_perfil, $id_area);

			$prevs = $this->checkCategoria($id_perfil);

			switch($status) {
				case 0:
					if ( $categoria == 1 ) {
						$categoria = 0;
					}
					// if ( $categoria == 1 ) { $categoria = 0; }
				break;

				case 1:
					if ( $prevs == 0 ) {
						$categoria = 1;
					}
					// if ( $categoria == 0 ) { $categoria = 1; }
				break;
			}

			try {
				$sql = "INSERT INTO empleado_areas(id_empleado, id_area, id_perfil, activa, razon, categoria) VALUES(:id_empleado, :id_area, :id_perfil, :activa, :razon, :categoria) ON DUPLICATE KEY UPDATE activa = :activa, razon = :razon, categoria = :categoria";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':activa', $status);
				$stmt->bindValue(':id_empleado', $this->id);
				$stmt->bindValue(':id_area', $id_area);
				$stmt->bindValue(':id_perfil', $id_perfil);
				$stmt->bindValue(':razon', $razon);
				$stmt->bindValue(':categoria', $categoria);

				$stmt->execute();
				$ret = true;

			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

		function checkArea($id_area) {

			global $site;
			$dbh = $site->getDatabase();
			$ret = array();

			try {
				$sql = "SELECT * FROM empleado_areas WHERE id_empleado = :id_empleado AND id_area = :id_area";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':id_empleado', $this->id);
				$stmt->bindValue(':id_area', $id_area);
				$stmt->execute();
				$row = $stmt->fetch();
				$ret = $row;
			} catch (PDOException $e) {
				//
			}
			return $ret;
		}

		function checkStatus($id_area, $id_perfil) {

			global $site;
			$dbh = $site->getDatabase();
			//$ret = array();

			try {
				$sql = "SELECT activa FROM empleado_areas WHERE id_area = :id_area AND id_empleado = :id_personal AND id_perfil = :id_perfil";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':id_area', $id_area);
				$stmt->bindValue(':id_personal', $this->id);
				$stmt->bindValue(':id_perfil', $id_perfil);
				$stmt->execute();
				$status = $stmt->fetch();
				$status = $status->activa;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return (boolean) $status;
		}

		# Solo para SG 22000
		function checkCategoria($id_perfil, $id_area = false) {

			global $site;
			$dbh = $site->getDatabase();
			//$ret = array();

			if ($id_area) {
				try {
					$sql = "SELECT COUNT(categoria) as suma FROM empleado_areas WHERE id_empleado = :id_personal AND categoria = 1 AND id_area = :id_area AND id_perfil = :id_perfil";
					$stmt = $dbh->prepare($sql);
					$stmt->bindValue(':id_personal', $this->id);
					$stmt->bindValue(':id_area', $id_area);
					$stmt->bindValue(':id_perfil', $id_perfil);
					$stmt->execute();
					$suma = $stmt->fetch();
					$suma = $suma->suma;
				} catch (PDOException $e) {
					echo $e->getMessage();
				}
			} else {
				try {
					$sql = "SELECT COUNT(categoria) as suma FROM empleado_areas WHERE id_empleado = :id_personal AND categoria = 1 AND id_perfil = :id_perfil";
					$stmt = $dbh->prepare($sql);
					$stmt->bindValue(':id_personal', $this->id);
					$stmt->bindValue(':id_perfil', $id_perfil);
					$stmt->execute();
					$suma = $stmt->fetch();
					$suma = $suma->suma;
				} catch (PDOException $e) {
					echo $e->getMessage();
				}
			}

			return $suma;
		}


		function insert($id_area, $status, $categoria = 0) {

			global $site;
			$dbh = $site->getDatabase();

			try {
				$sql = "INSERT INTO empleado_areas (id_empleado, id_area, activa, categoria) VALUES (:id_personal, :id_area, :status, :categoria)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':id_personal', $this->id);
				$stmt->bindValue(':id_area', $id_area);
				$stmt->bindValue(':status', $status);
				$stmt->bindValue(':categoria', $categoria);
				$stmt->execute();
				$ret['status'] = 'success';
			} catch (PDOException $e) {
				echo $e->getMessage();
			}
		}

		function getConocimientosAdquiridos() {

			global $site;
			$dbh = $site->getDatabase();

			try {
				$sql = "SELECT * FROM evidencia_sistema_gestion WHERE id_personal = :id_personal GROUP BY id_usuario, columna, id_conocimiento ORDER BY id_sistema_gestion";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':id_personal', $this->id);
				$stmt->execute();
				$rows = $stmt->fetchAll();
				// $ret['status'] = 'success';
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			if ( $rows || count($rows) > 0 ) {

				// print_a($rows);

				$conocimientos = array();

				$conocimientos_sg_1 = array();
				$conocimientos_sg_2 = array();
				$conocimientos_sg_3 = array();
				$conocimientos_sg_4 = array();
				$conocimientos_sg_5 = array();

				foreach ($rows as $row) {

					switch($row->id_sistema_gestion) {

						case 1:
							$conocimientos_sg_1[] = array($row->id_conocimiento => $row->columna);
						break;

						case 2:
							$conocimientos_sg_2[] = array($row->id_conocimiento => $row->columna);
						break;

						case 3:
							$conocimientos_sg_3[] = array($row->id_conocimiento => $row->columna);
						break;

						case 4:
							$conocimientos_sg_4[] = array($row->id_conocimiento => $row->columna);
						break;

						case 5:
							$conocimientos_sg_5[] = array($row->id_conocimiento => $row->columna);
						break;
					}
				}

				$conocimientos["sg1"] = $conocimientos_sg_1;
				$conocimientos["sg2"] = $conocimientos_sg_2;
				$conocimientos["sg3"] = $conocimientos_sg_3;
				$conocimientos["sg4"] = $conocimientos_sg_4;
				$conocimientos["sg5"] = $conocimientos_sg_5;

				// print_a($conocimientos);
				// exit;

				return $conocimientos;
			}

		}

	}

	# ==============================================================================================

	/**
	 * Cameras Class
	 *
	 * Cameras
	 *
	 * @version 1.0
	 * @author   Erick Sanchez <erick.sanchez@appbuilders.com>
	 */
	class Cameras extends NORM {

		protected static $table = 					'camera';
		protected static $table_fields = 			array('id', 'type', 'lat', 'lng', 'created', 'modified');
		protected static $singular_class_name = 	'Camera';
		protected static $plural_class_name = 		'Cameras';


		static function existsCamera($type, $lat, $lng) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = null;

			try {
				$sql = "SELECT * FROM camera WHERE type = :type AND lat = :lat AND lng = :lng";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':type', $type);
				$stmt->bindValue(':lat', $lat);
				$stmt->bindValue(':lng', $lng);
				$stmt->execute();
				$stmt->setFetchMode(PDO::FETCH_CLASS, "Camera", null);
				$rows = $stmt->fetch();
				$ret = $rows;
				// $ret = $rows;
			} catch (PDOException $e) {
				log_to_file( "Database error: {$e->getCode()} (Line {$e->getLine()}) in {$class_name}::count(): {$e->getMessage()}", 'norm' );
			}
			return $ret;
		}

		/**
		* This method can handle like and dislike
		* @param id: This is the id of camera
		* @param type: This the type, 1: like, 2: dislike 
		*/
		static function setStatusCamera($id, $type) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = false;

			try {
				$sql = "INSERT INTO likes(camera_id, type) VALUES(:id, :type)";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':id', $id);
				$stmt->bindValue(':type', $type);
				$stmt->execute();
				$ret = true;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}

		static function countStatus($id, $type) {

			global $app;
			$dbh = $app->getDatabase();
			$ret = 0;

			try {
				$sql = "SELECT COUNT(*) AS count FROM likes WHERE camera_id = :id AND type = :type";
				$stmt = $dbh->prepare($sql);
				$stmt->bindValue(':id', $id);
				$stmt->bindValue(':type', $type);
				$stmt->execute();
				$row = $stmt->fetch();
				$ret = $row->count;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}

			return $ret;
		}
	}

?>