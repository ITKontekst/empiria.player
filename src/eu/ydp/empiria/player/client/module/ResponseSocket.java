package eu.ydp.empiria.player.client.module;

import eu.ydp.empiria.player.client.controller.variables.objects.response.Response;

public interface ResponseSocket {

	/** Get access to response */
	public Response getResponse(String id);
}