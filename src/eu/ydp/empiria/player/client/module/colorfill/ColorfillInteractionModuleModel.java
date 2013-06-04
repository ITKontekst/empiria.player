package eu.ydp.empiria.player.client.module.colorfill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.inject.Inject;

import eu.ydp.empiria.player.client.controller.variables.objects.response.Response;
import eu.ydp.empiria.player.client.gin.scopes.module.ModuleScoped;
import eu.ydp.empiria.player.client.module.AbstractResponseModel;
import eu.ydp.empiria.player.client.module.ResponseModelChangeListener;

public class ColorfillInteractionModuleModel extends AbstractResponseModel<String> {

	private ResponseModelChangeListener responseModelChange;

	@Inject
	public ColorfillInteractionModuleModel(@ModuleScoped Response response) {
		super(response, null);
	}
	
	public void initialize(ResponseModelChangeListener modelChangeListener){
		responseModelChange = modelChangeListener;
	}

	@Override
	protected List<String> parseResponse(Collection<String> values) {
		return new ArrayList<String>(values);
	}
	
	public Response getResponse(){
		return this.response;
	}

	@Override
	public void onModelChange(){
		responseModelChange.onResponseModelChange();
	}
	
	public void setNewUserAnswers(List<String> answers){
		response.values = answers;
	}
}