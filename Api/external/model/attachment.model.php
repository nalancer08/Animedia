<?php

	/**
	 * Attachment Class
	 *
	 * Clase singular de attachment
	 *
	 * @version  1
	 * @author   Erick <erick.sanchez@thewebchi.mp>
	 */
	class Attachment extends CROOD {

		public $id;
		public $slug;
		public $name;
		public $attachment;
		public $mime;
		public $source;
		public $created;
		public $modified;

		/**
		 * Initialization callback
		 * @return nothing
		 */
		function init($args = false) {

			$now = date('Y-m-d H:i:s');

			$this->table = 					'attachment';
			$this->table_fields = 			array('id', 'slug', 'name', 'attachment', 'mime', 'source', 'created', 'modified');
			$this->update_fields = 			array('slug', 'name', 'attachment', 'mime', 'source', 'modified');
			$this->singular_class_name = 	'Attachment';
			$this->plural_class_name = 		'Attachments';

			# MetaModel
			$this->meta_id = 				'attachment_id';
			$this->meta_table = 			'attachment_meta';


			if (! $this->id ) {

				$this->id = 0;
				$this->slug = '';
				$this->name = '';
				$this->attachment = '';
				$this->mime = '';
				$this->source = 'local';
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

				$is_image = false;
				switch ($this->mime) {
					case 'image/png':  $is_image = true; break;
					case 'image/gif':  $is_image = true; break;
					case 'image/jpeg': $is_image = true; break;
				}

				switch ( $this->source ) {

					case '':
					case 'local':

							$this->url = $this->getUrl();
							$this->thumb = $is_image ? $this->getImage() : '';
							$this->isImage = $is_image;
					break;

					case 's3':
							$this->url = $this->getS3Url();
							$this->thumb = $is_image ? $this->getS3Image() : '';
							$this->isImage = $is_image;
					break;

				}

				// if ( $this->source == '' || $this->source == 'local' ) {}
			}
		}

		function getPath($echo = false) {
			global $app;
			$ret = false;
			$dir = date('Y/m', strtotime($this->created));
			$ret = $app->baseDir("/uploads/$dir/{$this->attachment}");
			return $ret;
		}

		function getUrl($echo = false) {
			global $app;
			$ret = false;
			$dir = date('Y/m', strtotime($this->created));
			$ret = $app->urlTo("/uploads/$dir/{$this->attachment}");
			return $ret;
		}

		function getImage($type = 'url', $size = 'thumbnail', $echo = false) {
			global $app;
			$ret = false;
			if ( substr($this->mime, 0, 5) == 'image' ) {
				# Generate path
				$dir = date('Y/m', strtotime($this->created));
				# Generate the image object (just in case)
				switch ($this->mime) {
					case 'image/png':  $ext = 'png'; break;
					case 'image/gif':  $ext = 'gif'; break;
					case 'image/jpeg': $ext = 'jpg'; break;
				}
				$image = array(
					'url' => $app->urlTo("/uploads/$dir/{$this->slug}.{$ext}"),
					'sizes' => array(
						//'avatar' => $app->urlTo("/uploads/$dir/{$this->slug}-avatar.{$ext}"),
						//'avatar-mini' => $app->urlTo("/uploads/$dir/{$this->slug}-avatar-mini.{$ext}"),
						'thumbnail' => $app->urlTo("/uploads/$dir/{$this->slug}-thumb.{$ext}"),
						//'medium' => $app->urlTo("/uploads/$dir/{$this->slug}-medium.{$ext}"),
						'large' => $app->urlTo("/uploads/$dir/{$this->slug}-large.{$ext}")
					)
				);
				# Return what the user wants
				switch ($type) {
					case 'url':
						$ret = isset( $image['sizes'][$size] ) ? $image['sizes'][$size] : $image['url'];
						break;
					case 'img':
						$ret = isset( $image['sizes'][$size] ) ? "<img src=\"{$image['sizes'][$size]}\" alt=\"\" />" : "<img src=\"{$image['url']}\" alt=\"\" />";
						break;
					case 'object':
						$ret = $image;
						break;
				}
				if ($echo) {
					echo $ret;
				}
			}
			return $ret;
		}


		///////////////////////////////////// S3 Functions ////////////////////////////////////////////////////

		function getS3Url($echo = false, $pref = "") {
			global $app;

			# Get parameters from profile
			$s3_key = $app->getOption('s3_key');
			$s3_secret = $app->getOption('s3_secret');
			$s3_bucket = $app->getOption('s3_bucket');

			# Check is some parameter is not define
			if ( !$s3_key && !$s3_secret && !$s3_bucket ) {
				return "Error, S3 config";
			}

			# Include the library of S3
			require $app->baseDir('/lib/Aws/aws-autoloader.php');
			$s3Client =  Aws\S3\S3Client::factory(array(
				'key'    => $s3_key,
				'secret' => $s3_secret
			));

			$ret = false;
			$dir = date('Y/m', strtotime($this->created));
			// print_a($dir . "/{$this->attachment}");
			if ( $pref == "" ) {
				$ret = $s3Client->getObjectUrl($s3_bucket, "{$dir}/{$this->attachment}", '+10 minutes');
			} else {
				$ret = $s3Client->getObjectUrl($s3_bucket, $pref, '+10 minutes');
			}
			return $ret;
		}

		function getS3Image($type = 'url', $size = 'thumbnail', $echo = false) {
			global $app;
			$ret = false;

			if ( substr($this->mime, 0, 5) == 'image' ) {
				# Generate path
				$dir = date('Y/m', strtotime($this->created));
				# Generate the image object (just in case)
				switch ($this->mime) {
					case 'image/png':  $ext = 'png'; break;
					case 'image/gif':  $ext = 'gif'; break;
					case 'image/jpeg': $ext = 'jpg'; break;
				}
				$image = array(
					'url' => "$dir/{$this->slug}.{$ext}",
					'sizes' => array(
						'avatar' => "$dir/{$this->slug}-avatar.{$ext}",
						'avatar-mini' => "$dir/{$this->slug}-avatar-mini.{$ext}",
						'thumbnail' => "$dir/{$this->slug}-thumb.{$ext}",
						'medium' => "$dir/{$this->slug}-medium.{$ext}",
						'large' => "$dir/{$this->slug}-large.{$ext}"
					)
				);
				# Return what the user wants
				switch ($type) {
					case 'url':
						$ret  = isset( $image['sizes'][$size] ) ? $this->getS3Url(false, $image['sizes'][$size]) : $this->getS3Url();
						break;
					case 'img':
						$ret = isset( $image['sizes'][$size] ) ? "<img src=\"{$image['sizes'][$size]}\" alt=\"\" />" : "<img src=\"{$image['url']}\" alt=\"\" />";
						break;
					case 'object':
						$ret = $image;
						break;
				}
				if ($echo) {
					echo $ret;
				}
			}
			return $ret;
		}

		function migrateFileToS3($delete = false) {


			global $app;
			$ret = null;

			# Get parameters from profile
			$s3_key = $app->getOption('s3_key');
			$s3_secret = $app->getOption('s3_secret');
			$s3_bucket = $app->getOption('s3_bucket');

			# Check is some parameter is not define
			if ( !$s3_key && !$s3_secret && !$s3_bucket ) {
				return "Error, S3 config";
			}

			# Include the library of S3
			require $app->baseDir('/lib/Aws/aws-autoloader.php');
			$s3Client =  Aws\S3\S3Client::factory(array(
				'key'    => $s3_key,
				'secret' => $s3_secret
			));

			$dest_dir = date('Y/m', strtotime($this->created));
			$dir_y = date('Y', strtotime($this->created));
			$dir_m = date('m', strtotime($this->created));
			$mime = $this->mime;

			# Chekc if folders exist en S3
			if ( !Attachments::checkS3Folder($s3Client, $dest_dir) ) {
				Attachments::createS3Folder($s3Client, "{$dir_y}");
				Attachments::createS3Folder($s3Client, "{$dir_y}/{$dir_m}");
			}

			/// Hasta aqui ya cree los folders si los necesito y todo el asunto

			# Generate a destination name
			$dest_name = $this->slug;
			$ext = substr( $this->attachment, strrpos($this->attachment, '.') + 1 );
			$dest_path = "{$dest_dir}/{$dest_name}.{$ext}";

			// print_a($this->getImage());

			$images = array(
				'normal' => $app->baseDir("/uploads/{$dest_path}"),
				'avatar' => $app->baseDir("/uploads/{$dest_dir}/{$dest_name}-avatar.{$ext}"),
				'avatar-mini' => $app->baseDir("/uploads/{$dest_dir}/{$dest_name}-avatar-mini.{$ext}"),
				'thumbnail' => $app->baseDir("/uploads/{$dest_dir}/{$dest_name}-thumb.{$ext}"),
				'medium' => $app->baseDir("/uploads/{$dest_dir}/{$dest_name}-medium.{$ext}"),
				'large' => $app->baseDir("/uploads/{$dest_dir}/{$dest_name}-large.{$ext}")
			);
			// print_a($images);

			# Check whether the name exists nor not
			if ( Attachments::checkS3File($s3Client, $dest_path) ) {
				$dest_name = $app->toAscii( $this->name . uniqid() );
				$dest_path = "{$dest_dir}/{$dest_name}.{$ext}";

				$this->slug = $dest_name;
				$this->attachment = "{$dest_name}.{$ext}";
			}

			$imagesS3 = array(
				'normal' => "{$dest_dir}/{$dest_name}.{$ext}",
				'avatar' => "{$dest_dir}/{$dest_name}-avatar.{$ext}",
				'avatar-mini' => "{$dest_dir}/{$dest_name}-avatar-mini.{$ext}",
				'thumbnail' => "{$dest_dir}/{$dest_name}-thumb.{$ext}",
				'medium' => "{$dest_dir}/{$dest_name}-medium.{$ext}",
				'large' => "{$dest_dir}/{$dest_name}-large.{$ext}"
			);
			// print_a($imagesS3);

			#Crunching
			if ( substr($mime, 0, 5) == 'image' && $ext != 'tiff' && $ext != 'tif') {

				foreach($images as $key => $image) {

					// print_a($key);
					// print_a($image);
					// print_a($imagesS3[$key]);

					$uploadFile = $s3Client->putObject( array(
						'Bucket' => $s3_bucket,
						'Key'    => $imagesS3[$key],
						'SourceFile'   => $image,
						'ACL'    => 'private',
						'ContentType' => $mime
					) );

					// print_a($uploadFile);
					if ( $delete ) {
						unlink($image);
					}
				}

				// Cambiso osbre la marcha
				$this->thumb = $this->getS3Image();

			} else {

				// PARA PDFS O FORMATOS RAROS

				$uploadFile = $s3Client->putObject( array(
					'Bucket' => $s3_bucket,
					'Key'    => $dest_path,
					'SourceFile'   => $app->baseDir("/uploads/{$dest_path}"),
					'ACL'    => 'private',
					'ContentType' => $mime
				) );
			}

			$this->source = 's3';
			$this->save();

			// Cambios sobre la marcha
			$this->url = $this->getS3Url();

			return $ret;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////
	}

	# ==============================================================================================

	/**
	 * Attachments Class
	 *
	 * Clase plural de attachment
	 *
	 * @version 1.0
	 * @author  Raul Vera <raul.vera@thewebchi.mp>
	 */
	class Attachments extends NORM {

		protected static $table = 					'attachment';
		protected static $table_fields = 			array('id', 'slug', 'name', 'attachment', 'mime', 'source', 'created', 'modified');
		protected static $singular_class_name = 	'Attachment';
		protected static $plural_class_name = 		'Attachments';

		static function getAt($id) {
			if ( is_numeric($id) ) {
				return self::getById($id);
			} else {
				return self::getBySlug($id);
			}
		}

		static function uploadLocal($file) {
			global $app;
			$ret = null;
			//
			if( $file && $file['tmp_name'] ) {
				# Get name parts
				$name = substr( $file['name'], 0, strrpos($file['name'], '.') );
				$ext = substr( $file['name'], strrpos($file['name'], '.') + 1 );
				# Normalize JPEG extensions
				$ext = ($ext == 'jpeg') ? 'jpg' : $ext;
				# Check destination folder
				$year = date('Y');
				$month = date('m');
				$dest_dir = "{$year}/{$month}";
				if (! file_exists( $app->baseDir("/uploads/{$dest_dir}") ) ) {
					@mkdir( $app->baseDir("/uploads/{$year}") );
					@mkdir( $app->baseDir("/uploads/{$year}/{$month}") );
				}
				# Generate a destination name
				$dest_name = $app->toAscii($name);
				$dest_path = $app->baseDir("/uploads/{$dest_dir}/{$dest_name}.{$ext}");
				# Check whether the name exists nor not
				if ( file_exists($dest_path) ) {
					$dest_name = $app->toAscii( $name . uniqid() );
					$dest_path = $app->baseDir("/uploads/{$dest_dir}/{$dest_name}.{$ext}");
				}
				# Get MIME type
				if ( $file['type'] ) {
					$mime = $file['type'];
				} else {
					switch ($ext) {
						case 'gif':
						case 'png':
							$mime = "image/{$ext}";
						case 'jpg':
							$mime = 'image/jpeg';
							break;
						case 'mpeg':
						case 'mp4':
						case 'ogg':
						case 'webm':
							$mime = "video/{$ext}";
							break;
						case 'pdf':
						case 'zip':
							$mime = "application/{$ext}";
							break;
						case 'csv':
						case 'xml':
							$mime = "text/{$ext}";
							break;
						default:
							$mime = 'application/octet-stream';
					}
				}
				# Crunching
				if ( substr($mime, 0, 5) == 'image' ) {
					$images = array(
						'thumbnail' => $app->baseDir("/uploads/{$dest_dir}/{$dest_name}-thumb.{$ext}"),
						'large' => $app->baseDir("/uploads/{$dest_dir}/{$dest_name}-large.{$ext}")
					);
					require_once $app->baseDir('/lib/PHPThumb/ThumbLib.inc.php');
					try {
						# Thumbnail
						$thumb = PhpThumbFactory::create( $file['tmp_name'] );
						$thumb->adaptiveResize(650, 200);
						$thumb->save($images['thumbnail']);
						# Large image
						$thumb = PhpThumbFactory::create( $file['tmp_name'] );
						$thumb->resize(1410, 300);
						$thumb->save($images['large']);
					} catch (Exception $e) {
						error_log( $e->getMessage() );
					}
				}
				# Move the uploadsed file
				# Discomment if you want the original file
				//move_uploaded_file($file['tmp_name'], $dest_path);
				
				# Create and save the attachment
				$attachment = new Attachment();
				$attachment->slug = $dest_name;
				$attachment->name = $name;
				$attachment->attachment = "{$dest_name}.{$ext}";
				$attachment->mime = $mime;
				$attachment->save();
				$ret = $attachment;
			}
			return $ret;
		}

		////////////////////////////////////////// S3 FUNCTIONS :3 /////////////////////////////////////////////////
		static function uploadS3($file) {

			global $app;
			$ret = null;

			# Get parameters from profile
			$s3_key = $app->getOption('s3_key');
			$s3_secret = $app->getOption('s3_secret');
			$s3_bucket = $app->getOption('s3_bucket');

			# Check is some parameter is not define
			if ( !$s3_key && !$s3_secret && !$s3_bucket ) {
				return "Error, S3 config";
			}

			# Include the library of S3
			require $app->baseDir('/lib/Aws/aws-autoloader.php');
			$s3Client =  Aws\S3\S3Client::factory(array(
				'key'    => $s3_key,
				'secret' => $s3_secret
			));

			if( $file && $file['tmp_name'] ) {

				# Get name parts
				$name = substr( $file['name'], 0, strrpos($file['name'], '.') );
				$ext = substr( $file['name'], strrpos($file['name'], '.') + 1 );
				$tamano = $file['size'];
				# Normalize JPEG extensions
				$ext = ($ext == 'jpeg') ? 'jpg' : $ext;
				# Check destination folder
				$year = date('Y');
				$month = date('m');
				$dest_dir = "{$year}/{$month}";

				# Chekc if folders exist en S3
				if ( !self::checkS3Folder($s3Client, $dest_dir) ) {
					self::createS3Folder($s3Client, "{$year}");
					self::createS3Folder($s3Client, "{$year}/{$month}");
				}

				# Generate a destination name
				$dest_name = $app->toAscii($name);
				$dest_path = "{$dest_dir}/{$dest_name}.{$ext}";

				# Check whether the name exists nor not
				if ( self::checkS3File($s3Client, $dest_path) ) {
					$dest_name = $app->toAscii( $name . uniqid() );
					$dest_path = "{$dest_dir}/{$dest_name}.{$ext}";
				}

				# Get MIME type
				if ( $file['type'] ) {
					$mime = $file['type'];
				} else {
					switch ($ext) {
						case 'gif':
						case 'png':
							$mime = "image/{$ext}";
						case 'jpg':
							$mime = 'image/jpeg';
							break;
						case 'tiff':
						case 'tif':
							$mime = 'image/tiff';
							break;
						case 'mpeg':
						case 'mp4':
						case 'ogg':
						case 'webm':
							$mime = "video/{$ext}";
							break;
						case 'pdf':
						case 'zip':
							$mime = "application/{$ext}";
							break;
						case 'csv':
						case 'xml':
							$mime = "text/{$ext}";
							break;
						default:
							$mime = 'application/octet-stream';
					}
				}

				#Crunching
				if ( substr($mime, 0, 5) == 'image' && $ext != 'tiff' && $ext != 'tif') {

					$images = array(
						'avatar' => $app->baseDir("/uploads/temp/{$dest_name}-avatar.{$ext}"),
						'avatar-mini' => $app->baseDir("/uploads/temp/{$dest_name}-avatar-mini.{$ext}"),
						'thumbnail' => $app->baseDir("/uploads/temp/{$dest_name}-thumb.{$ext}"),
						'medium' => $app->baseDir("/uploads/temp/{$dest_name}-medium.{$ext}"),
						'large' => $app->baseDir("/uploads/temp/{$dest_name}-large.{$ext}")
					);

					$imagesS3 = array(
						'avatar' => "{$dest_dir}/{$dest_name}-avatar.{$ext}",
						'avatar-mini' => "{$dest_dir}/{$dest_name}-avatar-mini.{$ext}",
						'thumbnail' => "{$dest_dir}/{$dest_name}-thumb.{$ext}",
						'medium' => "{$dest_dir}/{$dest_name}-medium.{$ext}",
						'large' => "{$dest_dir}/{$dest_name}-large.{$ext}"
					);

					require_once $app->baseDir('/lib/PHPThumb/ThumbLib.inc.php');
					try {
						#Avatar
						$thumb = PhpThumbFactory::create( $file['tmp_name'] );
						$thumb->adaptiveResize(96, 96);
						$thumb->save($images['avatar']);
						#Avatar-mini
						$thumb = PhpThumbFactory::create( $file['tmp_name'] );
						$thumb->adaptiveResize(48, 48);
						$thumb->save($images['avatar-mini']);
						# Thumbnail
						$thumb = PhpThumbFactory::create( $file['tmp_name'] );
						$thumb->adaptiveResize(150, 150);
						$thumb->save($images['thumbnail']);
						# Medium image
						$thumb = PhpThumbFactory::create( $file['tmp_name'] );
						$thumb->resize(300, 300);
						$thumb->save($images['medium']);
						# Large image
						$thumb = PhpThumbFactory::create( $file['tmp_name'] );
						$thumb->resize(1024, 1024);
						$thumb->save($images['large']);
					} catch (Exception $e) {
						error_log( $e->getMessage() );
					}

					foreach($images as $key => $image) {

						// print_a($key);
						// print_a($image);
						// print_a($imagesS3[$key]);

						$uploadFile = $s3Client->putObject( array(
							'Bucket' => $s3_bucket,
							'Key'    => $imagesS3[$key],
							'SourceFile'   => $image,
							'ACL'    => 'private',
							'ContentType' => $mime
						) );

						// print_a($uploadFile);
						unlink($image);
					}
				}
				# Move the uploadsed file // Subimos la imagen al server
				$destino_local = sprintf( '%s/%s', $app->baseDir('/uploads/temp'), "{$dest_name}.{$ext}" );
				move_uploaded_file($file['tmp_name'], $destino_local);

				$uploadFile = $s3Client->putObject( array(
					'Bucket' => $s3_bucket,
					'Key'    => $dest_path,
					'SourceFile'   => $destino_local,
					'ACL'    => 'private',
					'ContentType' => ($ext == 'pdf' ? 'application/pdf' : $mime )
				) );
				unlink($destino_local);

				# Create and save the attachment
				$attachment = new Attachment();
				$attachment->slug = $dest_name;
				$attachment->name = $name;
				$attachment->attachment = "{$dest_name}.{$ext}";
				$attachment->mime = $mime;
				$attachment->source = 's3';
				$attachment->save();
				$ret = $attachment;
			}
			return $ret;
		}

		static function createS3Folder($s3Client, $folder_name, $permissions_mode = 'private') {

			global $app;
			$s3_bucket = $app->getOption('s3_bucket');
			$s3Client->putObject( array(
				'Bucket' => $s3_bucket,
				'Key'    => "{$folder_name}/",
				'Body'   => "",
				'ACL'    => $permissions_mode
			) );
		}

		static function checkS3Folder($s3Client, $folder_name) {

			global $app;
			$s3_bucket = $app->getOption('s3_bucket');
			$folder = $s3Client->doesObjectExist( $s3_bucket , "{$folder_name}/");
			return $folder;
		}

		static function checkS3File($s3Client, $file_name) {

			global $app;
			$s3_bucket = $app->getOption('s3_bucket');
			$file = $s3Client->doesObjectExist( $s3_bucket , "{$file_name}");
			return $file;
		}


		//////////////////////////////////////////////////////////////////////////////////////////////////////////


		static function upload($file, $upload_mode = 'local') {

			if ( $upload_mode == 's3' ) {
				return self::uploadS3($file);
			} else {
				return self::uploadLocal($file);
			}
		}
	}
?>