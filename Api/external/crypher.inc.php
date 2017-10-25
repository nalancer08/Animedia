<?php

/**
* Helper library for CryptoJS AES encryption/decryption
* Allow you to use AES encryption on client side and server side vice versa
*
* @author Erick Sanchez (appbuilders.com.mx) CEO & CTO App Builders
* @link https://github.com/nalancer08/Crypher
*/

class Crypher {

    protected $passphrase;
    
    /**
    * This method allow set a pass phrase for encrypt and decrypt
    * @param phrase: String phrase to be saved into the object
    **/
    function setPassphrase($phrase) {
       
        $this->passphrase = $phrase;
    }

    /**
    * Thsi method decrypt data from a CryptoJS json encoding string
    * @param jsonString: Json with the neccesary values to decrypt
    */
    function decrypt($jsonString) {

        $jsondata = json_decode($jsonString, true);

        try {

            $salt = hex2bin($jsondata["s"]);
            $iv  = hex2bin($jsondata["iv"]);

        } catch (Exception $e) { 
            return null; 
        }

        $ct = base64_decode( $jsondata["ct"] );

        $concatedPassphrase = $this->passphrase . $salt;
        $md5 = array();
        $md5[0] = md5($concatedPassphrase, true);
        $result = $md5[0];

        for ($i = 1; $i < 3; $i++) {
            $md5[$i] = md5($md5[$i - 1] . $concatedPassphrase, true);
            $result .= $md5[$i];
        }

        $key = substr($result, 0, 32);
        $data = openssl_decrypt($ct, 'aes-256-cbc', $key, true, $iv);
        return $data;
        // return json_decode($data, true);
    }

    /**
    * This method encrypt value to a cryptojs compatiable json encoding string
    * @param value: String o data to be encrypt
    */
    function encrypt($value){

        $salt = openssl_random_pseudo_bytes(8);
        $salted = '';
        $dx = '';

        while (strlen($salted) < 48) {
            $dx = md5( $dx . $this->passphrase . $salt, true );
            $salted .= $dx;
        }


        $key = substr($salted, 0, 32);
        $iv  = substr($salted, 32,16);
        $encrypted_data = openssl_encrypt(json_encode($value), 'aes-256-cbc', $key, true, $iv);
        $data = array("ct" => base64_encode($encrypted_data), "iv" => bin2hex($iv), "s" => bin2hex($salt));
        return $data;
        // return json_encode($data);
    }
}