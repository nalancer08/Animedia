<?php
	/**
	 * config.inc.php
	 * Here's where you configure your Dragonfly instance
	 */

	# Set the active profile
	define( 'PROFILE', 'development' );
	define( 'VERSION', '1.0' );

	/**
	 * Site settings
	 * @var array 	Array with configuration options
	 */
	$settings = array(
		'development' => array(
			# Global settings
			'site_url' => 'http://localhost/appbuilders/apis/du/',
			# Database settings
			'db_driver' => 'mysql',
			'db_host' => 'localhost',
			'db_user' => 'root',
			'db_pass' => '',
			'db_name' => 'du',
			'db_file' => BASE_PATH . '/include/schema.sqlite',
			# Clients
			'app_key' => '00937ef9bfca40d1b6d16c13c3ca39bc',
			'app_clients' => array(
				'32407f13b8a1ceb1556221546346abee' => array(
					'key' => 'toolbelt',
					'name' => 'ToolBelt',
					'requires' => '1.0',
					'permissions' => array(
						'write',
						'read'
					)
				),
				'e6fda0f0d3e0adfff69e334462d1ef6a' => array(
					'key' => 'android',
					'name' => 'Android',
					'requires' => '1.0',
					'permissions' => array(
						'write',
						'read'
					)
				),
				'be72d1a7d3f0b1c52d95089056f202fe' => array(
					'key' => 'web',
					'name' => 'Web',
					'requires' => '1.0',
					'permissions' => array(
						'write',
						'read'
					)
				)
			),
			# Plugins
			'plugins' => array()
		),
		'testing' => array(
			# Global settings
			'site_url' => 'http://api.elchangodelaweb.com/loyalty',
			# Database settings
			'db_driver' => 'none',
			'db_host' => '',
			'db_user' => '',
			'db_pass' => '',
			'db_name' => '',
			'db_file' => BASE_PATH . '/include/schema.sqlite',
			# Clients
			'app_key' => '00937ef9bfca40d1b6d16c13c3ca39bc',
			'app_clients' => array(
				'32407f13b8a1ceb1556221546346abee' => array(
					'key' => 'debug',
					'name' => 'Debug',
					'requires' => '1.0',
					'permissions' => array(
						'write',
						'read'
					)
				)
			),
			# Plugins
			'plugins' => array()
		),
		'production' => array(
			# Global settings
			'site_url' => 'http://yourapp.com/api',
			# Database settings
			'db_driver' => 'none',
			'db_host' => '',
			'db_user' => '',
			'db_pass' => '',
			'db_name' => '',
			'db_file' => BASE_PATH . '/include/schema.sqlite',
			# Plugins
			'plugins' => array()
		),
		'shared' => array(
			# General
			'app_name' => 'TODO List API',
			'app_version' => VERSION,
			# Security settings
			'pass_salt' => 'M0f4Ukm=}ob%5S)FTZP#.<G$1[fLkzT6d!G"B+iz.i"9p4M`8y0G9JS}TXfQX,6O',
			'token_salt' => '2f2J[SiwEV[PGI<9E4A0d&g-o$w~91cP_OjYcRb<[6EJbh!<0F7V*u?hQ^UF?hsY'
		)
	);
?>