<?php
	/**
	 * Validator
	 * @author 	biohzrdmx <github.com/biohzrdmx>
	 * @version 2.0
	 * @license MIT
	 * @example Basic usage:
	 *
	 *     // Create a validator instance and add some rules
	 *     $validator = Validator::newInstance()
	 *         ->addRule('name', $name)
	 *         ->addRule('email', $email, 'email')
	 *         ->addRule('password', $password, 'equal')
	 *         ->addRule('confirm', $confirm, 'equal', $password)
	 *         ->validate();
	 *
	 *     // And check the result
	 *     if (! $validator->isValid() ) {
	 *         echo 'The following fields are required: ' . implode( ',', $validator->getErrors() );
	 *         exit;
	 *     }
	 *
	 */
	class Validator {

		protected $rules;
		protected $errors;

		/**
		 * Constructor
		 */
		function __construct() {
			$this->rules = array();
			$this->errors = array();
		}

		static function newInstance() {
			$new = new self();
			return $new;
		}

		/**
		 * Add a new validation rule
		 * @param string $name  Name of the rule
		 * @param mixed $value  Value of the variable that needs to be checked
		 * @param string $type  Type of rule: required, email, regex, equal, checkboxes, at least, at most
		 * @param mixed $opt    Options for the specified rule-type
		 */
		function addRule($name, $value, $type = 'required', $opt = '') {
			$this->rules[ $name ] = array(
				'value' => $value,
				'type' => $type,
				'opt' => $opt
			);
			return $this;
		}

		/**
		 * Check all the values against its associated rule type
		 * @return mixed 		TRUE if all the rules were satisfied or an array with the failed ones
		 */
		function validate() {
			$this->errors = array();
			foreach ($this->rules as $name => $rule) {
				if (! $this->checkRule( $rule ) ) {
					$this->errors[] = $name;
				}
			}
			return $this;
		}

		/**
		 * Helper function to determine whether the validation was successful or not
		 * @param  mixed  $ret 	Result of a previous call to validate()
		 * @return boolean      TRUE on success, FALSE otherwise
		 */
		function isValid() {
			return count( $this->errors ) == 0;
		}

		/**
		 * Helper function to determine whether the validation was successful or not
		 * @param  mixed  $ret 	Result of a previous call to validate()
		 * @return boolean      TRUE on success, FALSE otherwise
		 */
		function getErrors() {
			return $this->errors;
		}

		/**
		 * Check an specific rule
		 * @param  arry $rule 	The rule to check
		 * @return boolean      TRUE if the rule was satisfied, FALSE otherwise
		 */
		function checkRule($rule) {
			$ret = false;
			switch ( $rule['type'] ) {
				case 'required':
					$ret = !empty( $rule['value'] );
					break;
				case 'email':
					$pattern = '/^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/';
					$ret = preg_match($pattern, $rule['value'] ) == 1;
					break;
				case 'regex':
					$pattern = $rule['opt'];
					$ret = preg_match($pattern, $rule['value'] ) == 1;
					break;
				case 'equal':
					$ret = $rule['value'] == $rule['opt'];
					break;
				case 'checkboxes':
					$ret = is_array( $rule['value'] ) && count( $rule['value'] ) > 0;
					break;
				case 'at least':
					$ret = is_array( $rule['value'] ) && count( $rule['value'] ) >= $rule['opt'];
					break;
				case 'at most':
					$ret = is_array( $rule['value'] ) && count( $rule['value'] ) <= $rule['opt'];
					break;
			}
			return $ret;
		}

	}
?>