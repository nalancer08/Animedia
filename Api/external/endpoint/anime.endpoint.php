<?php

	class EndpointAnime extends Endpoint {

		function init() {

			global $app;

			# Add endpoint routes
			$app->addRoute('post', '/animes',          				'EndpointAnime::animes');
			$app->addRoute('post', '/animes/latest/medias',         'EndpointAnime::latestAnimesMedias');
			$app->addRoute('post', '/animes/latest',	          	'EndpointAnime::latestAnimes');
			$app->addRoute('post', '/animes/orderAsc',	          	'EndpointAnime::orderAsc');
			$app->addRoute('post', '/animes/search',	    		'EndpointAnime::search');
			$app->addRoute('post', '/animes/search/genres',	    	'EndpointAnime::animesByGenre');
			$app->addRoute('post', '/animes/genres',	          	'EndpointAnime::genres');
			$app->addRoute('post', '/animes/genre/new',	          	'EndpointAnime::newGenre');

			$app->addRoute('post', '/anime',          				'EndpointAnime::getAnimeDetails');
			$app->addRoute('post', '/anime/medias',          		'EndpointAnime::getAnimeMedias');
			$app->addRoute('post', '/anime/new',          			'EndpointAnime::newAnime');
		}

		/**
		* This method gives the last new animes
		**/
		static function animes() {

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

					// Alphabet
					// Year
					// Name
					// Lastest

					$params = array();
					//$params['by'] = 'modified';

					$animes = Animes::all($params);

					$ret->codeResponse(200);
					$ret->data = $animes;


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

		static function latestAnimesMedias() {

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
			# User UUID
			$uuid = $request->post('uuid', 0);

			//if ($bearer != '' && $user_id != 0) {
			//	if (Authentication::requireBearer($user_id, $bearer)) {

					$max_items = 15;
					$params = array();
					$params['by'] = 'created';
					$params['sort'] = 'desc';
					$params['show'] = 100;
					$medias = Medias::all($params);
					$animes_id = array();
					$animes = array();

					foreach ($medias as $media) {
						if (count($animes) <= $max_items) {
							if (!in_array($media->anime_id, $animes_id)) {
								$anime = Animes::getById($media->anime_id);
								if ($anime->latest == 1) {
									$animes_id[] = $media->anime_id;
									unset($media->description);
									$anime->media = $media;
									$animes[] = $anime;
								}
							}
						} else {
							break;
						}
					}

					$ret->codeResponse(200);
					$ret->data = $animes;

			/*	} else {
					$ret->codeResponse(500, "No match");
				}
			} else {
				$ret->codeResponse(400);
			} */

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, $client['key'], $user_id, $uuid);
			return $response->respond();
		}

		static function latestAnimesMediasOld() {

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

					$animes = Medias::latestAnimes();
					foreach ($animes as $anime) {
						$anime->media = Medias::getById($anime->media_id);
					}


					$ret->codeResponse(200);
					$ret->data = $animes;
					
			/*	} else {
					$ret->codeResponse(500, "No match");
				}
			} else {
				$ret->codeResponse(400);
			} */

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, $client['key'], $user_id);
			return $response->respond();
		}

		static function latestAnimes() {

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
			# Uuid
			$bit = $request->post('bit', '');
			$uuid = $request->post('uuid', '');
			$bit = Tokenizr::getData($bit);

			if ($bearer != '' && $user_id != 0 && $uuid != '' && $bit != '') {
				if (Authentication::requireBearer($user_id, $bearer) && Authentication::requireBearer($uuid, $bit)) {

					$params = array();
					$params['by'] = 'created';
					$params['show'] = 40;
					$params['sort'] = 'desc';
					$animes = Animes::all($params);

					$ret->codeResponse(200);
					$ret->data = $animes;
					
				} else {
					$ret->codeResponse(500, "No match");
				}
			} else {
				$ret->codeResponse(400);
			}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, $client['key'], $user_id, $uuid);
			return $response->respond();
		}

		static function orderAsc() {

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
			# Uuid
			$bit = $request->post('bit', '');
			$uuid = $request->post('uuid', '');
			$bit = Tokenizr::getData($bit);

			if ($bearer != '' && $user_id != 0 && $uuid != '' && $bit != '') {
				if (Authentication::requireBearer($user_id, $bearer) && Authentication::requireBearer($uuid, $bit)) {

					$params = array();
					$params['by'] = 'name';
					$params['show'] = 40;
					$params['sort'] = 'asc';
					$animes = Animes::all($params);

					$ret->codeResponse(200);
					$ret->data = $animes;
					
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

		static function newAnime() {

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
				//if (Authentication::requireBearer($user_id, $bearer)) {

					# Getting post variables
					$name = $request->post('name', '');
					$description = $request->post('description', '');
					$year = $request->post('year', '');
					$genres = $request->post('genres');
					$cover_url = $request->post('cover_url');
					$latest = $response->post('latest');

					# Getting image
					$file = $request->files('cover');

					# Creating anime
					$anime = new Anime();
					$anime->name = $name;
					$anime->description = $description;
					$anime->year = $year;
					$anime->latest = $latest;
					$anime->save();

					if ($anime) {

						$anime->cover = '';

						# Saving and match the image
						if ( $file && $file['name'] ) {
							$attachment = Attachments::upload($file);
							$anime->updateMeta('cover', $attachment->id);
							if ($attachment) {
								$anime->cover = $attachment->getImage();
							}
						}

						# Saving cover url
						if ($cover_url != "") {
							$anime->updateMeta('cover_url', $cover_url);
						}

						# Asign genres
						if ($genres != '') {
							$genresArray = explode(',', $genres);
							foreach ($genresArray as $genre) {
								$anime->assignGenre($genre);
							}
						}

						$ret->codeResponse(200);
						$ret->data = $anime;

					} else {
						$ret->codeResponse(500, "Please try again");
					}
				//} else {
				//	$ret->codeResponse(500, "No match");
				//}
			//} else {
			//	$ret->codeResponse(400);
			//}

			# Return payload
			$response->setHeader('Content-Type', 'application/json');
			$response->setBody( $ret->toJSON() );
			$managment->register($request, $response, $client['key'], 0);
			return $response->respond();
		}

		static function genres() {

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
			# Uuid
			$bit = $request->post('bit', '');
			$uuid = $request->post('uuid', '');
			$bit = Tokenizr::getData($bit);

			if ($bearer != '' && $user_id != 0 && $uuid != '' && $bit != '') {
				if (Authentication::requireBearer($user_id, $bearer) && Authentication::requireBearer($uuid, $bit) || $client['key'] == 'toolbel') {

					$genres = Genres::all();
					$ret->codeResponse(200);
					$ret->data = $genres;

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

		static function newGenre() {

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
				if (Authentication::requireBearer($user_id, $bearer) || $client['key'] = 'toolbelt') {

					$data = array();
					$genres = $request->post('genres');
					$genres = explode(',', $genres);
					foreach ($genres as $genre) {
						
						$genreObj = new Genre();
						$genreObj->name = $genre;
						$genreObj->save();
						$data[] = $genreObj;
					}

					$ret->codeResponse(200);
					$ret->data = $data;

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

		static function search() {

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
			# Uuid
			$bit = $request->post('bit', '');
			$uuid = $request->post('uuid', '');
			$bit = Tokenizr::getData($bit);

			if ($bearer != '' && $user_id != 0 && $uuid != '' && $bit != '') {
				if (Authentication::requireBearer($user_id, $bearer) && Authentication::requireBearer($uuid, $bit)) {

					$search = $request->post('search');
					$animes = Animes::searchByName($search);
					$ret->codeResponse(200);
					$ret->data = $animes;
					$ret->s = $search;
					
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

		static function animesByGenre() {

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
			# Uuid
			$bit = $request->post('bit', '');
			$uuid = $request->post('uuid', '');
			$bit = Tokenizr::getData($bit);

			if ($bearer != '' && $user_id != 0 && $uuid != '' && $bit != '') {
				if (Authentication::requireBearer($user_id, $bearer) && Authentication::requireBearer($uuid, $bit)) {

					$genres = $request->post('genres');
					$genres = str_replace('[', '', $genres);
					$genres = str_replace(']', '', $genres);

					if ($genres != '') {

						$animes = Animes::searchByGenres($genres);
						$ret->codeResponse(200);
						$ret->data = $animes;

					} else {
						$ret->codeResponse(500);
					}
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

		static function getAnimeDetails() {

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
			# Uuid
			$bit = $request->post('bit', '');
			$uuid = $request->post('uuid', '');
			$bit = Tokenizr::getData($bit);

			if ($bearer != '' && $user_id != 0 && $uuid != '' && $bit != '') {
				if (Authentication::requireBearer($user_id, $bearer) && Authentication::requireBearer($uuid, $bit) || $client['key'] == 'toolbelt') {

					$anime_id = $request->post('anime_id');
					$anime = Animes::getById($anime_id);

					if ($anime) {

						$chapters = $anime->getMedias();
						if (count($chapters) == 0) {
							$chapters = $anime->getMedias('chapter', 'latino');
						}

						$details = new stdClass();
						$details->genres = $anime->getGenres();
						$details->chapters = $chapters;
						$details->audios = $anime->getAvailableAudios();

						$ret->codeResponse(200);
						$ret->data = $details;
					}

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

		static function getAnimeMedias() {

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
			# Uuid
			$bit = $request->post('bit', '');
			$uuid = $request->post('uuid', '');
			$bit = Tokenizr::getData($bit);

			if ($bearer != '' && $user_id != 0 && $uuid != '' && $bit != '') {
				if (Authentication::requireBearer($user_id, $bearer) && Authentication::requireBearer($uuid, $bit) || $client['key'] == 'toolbelt') {

					$anime_id = $request->post('anime_id');
					$type = $request->post('type');
					$audio = $request->post('audio');

					$anime = Animes::getById($anime_id);

					if ($anime) {

						$chapters = $anime->getMedias($type, $audio);

						$ret->codeResponse(200);
						$ret->data = $chapters;
					}

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