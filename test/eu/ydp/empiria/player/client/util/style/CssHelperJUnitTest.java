package eu.ydp.empiria.player.client.util.style;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("PMD")
public class CssHelperJUnitTest {

	private Map<String, String> elementStyles = null;

	private final CssHelper instance = new CssHelper();
	@Before
	public void before(){
		elementStyles = new ImmutableMap.Builder<String,String>()
					.put("background","#79C9FF;")
					.put("font-size","left")
					.put("color","white;")
					.put("margin","margin")
					.put("padding","2px 8px 4px")
					.put("display","table-cell")
					.build();
	}
	@Test
	public void shouldNotFindSimilarStyles() {
		ImmutableMap<String, String> notEquals = new ImmutableMap.Builder<String,String>()
		.put("background","79C9FF;")
		.put("font-size","right")
		.put("color","black;")
		.put("margin","4px")
		.put("padding","3px 8px 4px")
		.put("display","table")
		.build();

		for(Map.Entry<String, String> entry:notEquals.entrySet()){
			assertFalse(instance.checkIfEquals(elementStyles, entry.getKey(), entry.getValue()));
		}
	}

	@Test
	public void shouldFindSimilarStyles() {
		 ImmutableMap<String, String> equalStyles = new ImmutableMap.Builder<String,String>()
			.put("background","#79C9FF")
			.put("font-size"," left ")
			.put("color","white ")
			.put("margin","margin ")
			.put("padding"," 2px  8px  4px ")
			.put("display"," table-cell;")
			.build();

			for(Map.Entry<String, String> entry:equalStyles.entrySet()){
				assertTrue(instance.checkIfEquals(elementStyles, entry.getKey(), entry.getValue()));
			}
	}

}
