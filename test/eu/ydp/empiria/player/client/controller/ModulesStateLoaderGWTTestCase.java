package eu.ydp.empiria.player.client.controller;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.junit.client.GWTTestCase;

import eu.ydp.empiria.player.client.module.HasChildren;
import eu.ydp.empiria.player.client.module.IModule;
import eu.ydp.empiria.player.client.module.IStateful;
import eu.ydp.empiria.player.client.module.IUniqueModule;

public class ModulesStateLoaderGWTTestCase extends GWTTestCase {

	private ModulesStateLoader loader;

	final String FIRST_IDENTIFIER = "EDIT_RESPONSE_1";
	final String SECOND_IDENTIFIER = "EDIT_RESPONSE_2";

	final String FIRST_STATE = "[\"1\"]";
	final String SECOND_STATE = "[\"2\"]";

	private ModuleMock firstModule;

	private ModuleMock secondModule;

	private List<IModule> modules;

	@Override
	public String getModuleName() {
		return "eu.ydp.empiria.player.Player";
	}

	@Override
	protected void gwtSetUp() throws Exception {
		loader = new ModulesStateLoader();

		firstModule = new ModuleMock(FIRST_IDENTIFIER);
		secondModule = new ModuleMock(SECOND_IDENTIFIER);

		modules = Lists.newArrayList((IModule) firstModule, (IModule) secondModule);
	}

	public void testDoesntSetStateOnModulesBecauseIsNull() { // given //
		// given
		JSONArray state = null;

		// when
		loader.setState(state, modules);

		// then
		assertNull(firstModule.getState());
		assertNull(secondModule.getState());
	}

	public void testDoesntSetStateOnModulesBecauseIsEmpty() { // given //
		// given
		String stateString = "{}";

		JSONObject stateObject = getStateObjectByString(stateString);
		JSONArray state = getJSONArrayWithObject(stateObject);

		// when
		loader.setState(state, modules);

		// then
		assertNull(firstModule.getState());
		assertNull(secondModule.getState());
	}

	public void testSetStateOnFirstModuleOnly() { // given //
		// given
		// @formatter:off
				String stateString = "{\"" +
						FIRST_IDENTIFIER + "\":" + FIRST_STATE +
					"}";
		// @formatter:on

		JSONObject stateObject = getStateObjectByString(stateString);
		JSONArray state = getJSONArrayWithObject(stateObject);

		// when
		loader.setState(state, modules);

		// then
		assertEquals(stateObject.get(FIRST_IDENTIFIER).isArray(), firstModule.getState());
		assertNull(secondModule.getState());
	}

	public void testSetStateOnTwoModules() {
		// given
		// @formatter:off
				String stateString = "{\"" +
						FIRST_IDENTIFIER + "\":" + FIRST_STATE + ", \"" + 
						SECOND_IDENTIFIER + "\":" + SECOND_STATE + 
					"}";
		// @formatter:on

		JSONObject stateObject = getStateObjectByString(stateString);
		JSONArray state = getJSONArrayWithObject(stateObject);

		// when
		loader.setState(state, modules);

		// then
		assertEquals(stateObject.get(FIRST_IDENTIFIER).isArray(), firstModule.getState());
		assertEquals(stateObject.get(SECOND_IDENTIFIER).isArray(), secondModule.getState());
	}

	private JSONObject getStateObjectByString(String stateString) {
		JSONObject stateObject = (JSONObject) JSONParser.parseStrict(stateString);
		return stateObject;
	}

	private JSONArray getJSONArrayWithObject(JSONObject stateObject) {
		JSONArray state = new JSONArray();
		state.set(0, stateObject);
		return state;
	}

	public class ModuleMock implements IUniqueModule, IStateful {

		private final String identifier;
		private JSONArray newState;

		public ModuleMock(String identifier) {
			this.identifier = identifier;
		}

		@Override
		@Deprecated
		public List<IModule> getChildren() {
			return null;
		}

		@Override
		public HasChildren getParentModule() {
			return null;
		}

		@Override
		public JSONArray getState() {
			return newState;
		}

		@Override
		public void setState(JSONArray newState) {
			this.newState = newState;
		}

		@Override
		public String getIdentifier() {
			return identifier;
		}
	}
}