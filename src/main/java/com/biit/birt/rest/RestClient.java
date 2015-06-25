package com.biit.birt.rest;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.biit.birt.configuration.BirtConfigurationReader;
import com.biit.logger.BiitCommonLogger;

public class RestClient {

	public static String callRestServiceJson(String targetPath, String path, String json) {
		return get(targetPath, path, MediaType.APPLICATION_JSON, json);
	}

	public static String callRestServiceXml(String targetPath, String path, String json) {
		return get(targetPath, path, MediaType.APPLICATION_XML, json);
	}

	private static String get(String target, String path, String requestType, String json) {
		boolean ssl = target.startsWith("https");
		String responseString = null;
		BiitCommonLogger.debug(RestClient.class, "Calling rest service '" + target + "/" + path + "'");

		HttpAuthenticationFeature authenticationFeature = HttpAuthenticationFeature.basic(BirtConfigurationReader
				.getInstance().getWebServiceUser(), BirtConfigurationReader.getInstance().getWebServicePass());
		Response response = null;
		if (ssl) {
			SSLContext sslContext = SslConfigurator.newInstance(true).createSSLContext();
			response = ClientBuilder.newBuilder().sslContext(sslContext).build()
					.target(UriBuilder.fromUri(target).build()).path(path).register(authenticationFeature)
					.request(requestType).post(Entity.entity(json, MediaType.APPLICATION_JSON));
		} else {
			response = ClientBuilder.newBuilder().build().target(UriBuilder.fromUri(target).build()).path(path)
					.register(authenticationFeature).request(requestType)
					.post(Entity.entity(json, MediaType.APPLICATION_JSON));
		}
		if (response.getStatusInfo().toString().equals(Response.Status.OK.toString())) {
			responseString = response.readEntity(String.class);
		}
		BiitCommonLogger.debug(RestClient.class, "Service returns " + responseString);
		return responseString;
	}
}
