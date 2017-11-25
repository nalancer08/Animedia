<?php

	class EndpointMedia extends Endpoint {

		function init() {

			global $app;

			# Add endpoint routes
			$app->addRoute('post', '/media/new',          'EndpointMedia::newMedia');
		}

		static function newMedia() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			# Token
			$client = Authentication::requireToken();

			# Payload
			$ret = new Payload();

			# Bearear
			$bearer = $request->post('bearer');
			$user_id = $request->post('user_id', 0);
			$bearer = Tokenizr::getData($bearer);

			//if ($bearer != '' && $user_id != 0) {
			//	if (Authentication::requireBearer($user_id, $bearer)) {

					# Getting post variables
					$anime_id = $request->post('anime_id');
					$number = $request->post('number');
					$name = $request->post('name');
					$description = $request->post('description');
					$type = $request->post('type');
					$audio = $request->post('audio');

					# Extra variables
					$url = $request->post('url');
					$file = $request->files('thumbnail');

					$media = new Media();
					$media->anime_id = $anime_id;
					$media->number = $number;
					$media->name = $name;
					$media->description = $description;
					$media->type = $type;
					$media->audio = $audio;
					$media->save();

					if ($media) {

						# Saving and match the image
						if ( $file && $file['name'] ) {
							$attachment = Attachments::upload($file);
							$media->updateMeta('thumbnail', $attachment->id);
							if ($attachment) {
								$media->thumbnail = $attachment->getImage();
							}
						}

						$media->updateMeta('url', $url);

						$ret->codeResponse(200);
						$ret->data = $media;

					} else {
						$ret->codeResponse(500, "Please try again");
					}
					
				/*} else {
					$ret->codeResponse(500, "No match");
				}
			} else {
				$ret->codeResponse(400);
			}*/

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, $client['key'], $user_id);
			return $response->respond();
		}

		static function base() {

			global $app;
			$request = $app->getRequest();
			$response = $app->getResponse();
			$managment = $app->getManagment();

			# Token
			$client = Authentication::requireToken();

			# Payload
			$ret = new Payload();

			# Bearear
			$bearer = $request->post('bearer');
			$user_id = $request->post('user_id', 0);
			$bearer = Tokenizr::getData($bearer);

			if ($bearer != '' && $user_id != 0) {

				if (Authentication::requireBearer($user_id, $bearer)) {

					
				} else {
					$ret->codeResponse(500, "No match");
				}
			} else {
				$ret->codeResponse(400);
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, $client['key'], $user_id);
			return $response->respond();
		}
	}
?>