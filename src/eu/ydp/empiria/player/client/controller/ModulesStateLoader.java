package eu.ydp.empiria.player.client.controller;

import java.util.List;

import com.google.common.base.Strings;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import eu.ydp.empiria.player.client.module.IModule;
import eu.ydp.empiria.player.client.module.IStateful;
import eu.ydp.empiria.player.client.module.IUniqueModule;

public class ModulesStateLoader {

	public void setState(JSONArray state, List<IModule> modules) {
		try {
			setStateOnModules(state, modules);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setStateOnModules(JSONArray state, List<IModule> modules) {
		if (stateExists(state)) {
			JSONObject stateObj = state.isArray().get(0).isObject();

			for (int i = 0; i < modules.size(); i++) {

				IModule module = modules.get(i);
				setStateOnModule(stateObj, module);
			}
		}
	}

	private boolean stateExists(JSONArray state) {
		return state.isArray() != null && state.isArray().size() > 0;
	}

	private void setStateOnModule(JSONObject stateObj, IModule module) {
		if (moduleIsStatefulAndUnique(module)) {
			String moduleIdentifier = ((IUniqueModule) module).getIdentifier();

			if (!Strings.isNullOrEmpty(moduleIdentifier)) {

				if (stateObj.containsKey(moduleIdentifier)) {
					JSONValue moduleState = stateObj.get(moduleIdentifier);

					if (moduleStateExists(moduleState)) {
						((IStateful) module).setState(moduleState.isArray());
					}
				}
			}
		}
	}

	private boolean moduleIsStatefulAndUnique(IModule module) {
		return module instanceof IStateful && module instanceof IUniqueModule;
	}

	private boolean moduleStateExists(JSONValue moduleState) {
		return moduleState != null && moduleState.isArray() != null;
	}
}
