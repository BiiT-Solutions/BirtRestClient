package com.biit.birt.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class RestClient {

	public static String callRestServiceJson(String targetPath, String path, String json) {
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(targetPath).path(path);
			String bean = target.request(MediaType.APPLICATION_JSON).post(
					Entity.entity(json, MediaType.APPLICATION_JSON), String.class);
			return bean;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String callRestServiceXml(String targetPath, String path, String json) {
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(targetPath).path(path);
			String bean = target.request(MediaType.APPLICATION_XML).post(
					Entity.entity(json, MediaType.APPLICATION_JSON), String.class);
			return bean;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String callRestService(String targetPath, String path, String json, MediaType request, MediaType send) {
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(targetPath).path(path);
			String bean = target.request(request).post(Entity.entity(json, send), String.class);
			return bean;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
