package com.appbuilders.animediapremium.Libraries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JsonBuilder {
	
	static public JSONObject stringToJson(String body) {
		
		JSONObject obj;

        try {
            obj = new JSONObject(body);
            return obj;
        } catch( Throwable t ) {
            System.out.println("Nielsen DEV -- Couldnt parse information to JSON");
            System.out.println(t);
            return null;
        }
	}
	
	static public JSONArray stringToJsonArray(String body) {
		
		JSONArray obj;

        try {
            obj = new JSONArray(body);
            return obj;
        } catch( Throwable t ) {
            System.out.println("Nielsen DEV -- Couldnt parse information to JSON");
            System.out.println(t);
            return null;
        }
	}
	
	static public String jsonToString(JSONObject obj) {
		
		String ret = "";
		ret = obj.toString();
		return ret;
	}
	
	static public String jsonToString(JSONArray array) {
		
		String ret = "";
		ret = array.toString();
		return ret;
	}

	static public JSONObject queryResultToJson(ResultSet result) throws SQLException {
		return JsonBuilder.queryResultToJson(result, false);
	}
	
	static public JSONObject queryResultToJson(ResultSet result, boolean debug) throws SQLException {
		
		String catalog_content;
		String key_name;
		JSONObject obj = new JSONObject();
		
		while( result.next() ) {

			if ( debug ) {
				System.out.println("________________ ::KEY:: ________________________");
			}
			
			key_name = result.getString("key_name");
			catalog_content = result.getString("catalog_content");
			
			if ( debug ) {
				System.out.println(key_name);
				System.out.println("________________ ::JSON:: ________________________");
			}
			
			obj = JsonBuilder.stringToJson(catalog_content);
			
			if ( debug ) {
				System.out.println(obj);
			}
		}
		
		return obj;
	}
	
	static public JSONObject queryFieldResultToJson(ResultSet result, String key) throws SQLException, IOException {
		return JsonBuilder.queryFieldResultToJson(result, key, false);
	}
	
	static public JSONObject queryFieldResultToJson(ResultSet result, String key, boolean debug) throws SQLException, IOException {
		
		String jsonField;
		JSONObject obj =  new JSONObject();
		
		if ( result == null ) {
			return obj;
		}
		
		while( result.next() ) {

			if ( debug ) {
				System.out.println("________________ ::KEY:: ________________________");
			}
			
			jsonField = result.getString(key);
			obj = JsonBuilder.stringToJson(jsonField);
			
			if ( debug ) {
				System.out.println(obj);
			}
		}
		
		return obj;
	}
	
	static public String checkType( String body ) {
		if ( body.subSequence(0, 1).equals("{") ) {
			return "object";
		}
		return "array";
	}
	
	static public String chechType( Object object ) {
		if ( object instanceof JSONArray ) {
			return "array";
		}
		return "object";
	}
	
	
	/* __________________________:: Specific methods to parse JSON A to B ::_________________________________*/
	
	static public void tranformJsonAtoB(JSONObject A) {}
	
	
	
	/* __________________________:: Specific methods to parse JSON B to A ::_________________________________*/
	
	static public void tranformJsonBtoA(JSONObject B) {}
	
	
	
	
	static public JSONObject getBaseTemplate(String instance_id, String instance_name) throws JSONException {
		
		JSONObject base = new JSONObject();
		base.put("MetricInstanceID", 1);
		base.put("MetricInstanceName", "SOVI_TEST");
		base.put("Sequence", 1);
		base.put("Reporting", "YES");
		base.put("Dependency", "NO");
		base.put("Steps", new JSONArray());
		base.put("targetFileRef", new JSONArray());
		
		return base;
	}
	
	
	static public JSONArray getSteps(JSONArray base_steps, JSONArray base_filters) throws JSONException {
		
		JSONArray steps = new JSONArray();
		
		for ( int i = 0; i < base_steps.length(); i++ ) {

			JSONObject temp_step = base_steps.getJSONObject(i).getJSONObject("step_content");
			String label = temp_step.getString("label"); 
			String stepId = temp_step.getString("stepid");
			String stepName = temp_step.getString("stepname");
			String dependency = temp_step.getString("dependency");
			String col_nm = temp_step.getString("col_nm");
			String dimention = temp_step.getString("dimention");
			String aggregation = temp_step.getString("aggregation");
			String operand = temp_step.getString("operand");
			String formulaOperand = temp_step.getString("formulaoperand");


			JSONObject correct_step = new JSONObject();
			correct_step.put("StepId", stepId);
			correct_step.put("StepName", stepName);
			correct_step.put("Sequence", "1");
			correct_step.put("Dependency", "NO");

			// Generamos el sub-objeto
			JSONObject expression = new JSONObject();
			expression.put("File", "7_raw_data_fcts_2016011.csv");
			expression.put("type", "csv");
			expression.put("Operand", operand);
			expression.put("Col_NM", col_nm);
			expression.put("Aggregation", aggregation);
			
			// Sacamos los filtros :D
			JSONArray filters = JsonBuilder.getFiltersByStepId(base_filters, stepId);
			expression.put("Data", filters);
			
			correct_step.put("Expresion", expression);
			
			steps.put(correct_step);
			
		}
		
		return steps;
	}
	
	
	static public JSONArray getFiltersByStepId(JSONArray base_filters, String pass_step_id) throws JSONException {
		
		JSONArray filters = new JSONArray();
		for ( int i = 0; i < base_filters.length(); i++ ) {

		    JSONObject temp_filter = base_filters.getJSONObject(i);
		    String step_id = temp_filter.getString("metric_step_id");

		    if ( step_id.equals(pass_step_id) ) {

		      JSONObject base_filter_key = temp_filter.getJSONObject("filter_content");
		      JSONArray base_filter = base_filter_key.getJSONArray("Filters");
		      String file = base_filter_key.getString("File");
		      String col_nm = base_filter_key.getString("Col_NM");

		      JSONObject new_filter = new JSONObject();
		      new_filter.put("File", file);
		      new_filter.put("Col_NM", col_nm);
		      new_filter.put("Filters", base_filter);

		      filters.put(new_filter);
		    }
		}

		return filters;
	}
	
	
	/* __________________________:: Specific methods to parse JSON ::_______________________________-_*/
	static public List<HashMap<String, String>> getValuesFromJsonArray(JSONObject json) {
		
		List<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
		
		
		
		return ret;
	}
}
