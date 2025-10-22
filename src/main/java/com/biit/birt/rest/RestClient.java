package com.biit.birt.rest;

/*-
 * #%L
 * Birt Rest Client
 * %%
 * Copyright (C) 2015 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.liferay.configuration.LiferayConfigurationReader;
import com.biit.logger.BiitCommonLogger;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class RestClient {

    private static final int KILO = 1024;

    private RestClient() {

    }

    public static String callRestServiceJson(String targetPath, String path, String json) {
        return post(targetPath, path, MediaType.APPLICATION_JSON, json);
    }

    public static String callRestServiceXml(String targetPath, String path, String json) {
        return post(targetPath, path, MediaType.APPLICATION_XML, json);
    }

    public static String callRestServiceText(String targetPath, String path, String json) {
        return post(targetPath, path, MediaType.TEXT_PLAIN, json);
    }

    public static byte[] callRestServiceGetJpgImage(String targetPath, String path, String json) {
        return postForImage(targetPath, path, "image/jpg", json);
    }

    public static byte[] callRestServiceGetPngImage(String targetPath, String path, String json) {
        return postForImage(targetPath, path, "image/png", json);
    }

    private static String post(String target, String path, String requestType, String json) {
        boolean ssl = target.startsWith("https");
        String responseString = null;

        HttpAuthenticationFeature authenticationFeature = HttpAuthenticationFeature.basic(LiferayConfigurationReader.getInstance().getUser(),
                LiferayConfigurationReader.getInstance().getPassword());
        Response response = null;
        if (ssl) {
            SSLContext sslContext = SslConfigurator.newInstance(true).createSSLContext();
            response = ClientBuilder.newBuilder().sslContext(sslContext).build().target(UriBuilder.fromUri(target).build()).path(path)
                    .register(authenticationFeature).request(requestType).post(Entity.entity(json, MediaType.APPLICATION_JSON));
        } else {
            response = ClientBuilder.newBuilder().build().target(UriBuilder.fromUri(target).build()).path(path).register(authenticationFeature)
                    .request(requestType).post(Entity.entity(json, MediaType.APPLICATION_JSON));
        }
        if (response.getStatusInfo().toString().equals(Response.Status.OK.toString())) {
            responseString = response.readEntity(String.class);
        } else {
            responseString = response.getStatusInfo().toString();
        }
        return responseString;
    }

    private static byte[] postForImage(String target, String path, String requestType, String json) {
        boolean ssl = target.startsWith("https");

        HttpAuthenticationFeature authenticationFeature = HttpAuthenticationFeature.basic(LiferayConfigurationReader.getInstance().getUser(),
                LiferayConfigurationReader.getInstance().getPassword());
        Response response = null;
        if (ssl) {
            SSLContext sslContext = SslConfigurator.newInstance(true).createSSLContext();
            response = ClientBuilder.newBuilder().sslContext(sslContext).build().target(UriBuilder.fromUri(target).build()).path(path)
                    .register(authenticationFeature).request(requestType).post(Entity.entity(json, MediaType.APPLICATION_JSON));
        } else {
            response = ClientBuilder.newBuilder().build().target(UriBuilder.fromUri(target).build()).path(path).register(authenticationFeature)
                    .request(requestType).post(Entity.entity(json, MediaType.APPLICATION_JSON));
        }
        if (response.getStatusInfo().toString().equals(Response.Status.OK.toString())) {
            InputStream result = response.readEntity(InputStream.class);
            try {
                byte[] bytes = toByteArray(result);
                return bytes;
            } catch (IOException e) {
                BiitCommonLogger.errorMessageNotification(RestClient.class, e);
            }
        }
        return null;
    }

    private static String get(String target, String path, String requestType) {
        boolean ssl = target.startsWith("https");
        String responseString = null;

        HttpAuthenticationFeature authenticationFeature = HttpAuthenticationFeature.basic(LiferayConfigurationReader.getInstance().getUser(),
                LiferayConfigurationReader.getInstance().getPassword());
        Response response = null;
        if (ssl) {
            SSLContext sslContext = SslConfigurator.newInstance(true).createSSLContext();
            response = ClientBuilder.newBuilder().sslContext(sslContext).build().target(UriBuilder.fromUri(target).build()).path(path)
                    .register(authenticationFeature).request(requestType).get();
        } else {
            response = ClientBuilder.newBuilder().build().target(UriBuilder.fromUri(target).build()).path(path).register(authenticationFeature)
                    .request(requestType).get();
        }
        if (response.getStatusInfo().toString().equals(Response.Status.OK.toString())) {
            responseString = response.readEntity(String.class);
        }
        return responseString;
    }

    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read = 0;
        byte[] bytes = new byte[KILO];

        while ((read = inputStream.read(bytes)) != -1) {
            baos.write(bytes, 0, read);
        }
        return baos.toByteArray();
    }
}
