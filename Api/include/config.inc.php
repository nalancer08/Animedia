<?php
	/**
	 * config.inc.php
	 * Here's where you configure your Dragonfly instance
	 */

	# Set the active profile
	define( 'PROFILE', 'development' );
	define( 'VERSION', '2.0' );

	/**
	 * Site settings
	 * @var array 	Array with configuration options
	 */
	$settings = array(
		'development' => array(
			# Global settings
			'site_url' => 'http://localhost/appbuilders/apis/animedia/',
			# Database settings
			'db_driver' => 'mysql',
			'db_host' => 'localhost',
			'db_user' => 'root',
			'db_pass' => '',
			'db_name' => 'animedia',
			'db_file' => BASE_PATH . '/include/schema.sqlite',
			# Clients
			'app_key' => '42fedaf1ca610806bcb9ab89de9e73d6',
			'app_clients' => array(
				'80f8cc918a88a6b7a5894e8c9e7859dece7a7c25' => array(
					'key' => 'toolbelt',
					'name' => 'ToolBelt',
					'requires' => '1.0',
					'permissions' => array(
						'write',
						'read'
					)
				),
				'8f6952dfc83073f80afbc048857d52d533a57970' => array(
					'key' => 'android',
					'name' => 'Android',
					'requires' => '1.0',
					'permissions' => array(
						'write',
						'read'
					)
				),
				'65492fb7931fd77515ce3288a361dd090e5901ab' => array(
					'key' => 'ios',
					'name' => 'iOS',
					'requires' => '1.0',
					'permissions' => array(
						'write',
						'read'
					)
				),
				'fd0735aaa1f116d395afd56ac37acd4a2b1154b1' => array(
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
			'site_url' => '',
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
			'app_name' => 'Animedia',
			'app_version' => VERSION,
			# Security settings
			'pass_salt' => 'w&btXpoVxC9nrjA3>@DT.g5a<YGP20jMJQSkc=D&x.<7^bHkmw4aF0L=Q|Xhui',
			'token_salt' => '$I^_y@e!Mp4>u870&GYjP&pb!x02oU?cBX;Efm~c!%01sM-I+tHSRof~n@pV6!xj'
		)
	);
?>