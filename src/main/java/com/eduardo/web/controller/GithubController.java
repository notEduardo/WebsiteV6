package com.eduardo.web.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin
public class GithubController {
	String token = System.getenv("GIT_KEY");
	String projurl = "https://api.github.com/users/notEduardo/repos";
	String readurlStart = "https://api.github.com/repos/notEduardo/";
	String readurlEnd = "/readme";
	String picsurlStart = "https://api.github.com/repos/notEduardo/";
	String picsurlEnd = "/contents/edPics";
	String repo ="";
	RestTemplate call = new RestTemplate();
	
	@GetMapping(path="/git")
    public String getGit() {
		
		JSONArray projects = new JSONArray();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "token "+token);
		HttpEntity<String> entity = new HttpEntity<>("body", headers);

		ResponseEntity<String> resp = call.exchange(projurl, HttpMethod.GET, entity, String.class);
		
		JSONParser parser = new JSONParser();
		try {
			JSONArray json = (JSONArray) parser.parse(resp.getBody());
			
			for(int i=0; i < json.size(); i++) {
				JSONObject returnO = new JSONObject();
				JSONObject o = (JSONObject) json.get(i);
				if((boolean)o.get("fork")) {
					continue;
				}
				String pName = (String) o.get("name");
				returnO.put("title", pName);
				returnO.put("link", o.get("html_url"));
				
				ResponseEntity<String> read = call.exchange(readurlStart+pName+readurlEnd, HttpMethod.GET, entity, String.class);
				JSONObject jsonRead = (JSONObject) parser.parse(read.getBody());
				ResponseEntity<String> pics = call.exchange(picsurlStart+pName+picsurlEnd, HttpMethod.GET, entity, String.class);
				JSONArray jsonPics = (JSONArray) parser.parse(pics.getBody());
				JSONArray jsonPicLinks = new JSONArray();
				
				for(Object pObj : jsonPics) {
					JSONObject jsonpObj = (JSONObject) pObj;
					jsonPicLinks.add(jsonpObj.get("download_url"));
				}
				
				returnO.put("readme", jsonRead.get("download_url"));
				returnO.put("pics", jsonPicLinks);
				
				projects.add(returnO);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return "{\"status\" : \"Error\"}";
		}
		
		return projects.toJSONString();
    }

}
